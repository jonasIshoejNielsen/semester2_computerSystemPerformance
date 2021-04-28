#!/bin/bash
SERVER_HOST=$1
MIN_CLIENTS=1
MAX_CLIENTS=16

./run-server.sh $SERVER_HOST false "0" "16" "1" $MIN_CLIENTS $MAX_CLIENTS
./run-server.sh $SERVER_HOST true "0" "16" "1" $MIN_CLIENTS $MAX_CLIENTS
for (( THREADS=1; THREADS<=1; THREADS*=2 ))
do
  for (( file=1; file<=2; file++ ))
  do
    ./run-server.sh $SERVER_HOST true "$THREADS" "16" $file $MIN_CLIENTS $MAX_CLIENTS
    wait
  done
  wait
done
wait
echo "Done"

