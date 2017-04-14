#!/system/bin/sh

if [ -a "./stop-auto-ringer" ]
then
  echo "stop-auto-ringer"
else
  sh ./vibrate-hours.sh
  if [ $? -eq 1 ]
  then
    sh ./ringer-vibrate.sh
  else
    sh ./ringer-normal.sh
  fi
fi
