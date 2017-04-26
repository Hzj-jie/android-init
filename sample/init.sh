#!/system/bin/sh

echo Start init.sh @ `date`

# sleep 120

sh "./start-activity.sh" "com.tencent.mm" ".ui.LauncherUI"
sh "./start-activity.sh" "org.gemini.round_corner" ".RoundCorner"
# sh "./start-activity.sh" "com.microsoft.skydrive" ".MainActivity"
# sh "./start-activity.sh" "com.skype.polaris" "com.skype.raider.Main"

# sh "./move.sh" &
# sh "./logger.sh"

cp /system/etc/hosts $SDCARD/backup/
cp /system/build.prop $SDCARD/backup/emui4.0/build.prop.changed

# sh "./power-watch.sh" &

# Do not need to debug anymore

echo Finish init.sh @ `date`
