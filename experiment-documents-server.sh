#!/bin/bash
SERVER_HOST=$1
THREADS="4"
NUMBER_OF_CLIENTS=16
declare -a OPS=(300 90 55 36 25 21 19 16)
index_ops=0
for (( dsize=4; dsize<=256; dsize+=36 ))
do
  echo "dsize=$dsizeStart, index_ops=$index_ops, OPS=${OPS[$index_ops]}"
  for (( file=1; file<=2; file++ ))
  do
    ./run-server.sh $SERVER_HOST true $THREADS $dsize $file ${OPS[$index_ops]} $NUMBER_OF_CLIENTS $NUMBER_OF_CLIENTS
    wait
  done
  index_ops=$((index_ops+1))
done
wait
echo "Done"

