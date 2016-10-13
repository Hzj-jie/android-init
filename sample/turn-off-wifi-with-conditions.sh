#!/system/bin/sh

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

sh "$CURRENT/turn-off-wifi.sh"
