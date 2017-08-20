#!/system/bin/sh

echo in screen-on.sh

sh ./set-assistant.sh

if [ "$WIFI_CONNECT" == "true" ]
then
  exit 0
else
#  if [ "$WIFI_ON" == "true" ]
#  then
#    # No WIFI
#    sh ./turn-off-wifi.sh
#    sh ./choose-3g-4g.sh
#  else
#    if [ "$SIGNAL_STRENGTH" -le 1 ]
#    then
#      sh ./enable-2g.sh
#      sh ./turn-on-wifi.sh
#    else
#      sh ./choose-3g-4g.sh
#    fi
#  fi
  # Ensures signal-strengths takes effect.
  USER_PRESENT=false
  sh ./signal-strengths.sh
fi
