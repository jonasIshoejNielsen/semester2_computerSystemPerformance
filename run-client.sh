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
declare -a CLIENTS=(1 8 16)
for NUMBER_OF_CLIENTS in "${CLIENTS[@]}"
do
  for (( REPEAT=1; REPEAT<=3; REPEAT++ ))
  do
    echo "$NUMBER_OF_CLIENTS repeat: $REPEAT"
    for (( CLIENT_ID=1; CLIENT_ID<=$NUMBER_OF_CLIENTS; CLIENT_ID++ ))
    do
      java -jar ./jars/WoCoClient.jar $SERVER_HOST $SERVER_PORT $DOC_SIZE $REPEAT $FILE_SUFF $SEED $CLIENT_ID $NUMBER_OF_CLIENTS $CLEANNING $THREADCOUNT &
    done
    wait
    sleep 5
  done
  wait
done
wait
echo "Done"
