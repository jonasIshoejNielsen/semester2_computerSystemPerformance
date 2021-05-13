#!/bin/bash
SERVER_HOST=$1
SERVER_PORT="9000"
DOC_SIZE=$2
OPS=$3
FILE_SUFF=$4
SEED=-1
CLEANNING=$5
THREADCOUNT=$6
NUMBER_OF_CLIENTS=$7

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
  sleep $((30+OPS/100))
done
wait
sleep $((15+OPS/100))
echo "Done"
