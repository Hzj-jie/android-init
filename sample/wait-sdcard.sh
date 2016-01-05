#!/system/bin/sh

ls /storage/sdcard1/ > /dev/null 2>&1

while [ $? != 0 ]
do
    sleep 10
    ls /storage/sdcard1/ > /dev/null 2>&1
done
