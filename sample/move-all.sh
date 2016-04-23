#!/system/bin/sh

for i in $(ls "$1")
do
    if [ -d "$1" ]
    then
        mv "$1/$i" "$2/$i"
    else
        mv "$(dirname $1)/$i" "$2/$i"
    fi
done
