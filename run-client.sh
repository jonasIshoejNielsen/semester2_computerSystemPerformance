#!/bin/bash
SERVER_HOST=$1
SERVER_PORT="9000"
DOC_SIZE=$2
OPS=$3
FILE_SUFF=$4
SEED=-1
CLEANNING=$5
THREADCOUNT=$6
MIN_CLIENTS=$7
MAX_CLIENTS=$8

declare -a CLIENTS=(1 4 8 12 16 32)
for NUMBER_OF_CLIENTS in "${CLIENTS[@]}"
do
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
    sleep $((20+OPS/100))
  done
  wait
  sleep 15
done
wait
echo "Done"
