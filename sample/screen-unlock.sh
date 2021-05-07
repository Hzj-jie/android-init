#!/system/bin/sh

echo in screen-unlock.sh

if [ "$MODEL" != "Pixel 4a (5G)" ]
  exit 0
fi

sh ./gps-on.sh &
# sh ./set-assistant.sh &
