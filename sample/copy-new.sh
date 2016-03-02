#!/system/bin/sh

for i in $(ls "$1")
do
    if [ ! -a "$2/$i" ]
    then
        cp "$1/$i" "$2/$i"
    fi
done
