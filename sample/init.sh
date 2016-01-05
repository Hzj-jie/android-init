#!/system/bin/sh

echo Start init.sh

sleep 120

sh /storage/sdcard1/init/start-activity.sh "com.tencent.mm" ".ui.LauncherUI"
sh /storage/sdcard1/init/start-activity.sh "com.microsoft.skydrive" ".MainActivity"
sh /storage/sdcard1/init/start-activity.sh "com.skype.polaris" "com.skype.raider.Main"

# sh /storage/sdcard1/init/move.sh &
# sh /storage/sdcard1/init/logger.sh

# Do not need to debug anymore
sh /storage/sdcard1/init/move.sh

echo Finish init.sh
