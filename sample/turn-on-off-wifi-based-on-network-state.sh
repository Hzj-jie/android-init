#!/system/bin/sh

# sh ./mobile-data-accessible.sh
sh ./connected-to-2g.sh
if [ $? -eq 1 ]
then
  sh ./turn-on-wifi.sh
else
  sh ./turn-off-wifi.sh
fi
