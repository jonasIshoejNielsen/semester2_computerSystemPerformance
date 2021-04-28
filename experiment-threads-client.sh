#!/bin/bash
SERVER_HOST=$1
MIN_CLIENTS=1
MAX_CLIENTS=16


for (( file=1; file<=2; file++ ))
do
  ./run-client.sh $SERVER_HOST "16" $file false "0" $MIN_CLIENTS $MAX_CLIENTS
  ./run-client.sh $SERVER_HOST "16" $file true "0" $MIN_CLIENTS $MAX_CLIENTS
  ./run-client.sh $SERVER_HOST "16" $file true "1" $MIN_CLIENTS $MAX_CLIENTS
  ./run-client.sh $SERVER_HOST "16" $file true "8" $MIN_CLIENTS $MAX_CLIENTS
  ./run-client.sh $SERVER_HOST "16" $file true "16" $MIN_CLIENTS $MAX_CLIENTS
  wait
done
wait
echo "Done"