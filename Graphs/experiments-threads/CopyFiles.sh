#!/bin/bash
clean=$1
threads=$2
file=$3
fileType=$4
takeSums=$5

folder="experiments-threads-server/Logsserver-clean-$clean-threads-$threads-file-$file-dSize-16384"
end="ALL.txt"


echo "start"
declare -a CLIENTS=(1 4 8 12 16 32)
for NUMBER_OF_CLIENTS in "${CLIENTS[@]}"
do
  if [ "$takeSums" = true ] ;
  then
    #awk -f merge.awk "$folder/$fileType-$NUMBER_OF_CLIENTS$end" | clip.exe
    ../ConcatFile.sh 100 "$folder/$fileType-$NUMBER_OF_CLIENTS$end" | sed 's/\./,/' | sed 's/\./,/' | clip.exe
    echo $NUMBER_OF_CLIENTS
  else
    cat "$folder/$fileType-$NUMBER_OF_CLIENTS$end" | clip.exe
    echo $NUMBER_OF_CLIENTS
  fi
  sleep 3
done