#!/system/bin/sh

sh ./auto-ringer.sh &
sh ./auto-network.sh &
sh ./auto-wifi.sh &
sh ./media-scanner.sh &
sh ./move.sh &

# 1800 is the maximum reliable seconds we can sleep, otherwise the delay may
# be too significant.
sleep 1800
