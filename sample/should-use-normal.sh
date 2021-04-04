#!/system/bin/sh

if [ -a "./stop-auto-ringer" ]
then
  echo "stop-auto-ringer"
  exit 1
fi

sh ./should-use-silent.sh
if [ $? -eq 0 ]
then
  exit 1
fi

sh ./should-use-vibrate.sh
if [ $? -eq 0 ]
then
  exit 1
fi

exit 0
