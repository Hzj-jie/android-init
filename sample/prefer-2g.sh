#!/system/bin/sh

# return 1 if current preferred network mode is 2g

if [ "$PREFERRED_NETWORK_TYPE" -eq "1" ]
then
  exit 1
else
  exit 0
fi
