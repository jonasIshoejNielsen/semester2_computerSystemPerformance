NF{ 
    if(i>=10){
        line = line sum1 "\t" sum2 "\n";
        i=0;
        sum1=0.0;
        sum2=0.0;
    }else{
        line = line " " $0;
        i++;
    }
}

END{
    print line;
}