#!/system/bin/sh

echo in signal-strengths.sh

echo LEVEL $LEVEL
if [ $LEVEL -le 1 ]
then
    sh ./turn-on-wifi.sh
elif [ $LEVEL -ge 3 ]
then
    sh ./turn-off-wifi-with-conditions.sh
fi
