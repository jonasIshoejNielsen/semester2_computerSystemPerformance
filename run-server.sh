#!/bin/bash
SERVER_HOST=$1
SERVER_PORT="9000"
CLEAN=$2    #true
THREADS=$3
DOC_SIZE=$4
FILE_SUFF=$5
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
    date +"%T"
    java -jar ./jars/WoCoServer.jar $SERVER_HOST $SERVER_PORT $CLEAN "$THREADS" "$NUMBER_OF_CLIENTS" $DOC_SIZE $FILE_SUFF $REPEAT $OPS
    wait
  done
  wait
done
echo "Done"

