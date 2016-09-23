#!/system/bin/sh

echo in screen-off.sh

# sh "$CURRENT/kill-all.sh" "com.google.android.gms"
# sh "$CURRENT/kill-all.sh" "com.google.process.gapps"
# am force-stop com.google.android.gms
# am force-stop com.google.process.gapps
# am kill com.google.android.gms
# am kill com.google.process.gapps
# pm disable com.google.android.gms
# pm enable com.google.android.gms
# pm disable com.google.process.gapps
# pm enable com.google.process.gapps

if [ -z "$CURRENT" ]
then
    echo No CURRENT defined, detect current script location
    export CURRENT=$(dirname "$(readlink -f "$0")")
fi

sh "$CURRENT/gps-off.sh" &
# sh "$CURRENT/turn-off-wifi.sh" &
sh "$CURRENT/change-wallpaper.sh" &
