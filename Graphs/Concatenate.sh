#!/bin/bash
clean=$1
threads=$2
file=$3

folder="experiments-threads-server/Logsserver-clean-$clean-threads-$threads-file-$file-dSize-16384"
end="ALL.txt"


declare -a CLIENTS=(1 4 8 12 16 32)
for NUMBER_OF_CLIENTS in "${CLIENTS[@]}"
do
  echo $NUMBER_OF_CLIENTS
  cat "$folder/InServer_Tput-$NUMBER_OF_CLIENTS$end" | clip.exe
  sleep 5
done

sleep 15