#!/bin/bash
SERVER_HOST=$1
SERVER_PORT="9000"
DOC_SIZE=$2
FILE_SUFF=$3
SEED=-1
CLEANNING=$4
THREADCOUNT=$5
MIN_CLIENTS=$6
MAX_CLIENTS=$7

declare -a CLIENTS=(1 4 8 12 16 32 64 128)
declare -a sends=(125 125 125 125 125 125 50 25)
for ((i=0;i<${#CLIENTS[@]};i++))
do
  NUMBER_OF_CLIENTS=${CLIENTS[$i]}
  OPS=${sends[$i]}
  if [[ $NUMBER_OF_CLIENTS -lt $MIN_CLIENTS ]]; then
    continue
  fi
  if [[ $NUMBER_OF_CLIENTS -gt $MAX_CLIENTS ]]; then
    continue
  fi
  for (( REPEAT=1; REPEAT<=3; REPEAT++ ))
  do
    echo "$NUMBER_OF_CLIENTS repeat: $REPEAT"
    date +"%T"
    for (( CLIENT_ID=1; CLIENT_ID<=$NUMBER_OF_CLIENTS; CLIENT_ID++ ))
    do
      java -jar ./jars/WoCoClient.jar $SERVER_HOST $SERVER_PORT $DOC_SIZE $OPS $FILE_SUFF $SEED $CLIENT_ID $NUMBER_OF_CLIENTS $CLEANNING $THREADCOUNT $REPEAT &
    done
    wait
    echo "done, now sleep: $((20+OPS/10))"
    date +"%T"
    sleep $((20+(OPS*NUMBER_OF_CLIENTS/100)))
  done
  wait
  sleep $((15+(OPS*NUMBER_OF_CLIENTS/100)))
done
wait
echo "Done"
