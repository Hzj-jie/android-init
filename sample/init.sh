#!/system/bin/sh

echo Start init.sh

sleep 120

am start --user 0 com.tencent.mm/.ui.LauncherUI
sleep 30
am start --user 0 com.microsoft.skydrive/.MainActivity
sleep 30
am start --user 0 com.skype.polaris/com.skype.raider.Main
sleep 30

sh /sdcard/init/move_wechat_pics.sh &
sh /sdcard/init/logger.sh

echo Finish init.sh
