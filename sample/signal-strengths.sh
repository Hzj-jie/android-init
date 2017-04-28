#!/system/bin/sh

echo in signal-strengths.sh

if [ "$USER_PRESENT" != "true" ]
then
  # sh ./connect-to-google-wifi.sh
  # if [ $? -eq 1 ] || [ $SIGNAL_STRENGTH -le 1 ]
  if [ $SIGNAL_STRENGTH -le 1 ]
  then
    sh ./enable-2g.sh &
    sh ./turn-on-wifi.sh &
  else
    sh ./connected-to-tmobile-3g.sh
    if [ $? -eq 1 ]
    then
      sh ./enable-3g.sh &
    fi

    if [ $SIGNAL_STRENGTH -ge 3 ]
    then
      sh ./turn-off-wifi-with-conditions.sh &
    fi
  fi
fi
