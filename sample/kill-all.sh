#!/system/bin/sh

echo kill-all.sh with parameter "$1"
ps | grep "$1" | sed 's/ \+/ /g' | \
while read i
do
    j=$(echo "$i" | cut -f 2 -d ' ')
    echo will kill $i with process id $j
    kill $j
done
