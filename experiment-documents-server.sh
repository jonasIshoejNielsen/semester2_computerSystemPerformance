#!/bin/bash
SERVER_HOST=$1
THREADS="4"
NUMBER_OF_CLIENTS=16

for (( dsize=4; dsize<=256; dsize*=2 ))
do
  for (( file=1; file<=2; file++ ))
  do
    ./run-server.sh $SERVER_HOST true $THREADS $dsize $file "125" $NUMBER_OF_CLIENTS $NUMBER_OF_CLIENTS
    wait
  done
done
wait
echo "Done"

