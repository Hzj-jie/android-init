#!/system/bin/sh

while (true)
do
  HOUR=$(date +%H)
  MINUTE=$(date +%M)
  DAY=$(date +%u)

  if [ "$HOUR" -ge 23 -o "$HOUR" -le 7 ]
  then
    # 23:00 - 08:00 -- vibrate mode
    sh "$CURRENT/ringer-vibrate.sh"
  else
    if [ "$DAY" -ge 1 -a "$DAY" -le 5 ]
    then
      # working day
      if [ "$HOUR" -ge 8 -a "$HOUR" -le 9 ]
      then
        # 08:00 - 10:00 -- normal mode
        sh "$CURRENT/ringer-normal.sh"
      elif [ "$HOUR" -ge 10 -a "$HOUR" -le 17 ]
      then
        # 10:00 - 18:00 -- vibrate mode
        sh "$CURRENT/ringer-vibrate.sh"
      elif [ "$HOUR" -ge 18 -a "$HOUR" -le 22 ]
      then
        # 18:00 - 23:00 -- normal mode
        sh "$CURRENT/ringer-normal.sh"
      fi
    else
      # 08:00 - 23:00 -- normal mode
      sh "$CURRENT/ringer-normal.sh"
    fi
  fi

  sleep 60
done
