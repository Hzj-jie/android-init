#!/system/bin/sh

if [ -a "./stop-auto-network" ]
then
  echo "stop-auto-network"
else
  # Trigger signal-strengths.sh if no signal strengths change.
  # sh ./signal-strengths.sh

  if [ "$WIFI_CONNECT" == "true" ]
  then
    sh ./enable-2g.sh
  else
    if [ "$SIGNAL_STRENGTH" -le 1 ]
    then
      sh ./enable-2g.sh
      sh ./turn-on-wifi.sh
    else
      sh ./connected-to-tmobile-2g.sh
      if [ $? -eq 1 ]
      then
        if [ "$SIGNAL_STRENGTH" -ge 3 ]
        then
          sh ./enable-3g.sh
        fi
      else
        sh ./connected-to-tmobile-3g.sh
        if [ $? -eq 1 ]
        then
          sh ./enable-3g.sh
        else
          echo Unknown carrier, try 4g
          sh ./enable-4g.sh
        fi
      fi

      sh ./mobile-data-accessible.sh
      if [ $? -eq 0 ]
      then
        sh ./turn-on-wifi.sh
      else
        sh ./turn-off-wifi.sh
      fi
    fi
  fi
fi
