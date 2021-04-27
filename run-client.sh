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
for (( NUMBER_OF_CLIENTS=$MIN_CLIENTS; NUMBER_OF_CLIENTS<=$MAX_CLIENTS; NUMBER_OF_CLIENTS*=2 ))
do
  for (( REPEAT=1; REPEAT<=1; REPEAT++ ))
  do
    echo "NUMBER_OF_CLIENTS=$NUMBER_OF_CLIENTS, REPEAT=$REPEAT, threads=$THREADCOUNT, DOC_SIZE=$DOC_SIZE"
    for (( CLIENT_ID=1; CLIENT_ID<=$NUMBER_OF_CLIENTS; CLIENT_ID++ ))
    do
      java -jar ./jars/WoCoClient.jar $SERVER_HOST $SERVER_PORT $DOC_SIZE $REPEAT $FILE_SUFF $SEED $CLIENT_ID $NUMBER_OF_CLIENTS $CLEANNING $THREADCOUNT &
    done
    wait
    sleep 5
  done
  wait
done
echo "Done"
