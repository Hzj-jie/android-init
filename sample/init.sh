#!/system/bin/sh

echo Start init.sh

export SDCARD=/storage/sdcard1/
export INTERNAL=/storage/sdcard0/

# sleep 120

sh "$SDCARD/init/start-activity.sh" "com.tencent.mm" ".ui.LauncherUI"
# sh "$SDCARD/init/start-activity.sh" "com.microsoft.skydrive" ".MainActivity"
# sh "$SDCARD/init/start-activity.sh" "com.skype.polaris" "com.skype.raider.Main"

# sh "$SDCARD/init/move.sh" &
# sh "$SDCARD/init/logger.sh"

# Do not need to debug anymore
sh "$SDCARD/init/move.sh"

echo Finish init.sh
