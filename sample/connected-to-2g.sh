#!/system/bin/sh

# return 1 if the device is now connecting to a 2g service

sh ./connected-to-tmobile-2g.sh

if [ $? -eq 1 ]
then
  exit 1
fi

sh ./connected-to-china-mobile-2g.sh

if [ $? -eq 1 ]
then
  exit 1
fi

exit 0
