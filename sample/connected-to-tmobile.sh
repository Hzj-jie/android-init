#!/system/bin/sh

# return 1 if the device is now connecting to t-mobile service
sh ./connected-to-tmobile-2g.sh
if [ $? -eq 1 ]
then
  exit 1
else
  sh ./connected-to-tmobile-3g.sh
  if [ $? -eq 1 ]
  then
    exit 1
  fi
fi

exit 0
