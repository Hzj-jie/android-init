#!/system/bin/sh

if [ -a "./stop-auto-ringer" ]
then
  echo "stop-auto-ringer"
  exit 1
fi

sh ./vibrate-hours.sh
if [ $? -eq 1 ]
then
  exit 0
fi

exit 1
