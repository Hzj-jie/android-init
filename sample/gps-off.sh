#!/system/bin/sh

am start --user 0 "org.gemini.gps_onoff/.GpsOffActivity" &
am start --user 0 "org.gemini.gps_onoff/.WifiScanOffActivity" &
am start --user 0 "org.gemini.gps_onoff/.BluetoothScanOffActivity" &
