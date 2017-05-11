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

  if [ "$SIGNAL_STRENGTH" -ge 3 ]
  then
    sh ./turn-off-wifi.sh
  else
    sh ./mobile-data-accessible.sh
    if [ $? -eq 0 ]
    then
      sh ./turn-on-wifi.sh
    else
      sh ./turn-off-wifi.sh
    fi
  fi
fi
