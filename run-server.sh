#!/bin/bash
SERVER_HOST=$1
SERVER_PORT="9000"
CLEAN=$2    #true
THREADS=$3
DOC_SIZE=$4
FILE_SUFF=$5


for ((NUMBER_OF_CLIENTS=1; NUMBER_OF_CLIENTS<=16; NUMBER_OF_CLIENTS*=2))
do
  for (( REPEAT=1; REPEAT<=1; REPEAT++ ))
  do
    java -jar ./jars/WoCoServer.jar $SERVER_HOST $SERVER_PORT $CLEAN "$THREADS" "$NUMBER_OF_CLIENTS" $DOC_SIZE $FILE_SUFF
    wait
  done
  wait
done
echo "Done"

