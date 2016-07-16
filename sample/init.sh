#!/system/bin/sh

echo Start init.sh @ `date`

if [ -z "$SDCARD" ]
then
    export SDCARD=/storage/sdcard1/
fi

if [ -z "$INTERNAL" ]
then
    export INTERNAL=/storage/sdcard0/
fi

# sleep 120

sh "$SDCARD/init/start-activity.sh" "com.tencent.mm" ".ui.LauncherUI"
# sh "$SDCARD/init/start-activity.sh" "com.microsoft.skydrive" ".MainActivity"
# sh "$SDCARD/init/start-activity.sh" "com.skype.polaris" "com.skype.raider.Main"

# sh "$SDCARD/init/move.sh" &
# sh "$SDCARD/init/logger.sh"

cp /system/etc/hosts $SDCARD/backup/
cp /system/build.prop $SDCARD/backup/emui3.1/build.prop.changed

# sh "$SDCARD/init/power-watch.sh" &

# Do not need to debug anymore

sh "$SDCARD/init/move.sh"

echo Finish init.sh @ `date`
