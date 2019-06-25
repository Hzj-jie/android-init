#!/system/bin/sh

# return 0 if out of office hours, otherwise return 1.

HOUR=$(date +%H)
MINUTE=$(date +%M)
DAY=$(date +%u)

if [ "$DAY" -ge 1 ] && [ "$DAY" -le 5 ]
then
  # working day
  if [ "$HOUR" -ge 9 ] && [ "$HOUR" -le 18 ]
  then
    exit 1
  fi
fi

exit 0
