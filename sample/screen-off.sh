#!/system/bin/sh

echo in screen-off.sh

sh "./gps-off.sh" &
sh "./change-wallpaper.sh" &
sh "./auto-ringer.sh" &
# sh "./enable-2g.sh" &
sh "./signal-strength.sh" &
sh "./turn-off-wifi.sh" &
sh "./clear-assistant.sh" &
