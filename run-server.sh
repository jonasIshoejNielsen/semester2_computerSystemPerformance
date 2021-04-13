#!/bin/bash
numberOfClients=$1
threads=$2
java -jar ./jars/WoCoServer.jar 127.0.0.1 12345 "$numberOfClients" true "$threads"