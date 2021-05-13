#!/bin/bash
input=$1
comb=$2
#awk -v combine=$comb -F '\t' 'BEGIN { OFS=FS } { gsub(/,/, "."); s1+=$1; s2+=$2 } !(FNR%combine) { print s1, s2; s1=s2=0 } END { if(s1 || s2) print s1, s2 }' "$input"
awk -v combine=$comb -F '\t' 'BEGIN { OFS=FS } { gsub(/,/, "."); s1+=$1; s2+=$2 } !(FNR%combine) { print s1; s1=s2=0 } END { if(s1 || s2) print s1, s2 }' "$input"

#sum1=0.0
#sum2=0.0
#i=0
#while IFS= read -r line
#do
#  i="$( bc <<<"$i + 1" )"
#  echo ${line:2:1}
#  read field1 field2 <<< ${line}
#  sum1="$( bc <<<"$sum1 + ${field1/,/.}" )"
#  sum2="$( bc <<<"$sum2 + ${field2/,/.}" )"
#  if [[ $i -ge 10 ]] ;
#  then
#    echo -e "$sum1 \t $sum2"
#    i=0
#    sum1=0.0
#    sum2=0.0
#  fi
#done < "$input"
