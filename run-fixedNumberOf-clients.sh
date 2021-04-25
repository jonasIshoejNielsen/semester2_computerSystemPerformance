#!/bin/bash

for (( NUMBER_OF_CLIENTS=1; NUMBER_OF_CLIENTS<=16; NUMBER_OF_CLIENTS*=2 ))
do
  echo $NUMBER_OF_CLIENTS
  ./run-client.sh $1 $2 $3 $NUMBER_OF_CLIENTS $5 $6 $7
  sleep 3
done
wait
echo "Done"
