#!/system/bin/sh

if [ -a "./stop-auto-network" ]
then
  echo "stop-auto-network"
else
  if [ $CARRIER = "China Mobile" ]
  then
    sh ./enable-4g.sh
  else
    sh ./connect-to-google-wifi.sh
    if [ $? -eq 1 ] || [ $SIGNAL_STRENGTH -le 1 ]
    then
      sh ./enable-2g.sh
    else
      sh ./enable-3g.sh
    fi
  fi
fi
