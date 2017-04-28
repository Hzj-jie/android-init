#!/bin/sh

rm .*.un~
for i in $(ls -1 *.sh)
do
  if [ $i != "push.sh" ]
  then
    echo $i '==>'
    adb push $i /sdcard/init/
  fi
done
