#!/system/bin/sh

while (true)
do
  HOUR=$(date +%H)
  MINUTE=$(date +%M)

  if [ "$HOUR" -eq 23 -a "$MINUTE" -ge 0 -a "$MINUTE" -le 30 ]
  then
    # 23:00 - 08:00 -- vibrate mode
    sh "$CURRENT/ringer-vibrate.sh"
  elif [ "$HOUR" -eq 8 -a "$MINUTE" -ge 0 -a "$MINUTE" -le 30 ]
  then
    # 08:00 - 10:29 -- normal mode
    sh "$CURRENT/ringer-normal.sh"
  elif [ "$HOUR" -eq 10 -a "$MINUTE" -ge 29 -a "$MINUTE" -le 59 ]
  then
    # 10:29 - 18:29 -- vibrate mode
    sh "$CURRENT/ringer-vibrate.sh"
  elif [ "$HOUR" -eq 18 -a "$MINUTE" -ge 29 -a "$MINUTE" -le 59 ]
  then
    # 18:29 - 23:00 -- normal mode
    sh "$CURRENT/ringer-normal.sh"
  fi

  sleep 900
done
