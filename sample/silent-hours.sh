#!/system/bin/sh

# return 1 if silent-mode should be used, otherwise return 0.

sh ./sleep-hours.sh

if [ $? -eq 1 ]
then
  exit 1
fi

exit 0
