#!/bin/bash
SERVER_HOST=$1

./run-server.sh $SERVER_HOST false "0" "16" "1"
./run-server.sh $SERVER_HOST true "0" "16" "1"
for (( THREADS=1; THREADS<=16; THREADS*=2 ))
do
  ./run-server.sh $SERVER_HOST true "$THREADS" "16" "1"
  wait
done
wait
echo "Done"

