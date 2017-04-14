#!/system/bin/sh

if [ -a "./stop-auto-network" ]
then
  echo "stop-auto-network"
else
  sh ./office-hours.sh
  if [ $? -eq 1 ]
  then
    sh ./enable-2g.sh
  else
    sh ./enable-3g.sh
  fi
fi
