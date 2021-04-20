#!/bin/bash
SERVER_HOST="127.0.0.1"
SERVER_PORT="12345"
DOC_SIZE=$1
REPEAT=5
FILE_SUFF="1"
numberOfClients=$2
for (( CLIENT_ID=1; CLIENT_ID<=$numberOfClients; CLIENT_ID++ ))
do
  java -jar ./jars/WoCoClient.jar $SERVER_HOST $SERVER_PORT $DOC_SIZE $REPEAT $FILE_SUFF $CLIENT_ID $numberOfClients &
done
wait
