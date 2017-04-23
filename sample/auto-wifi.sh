#!/system/bin/sh

if [ -a "./stop-auto-wifi" ]
then
  echo "stop-auto-wifi"
else
  sh ./office-hours.sh
  if [ $? -eq 1 ]
  then
    sh ./turn-on-wifi.sh
  else
    sh ./turn-off-wifi-with-conditions.sh
  fi
fi
