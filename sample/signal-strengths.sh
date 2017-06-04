#!/system/bin/sh

echo in signal-strengths.sh

if [ "$USER_PRESENT" == "true" ]
then
  sh ./auto-network.sh
fi
