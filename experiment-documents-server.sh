#!/bin/bash
SERVER_HOST=$1
index_ops=$2
THREADS="8"
NUMBER_OF_CLIENTS=24
declare -a OPS=(600 100 50 40 25 22 20 16)
dsize=$((4+index_ops*36))

for (( file=1; file<=2; file++ ))
do
  echo "dsize=$dsizeStart, index_ops=$index_ops, OPS=${OPS[$index_ops]}, file=$file"
  ./run-documents-server.sh $SERVER_HOST true $THREADS $dsize $file ${OPS[$index_ops]} $NUMBER_OF_CLIENTS
  wait
done
echo "Done"

