#!/bin/bash
SERVER_HOST="127.0.0.1"
SERVER_PORT="12345"
CLEAN=$1    #true
THREADS=$2
NUMBER_OF_CLIENTS=$3
java -jar ./jars/WoCoServer.jar $SERVER_HOST $SERVER_PORT $CLEAN "$THREADS" "$NUMBER_OF_CLIENTS"