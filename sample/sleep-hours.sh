#!/system/bin/sh

# return 0 if out of sleep hours, otherwise return 1

HOUR=$(date +%H)
MINUTE=$(date +%M)
DAY=$(date +%u)

if [ "$HOUR" -ge 23 -o "$HOUR" -le 7 ]
then
  # 23:00 - 08:00 -- vibrate mode
  exit 1
fi

exit 0
