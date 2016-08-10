#!/system/bin/sh

while true
do
    sh "$CURRENT/start-activity.sh" org.gemini.media_scanner .MediaScannerActivity
    sh "$CURRENT/wait-process.sh" "org.gemini.media_scanner"

    sleep 21600
done
