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
  ./run-documents-client.sh $SERVER_HOST $dsize ${OPS[$index_ops]} $file true $THREADS $NUMBER_OF_CLIENTS
  wait
done
echo "Done"