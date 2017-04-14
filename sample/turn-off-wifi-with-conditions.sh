#!/system/bin/sh

sh ./office-hours.sh
if [ $? -eq 1 ]
then
  exit 0
fi

ps | grep com.android.vending
if [ $? -eq 0 ]
then
  exit 0
fi

ps | grep ne.lushi.lushilauncher
if [ $? -eq 0 ]
then
  exit 0
fi

ps | grep com.google.android.apps.photos
if [ $? -eq 0 ]
then
  exit 0
fi

if [ "$SIGNAL_STRENGTH" -le 1 ]
then
  exit 0
fi

if [ "$USER_PRESENT" == "true" ]
then
  exit 0
fi

sh ./turn-off-wifi.sh
