#!/bin/bash
SERVER_HOST=$1
MIN_CLIENTS=64
MAX_CLIENTS=128

./run-server.sh $SERVER_HOST false "0" "16" "1" $MIN_CLIENTS $MAX_CLIENTS
./run-server.sh $SERVER_HOST true "0" "16" "1" $MIN_CLIENTS $MAX_CLIENTS
./run-server.sh $SERVER_HOST true "1" "16" "1" $MIN_CLIENTS $MAX_CLIENTS
./run-server.sh $SERVER_HOST true "4" "16" "1" $MIN_CLIENTS $MAX_CLIENTS
./run-server.sh $SERVER_HOST true "8" "16" "1" $MIN_CLIENTS $MAX_CLIENTS
./run-server.sh $SERVER_HOST true "16" "16" "1" $MIN_CLIENTS $MAX_CLIENTS
./run-server.sh $SERVER_HOST false "0" "16" "2" $MIN_CLIENTS $MAX_CLIENTS
./run-server.sh $SERVER_HOST true "0" "16" "2" $MIN_CLIENTS $MAX_CLIENTS
./run-server.sh $SERVER_HOST true "1" "16" "2" $MIN_CLIENTS $MAX_CLIENTS
./run-server.sh $SERVER_HOST true "4" "16" "2" $MIN_CLIENTS $MAX_CLIENTS
./run-server.sh $SERVER_HOST true "8" "16" "2" $MIN_CLIENTS $MAX_CLIENTS
./run-server.sh $SERVER_HOST true "16" "16" "2" $MIN_CLIENTS $MAX_CLIENTS
echo "Done"