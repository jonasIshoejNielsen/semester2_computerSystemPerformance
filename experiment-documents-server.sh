#!/bin/bash
SERVER_HOST=$1
index_ops=$2
THREADS="4"
NUMBER_OF_CLIENTS=16
declare -a OPS=(300 90 55 36 25 21 19 16)
dsize=$((4+index_ops*36))

for (( file=1; file<=2; file++ ))
do
  echo "dsize=$dsizeStart, index_ops=$index_ops, OPS=${OPS[$index_ops]}, file=$file"
  ./run-server.sh $SERVER_HOST true $THREADS $dsize $file ${OPS[$index_ops]} $NUMBER_OF_CLIENTS $NUMBER_OF_CLIENTS
  wait
done
echo "Done"

