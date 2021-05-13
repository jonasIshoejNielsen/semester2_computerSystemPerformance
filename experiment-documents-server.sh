#!/bin/bash
SERVER_HOST=$1
index_ops=$2
THREADS="8"
NUMBER_OF_CLIENTS=24
declare -a OPS=(400 80 40 35 22 20 18 15)
dsize=$((4+index_ops*36))

for (( file=1; file<=2; file++ ))
do
  echo "dsize=$dsizeStart, index_ops=$index_ops, OPS=${OPS[$index_ops]}, file=$file"
  ./run-documents-server.sh $SERVER_HOST true $THREADS $dsize $file ${OPS[$index_ops]} $NUMBER_OF_CLIENTS
  wait
done
echo "Done"

