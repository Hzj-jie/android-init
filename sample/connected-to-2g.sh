#!/system/bin/sh

# return 1 if the device is now connecting to a 2g service

if [ "$DATA_NETWORK_CLASS" == "2" ]
then
  exit 1
fi

if [ "$DATA_NETWORK_CLASS" == "0" ]
then
  exit 1
fi

exit 0
