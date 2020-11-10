#!/system/bin/sh

if [ -a "./stop-auto-ringer" ]
then
  echo "stop-auto-ringer"
  exit 0
fi

sh ./silent-hours.sh
if [ %? -eq 1 ]
then
  sh ./ringer-silent.sh
  exit 0
fi

sh ./vibrate-hours.sh
if [ $? -eq 1 ]
then
  sh ./ringer-vibrate.sh
  exit 0
fi

sh ./ringer-normal.sh
