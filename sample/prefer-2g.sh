#!/system/bin/sh

# return 1 if current preferred network mode is 2g

settings get global preferred_network_mode | grep 1 >/dev/null 2>&1
if [ $? -eq 0 ]
then
  exit 1
else
  exit 0
fi
