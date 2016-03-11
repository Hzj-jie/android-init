#!/system/bin/sh

ls "$SDCARD" > /dev/null 2>&1

while [ $? != 0 ]
do
    sleep 10
    ls "$SDCARD" > /dev/null 2>&1
done
