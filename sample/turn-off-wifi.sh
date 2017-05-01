#!/system/bin/sh

if [ -a "./stop-turn-off-wifi" ]
then
  echo "stop-turn-off-wifi"
  exit 0
fi

if [ "$WIFI_ON" == "false" ]
then
  exit 0
fi

sh ./process-running.sh com.android.vending
if [ $? -eq 1 ]
then
  exit 0
fi

sh ./process-running.sh ne.lushi.lushilauncher
if [ $? -eq 1 ]
then
  exit 0
fi

sh ./process-running.sh com.google.android.apps.photos
if [ $? -eq 1 ]
then
  exit 0
fi

sh ./process-running.sh com.theolivetree.ftpserver
if [ $? -eq 1 ]
then
  exit 0
fi

sh ./process-running.sh com.dv.adm
if [ $? -eq 1 ]
then
  exit 0
fi

if [ "$SIGNAL_STRENGTH" -le 1 ]
then
  exit 0
fi

if [ "$USER_PRESENT" == "true" ]
then
  exit 0
fi

# t-mobile 3g is back to work again
: '
sh ./office-hours.sh
if [ $? -eq 1 ]
then
  exit 0
fi

sh ./connect-to-google-wifi.sh
if [ $? -eq 1 ]
then
  exit 0
fi
'

am start --user 0 "org.gemini.wifi_onoff/.WifiOffActivity"
