#!/system/bin/sh

echo in screen-off.sh

# sh "./kill-all.sh" "com.google.android.gms"
# sh "./kill-all.sh" "com.google.process.gapps"
# am force-stop com.google.android.gms
# am force-stop com.google.process.gapps
# am kill com.google.android.gms
# am kill com.google.process.gapps
# pm disable com.google.android.gms
# pm enable com.google.android.gms
# pm disable com.google.process.gapps
# pm enable com.google.process.gapps

sh "./gps-off.sh" &
# sh "./turn-off-wifi.sh" &
sh "./change-wallpaper.sh" &
sh "./auto-ringer.sh" &
sh "./auto-network.sh" &
sh "./auto-wifi.sh" &
