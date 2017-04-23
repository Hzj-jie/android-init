#!/system/bin/sh

# return 1 if the device is now connecting to t-mobile 3g service

echo $CARRIER | grep -i t-mobile3g > /dev/null 2>&1
if [ $? -eq 0 ]
then
  exit 1
else
  exit 0
fi
