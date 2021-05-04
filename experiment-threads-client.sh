#!/bin/bash
SERVER_HOST=$1
MIN_CLIENTS=1
MAX_CLIENTS=32

./run-client.sh $SERVER_HOST "16" "125" "1" false "0" $MIN_CLIENTS $MAX_CLIENTS
./run-client.sh $SERVER_HOST "16" "125" "1" true "0" $MIN_CLIENTS $MAX_CLIENTS
./run-client.sh $SERVER_HOST "16" "125" "1" true "1" $MIN_CLIENTS $MAX_CLIENTS
./run-client.sh $SERVER_HOST "16" "125" "1" true "8" $MIN_CLIENTS $MAX_CLIENTS
./run-client.sh $SERVER_HOST "16" "125" "1" true "16" $MIN_CLIENTS $MAX_CLIENTS
./run-client.sh $SERVER_HOST "16" "125" "2" false "0" $MIN_CLIENTS $MAX_CLIENTS
./run-client.sh $SERVER_HOST "16" "125" "2" true "0" $MIN_CLIENTS $MAX_CLIENTS
./run-client.sh $SERVER_HOST "16" "125" "2" true "1" $MIN_CLIENTS $MAX_CLIENTS
./run-client.sh $SERVER_HOST "16" "125" "2" true "8" $MIN_CLIENTS $MAX_CLIENTS
./run-client.sh $SERVER_HOST "16" "125" "2" true "16" $MIN_CLIENTS $MAX_CLIENTS
echo "Done"