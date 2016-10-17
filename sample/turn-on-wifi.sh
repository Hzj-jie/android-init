#!/system/bin/sh

if [ "$WIFI_ON" == "true" ]
then
  exit 0
fi

am start --user 0 "org.gemini.wifi_onoff/.WifiOnActivity"
