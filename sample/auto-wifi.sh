#!/system/bin/sh

if [ -a "./stop-auto-wifi" ]
then
  echo "stop-auto-wifi"
else
  : '
  sh ./office-hours.sh
  if [ $? -eq 1 ]
  then
    sh ./turn-on-wifi.sh
  else
    sh ./turn-off-wifi.sh
  fi
  '

  sh ./turn-off-wifi.sh
  sh ./internet-accessible.sh
  if [ $? -eq 0 ]
  then
    sh ./turn-on-wifi.sh
  fi
fi
