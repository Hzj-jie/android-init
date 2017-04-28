#!/system/bin/sh

# return 1 if the device is now connecting to t-mobile 3g service

if [ "$CARRIER" == "T-Mobile3G" ]
then
  exit 1
else
  exit 0
fi
