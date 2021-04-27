#!/bin/bash
SERVER_HOST=$1
MIN_CLIENTS=1
MAX_CLIENTS=16

./run-client.sh $SERVER_HOST "16" "1" false "0" $MIN_CLIENTS $MAX_CLIENTS
./run-client.sh $SERVER_HOST "16" "1" true "0" $MIN_CLIENTS $MAX_CLIENTS
for (( THREADS=1; THREADS<=1; THREADS*=2 ))
do
  ./run-client.sh $SERVER_HOST "16" "1" true $THREADS $MIN_CLIENTS $MAX_CLIENTS
  wait
done
wait
echo "Done"