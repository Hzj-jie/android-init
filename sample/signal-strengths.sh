#!/system/bin/sh

echo in signal-strengths.sh

# if [ "$USER_PRESENT" == "true" ]
# then
#   if [ "$WIIF_ON" == "false" ] then
#     sh ./turn-on-wifi-if-no-network.sh
#   fi
# else
#   if [ "$WIFI_ON" == "true" ] then
#     if [ "$WIFI_CONNECT" == "false" ] then
#       sh ./turn-off-wifi.sh
#     else
#       if [ "$SIGNAL_STRENGTH" -gt 3 ] then
#         sh ./turn-on-off-wifi-based-on-network-state.sh
#       fi
#     fi
#   else
#     # Only execute ping when signal strength is weak to save battery.
#     if [ "$SIGNAL_STRENGTH" -le 1 ] then
#       sh ./turn-on-wifi-if-no-network.sh
#     fi
#   fi
# fi

PREFER_WIFI=0
if [ "$SIGNAL_STRENGTH" -le 0 ]
then
  PREFER_WIFI=1
else
  sh ./connected-to-2g.sh
  if [ $? -eq 1 ]
  then
    PREFER_WIFI=1
  fi
fi

if [ "$PREFER_WIFI" -eq 1 ]
then
  sh ./prefer-2g.sh
  if [ $? -eq 0 ]
  then
    sh ./enable-2g.sh
  fi
  if [ "$WIFI_ON" == "false" ]
  then
    sh ./turn-on-wifi.sh
  else
    if [ "$WIFI_CONNECT" == "false" ]
    then
      sh ./turn-off-wifi.sh
    fi
  fi
else
  if [ "$USER_PRESENT" == "false" ]
  then
    sh ./choose-3g-4g.sh
    sh ./turn-off-wifi.sh
  fi
fi
