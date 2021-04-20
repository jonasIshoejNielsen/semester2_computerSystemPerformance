#!/bin/bash
CLEAN=$1    #true
THREADS=$2
NUMBER_OF_CLIENTS=$3
java -jar ./jars/WoCoServer.jar 127.0.0.1 12345 true "$threads" "$NUMBER_OF_CLIENTS"