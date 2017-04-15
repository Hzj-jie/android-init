#!/system/bin/sh

HOUR=$(date +%H)

if [ "$HOUR" -ge 3 -a "$HOUR" -le 5 ]
then
  sh "./start-activity.sh" org.gemini.media_scanner .MediaScannerActivity
fi
