#!/bin/bash
SERVER_HOST=$1
THREADS="8"
NUMBER_OF_CLIENTS=8

for (( dsize=4; dsize<=256; dsize*=2 ))
do
  for (( file=1; file<=2; file++ ))
  do
    ./run-client.sh $SERVER_HOST $dsize $file true $THREADS "125" $NUMBER_OF_CLIENTS $NUMBER_OF_CLIENTS
    wait
  done
done
wait
echo "Done"