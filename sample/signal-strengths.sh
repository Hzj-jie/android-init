#!/system/bin/sh

echo in signal-strengths.sh

if [ "$USER_PRESENT" != "true" ] && [ "$SIGNAL_STRENGTH" -le 1 ]
then
  sh ./turn-on-wifi-if-no-network.sh
fi
