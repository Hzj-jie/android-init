#!/system/bin/sh

echo Start init.sh

sleep 120

am start -W --user 0 com.tencent.mm/.ui.LauncherUI
am start -W --user 0 com.microsoft.skydrive/.MainActivity
am start -W --user 0 com.skype.polaris/com.skype.raider.Main

sh /storage/sdcard1/init/move.sh &
sh /storage/sdcard1/init/logger.sh

echo Finish init.sh
