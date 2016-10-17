#!/system/bin/sh

if [ "$WIFI_ON" == "false" ]
then
  exit 0
fi

am start --user 0 "org.gemini.wifi_onoff/.WifiOffActivity"
