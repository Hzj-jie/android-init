#!/system/bin/sh

if [ "$WIFI_ON" == "true" ] || [ "$1" == "force" ]
then
  exit 0
fi

am start --user 0 "org.gemini.wifi_onoff/.WifiOnActivity"
