#!/bin/bash
clean=$1
threads=$2
file=$3
fileType=$4
takeSums=$5
getStd=$6

folder="experiments-threads-server/Logsserver-clean-$clean-threads-$threads-file-$file-dSize-16384"
end="ALL.txt"


echo "start"
sleep 1
declare -a CLIENTS=(1 4 8 12 16 32 64 128)
for NUMBER_OF_CLIENTS in "${CLIENTS[@]}"
do
  sleep 1
  if [ "$takeSums" = true ] ;
  then
    #awk -f merge.awk "$folder/$fileType-$NUMBER_OF_CLIENTS$end" | clip.exe
    ../ConcatFile.sh "$folder/$fileType-$NUMBER_OF_CLIENTS$end" 10 $getStd | sed 's/\./,/' | sed 's/\./,/' | clip.exe
    echo $NUMBER_OF_CLIENTS
  else
    ../ConcatFile.sh "$folder/$fileType-$NUMBER_OF_CLIENTS$end" 1 $getStd | sed 's/\./,/' | sed 's/\./,/' | clip.exe
    echo $NUMBER_OF_CLIENTS
  fi
done