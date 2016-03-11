#!/system/bin/sh

for i in $(ls $1)
do
    if [ ! -a "$2/$i" ]
    then
        if [ -d $1 ]
        then
            cp "$1/$i" "$2/$i"
        else
            cp "$(dirname $1)/$i" "$2/$i"
        fi
    fi
done
