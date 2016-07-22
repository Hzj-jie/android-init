#!/system/bin/sh

while true
do
    sh "$SDCARD/init/start-activity.sh" org.gemini.media_scanner .MediaScannerActivity
    sh "$SDCARD/init/wait-process.sh" "org.gemini.media_scanner"

    sleep 21600
done
