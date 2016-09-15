#!/system/bin/sh

echo Start init.sh @ `date`

if [ -z "$INTERNAL" ]
then
    echo No INTERNAL defined, use default value
    export INTERNAL=/storage/sdcard0/
fi

if [ -z "$SDCARD" ]
then
    echo No SDCARD defined, use default value as '$INTERNAL'
    # export SDCARD=/storage/sdcard1/
    export SDCARD=$INTERNAL
fi

if [ -z "$CURRENT" ]
then
    echo No CURRENT defined, detect current script location
    export CURRENT=$(dirname "$(readlink -f "$0")")
fi

echo SDCARD=$SDCARD, INTERNAL=$INTERNAL, CURRENT=$CURRENT

# sleep 120

sh "$CURRENT/start-activity.sh" "com.tencent.mm" ".ui.LauncherUI"
# sh "$CURRENT/start-activity.sh" "com.microsoft.skydrive" ".MainActivity"
# sh "$CURRENT/start-activity.sh" "com.skype.polaris" "com.skype.raider.Main"

# sh "$CURRENT/move.sh" &
# sh "$CURRENT/logger.sh"

cp /system/etc/hosts $SDCARD/backup/
cp /system/build.prop $SDCARD/backup/emui4.0/build.prop.changed

# sh "$CURRENT/power-watch.sh" &

# Do not need to debug anymore

sh "$CURRENT/media-scanner.sh" &
sh "$CURRENT/auto-ringer.sh" &
sh "$CURRENT/move.sh"

echo Finish init.sh @ `date`
