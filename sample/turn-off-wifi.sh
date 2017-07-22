#!/system/bin/sh

if [ -a "./stop-turn-off-wifi" ]
then
  echo "stop-turn-off-wifi"
  exit 1
fi

if [ "$WIFI_CONNECT" == "true" ]
then
  if [ "$WIFI_ON" == "false" ]
  then
    exit 2
  fi

  sh ./process-running.sh com.android.vending
  if [ $? -eq 1 ]
  then
    exit 3
  fi

  sh ./process-running.sh ne.lushi.lushilauncher
  if [ $? -eq 1 ]
  then
    exit 4
  fi

  sh ./process-running.sh com.google.android.apps.photos
  if [ $? -eq 1 ]
  then
    exit 5
  fi

  sh ./process-running.sh com.theolivetree.ftpserver
  if [ $? -eq 1 ]
  then
    exit 6
  fi

  sh ./process-running.sh com.dv.adm
  if [ $? -eq 1 ]
  then
    exit 7
  fi

  sh ./process-running.sh com.icecoldapps.synchronizeultimate
  if [ $? -eq 1 ]
  then
    exit 8
  fi

  if [ "$SIGNAL_STRENGTH" -le 1 ]
  then
    exit 100
  fi

  if [ "$USER_PRESENT" == "true" ]
  then
    exit 101
  fi

  # t-mobile 3g is back to work again
  : '
  sh ./office-hours.sh
  if [ $? -eq 1 ]
  then
    exit 102
  fi

  sh ./connect-to-google-wifi.sh
  if [ $? -eq 1 ]
  then
    exit 103
  fi
  '
fi

am start --user 0 "org.gemini.wifi_onoff/.WifiOffActivity" &
