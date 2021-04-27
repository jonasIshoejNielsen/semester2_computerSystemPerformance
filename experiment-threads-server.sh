#!/bin/bash
SERVER_HOST=$1
MIN_CLIENTS=1
MAX_CLIENTS=16

./run-server.sh $SERVER_HOST false "0" "16" "1" $MIN_CLIENTS $MAX_CLIENTS
./run-server.sh $SERVER_HOST true "0" "16" "1" $MIN_CLIENTS $MAX_CLIENTS
for (( THREADS=1; THREADS<=1; THREADS*=2 ))
do
  ./run-server.sh $SERVER_HOST true "$THREADS" "16" "1" $MIN_CLIENTS $MAX_CLIENTS
  wait
done
wait
echo "Done"

