#!/system/bin/sh

# These actions require an Android activity, which will impact the user.
if [ "$USER_PRESENT" != "true" ]
then
  sh ./auto-ringer.sh &
  sh ./auto-network.sh &
  # sh ./auto-wifi.sh &
  sh ./media-scanner.sh &
fi
sh ./move.sh &

# 1200 is the maximum reliable seconds we can sleep, otherwise the delay may
# be too significant.
sleep 1200
