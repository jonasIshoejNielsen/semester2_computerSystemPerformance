#!/bin/bash
SERVER_HOST="127.0.0.1"
SERVER_PORT="12345"
DOC_SIZE="10"
REPEAT="4"
FILE_SUFF="1"
#java -jar ./jars/WoCoClient.jar 127.0.0.1 12345 10 1 1 2
for numberOfClients in 1 2 3 4 8 12 16 24 32 64 128 256
do
	for (( CLIENT_ID=1; CLIENT_ID<=$numberOfClients; CLIENT_ID++ ))
  do
    #echo "$CLIENT_ID / $numberOfClients"
    java -jar ./jars/WoCoClient.jar $SERVER_HOST $SERVER_PORT $DOC_SIZE $REPEAT $FILE_SUFF $CLIENT_ID $numberOfClients &
  done
  wait
done
wait