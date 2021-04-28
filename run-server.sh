#!/bin/bash
SERVER_HOST=$1
SERVER_PORT="9000"
CLEAN=$2    #true
THREADS=$3
DOC_SIZE=$4
FILE_SUFF=$5
MIN_CLIENTS=$6
MAX_CLIENTS=$7

declare -a CLIENTS=(1 8 16)
for NUMBER_OF_CLIENTS in "${CLIENTS[@]}"
do
  for (( REPEAT=1; REPEAT<=3; REPEAT++ ))
  do
    java -jar ./jars/WoCoServer.jar $SERVER_HOST $SERVER_PORT $CLEAN "$THREADS" "$NUMBER_OF_CLIENTS" $DOC_SIZE $FILE_SUFF $REPEAT
    wait
    date +"%T"
  done
  wait
done
echo "Done"

