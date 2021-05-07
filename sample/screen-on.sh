#!/system/bin/sh

echo in screen-on.sh

if [ "$MODEL" != "Pixel 4a (5G)" ]
  exit 0
fi

if [ "$MODEL" == "PE-TL10" ]
then
  sh ./set-assistant.sh
fi

# sh "./start-activity.sh" "org.gemini.round_corner" ".RoundCorner"

if [ "$WIFI_CONNECT" == "true" ]
then
  exit 0
fi

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
