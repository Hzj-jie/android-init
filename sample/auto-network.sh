#!/system/bin/sh

if [ -a "./stop-auto-network" ]
then
  echo "stop-auto-network"
else
  sh ./office-hours.sh
  OFFICE_HOUR=$?
  sh ./connect-to-google-wifi.sh
  CONNECT_TO_GOOGLE=$?
  if [ $OFFICE_HOUR -eq 1 -o $CONNECT_TO_GOOGLE -eq 1 -o $SIGNAL_STRENGTH -le 1 ]
  then
    sh ./enable-2g.sh
  else
    sh ./enable-3g.sh
  fi
fi
