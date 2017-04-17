#!/system/bin/sh

# return 1 if the device is now connecting to a google wifi

echo $WIFI_SSID | grep -i google > /dev/null 2>&1
if [ $? -eq 0 ]
then
  exit 1
else
  exit 0
fi
