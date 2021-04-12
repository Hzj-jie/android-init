#!/system/bin/sh

echo in screen-off.sh

# Android 11 does not allow to run am command from app anymore.
if [ "$MODEL" != "Pixel 4a (5G)" ]
  exit 0
fi

if [ "$MODEL" == "PE-TL10" ]
then
  sh "./gps-off.sh" &
fi
sh "./change-wallpaper.sh" &
sh "./auto-ringer.sh" &
# sh "./enable-2g.sh" &
sh "./looper-impl.sh" &

if [ "$MODEL" == "PE-TL10" ]
then
  sh "./clear-assistant.sh" &
  sh "./signal-strengths.sh" &
else
  sh "./turn-on-off-wifi-based-on-network-state.sh" &
fi
