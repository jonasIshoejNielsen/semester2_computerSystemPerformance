#!/bin/bash
file=$1
fileType=$2
takeSums=$3
getStd=$4

NUMBER_OF_CLIENTS=24
declare -a sizes=(4096 40960 77824 114688 151552 188416 225280 262144)
declare -a sends=(600 100 50 40 25 22 20 16)

end="ALL.txt"

echo "start"
for ((i=0;i<${#sizes[@]};i++))
do
  dSize=${sizes[$i]}
  folder="experiments-documents-server/Logsserver-clean-true-threads-8-file-$file-dSize-$dSize"
  if [ "$takeSums" = true ] ;
  then
    #awk -f merge.awk "$folder/$fileType-$NUMBER_OF_CLIENTS$end" | clip.exe
    dSend=${sends[$i]}
    combine=$((dSend*NUMBER_OF_CLIENTS*100/400))
    ../ConcatFile.sh "$folder/$fileType-$NUMBER_OF_CLIENTS$end" $combine $getStd | sed 's/\./,/' | sed 's/\./,/' | clip.exe
    echo "dSize=$dSize, dSend=$dSend"
    echo "combine=$combine"
  else
    cat "$folder/$fileType-$NUMBER_OF_CLIENTS$end" | clip.exe
  fi
  echo $((dSize/1024))
  echo ""
  sleep 2
done

