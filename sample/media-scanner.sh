#!/system/bin/sh

while true
do
    sleep 21600

    sh "$CURRENT/start-activity.sh" org.gemini.media_scanner .MediaScannerActivity
    sh "$CURRENT/wait-process.sh" "org.gemini.media_scanner"
done
