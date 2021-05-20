#!/bin/bash
input=$1
comb=$2
getStd=$3
#awk -v combine=$comb -F '\t' 'BEGIN { OFS=FS } { gsub(/,/, "."); s1+=$1; s2+=$2 } !(FNR%combine) { print s1, s2; s1=s2=0 } END { if(s1 || s2) print s1, s2 }' "$input"
if [ "$getStd" =  true ] ;
then
    awk -v combine=$comb -F '\t' 'BEGIN { OFS=FS } { gsub(/,/, "."); s1+=$1; s2+=$2 } !(FNR%combine) { print s2; s1=s2=0 } END { if(s1 || s2) print s1, s2 }' "$input"
else
    awk -v combine=$comb -F '\t' 'BEGIN { OFS=FS } { gsub(/,/, "."); s1+=$1; s2+=$2 } !(FNR%combine) { print s1; s1=s2=0 } END { if(s1 || s2) print s1, s2 }' "$input"
fi