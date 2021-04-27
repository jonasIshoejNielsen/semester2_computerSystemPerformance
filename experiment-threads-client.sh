#!/bin/bash
SERVER_HOST=$1

./run-client.sh $SERVER_HOST "16" "1" false "0"
./run-client.sh $SERVER_HOST "16" "1" true "0" 
for (( THREADS=1; THREADS<=16; THREADS*=2 ))
do
  ./run-client.sh $SERVER_HOST "16" "1" true $THREADS
  wait
done
wait
echo "Done"