
#!/bin/sh

./gradlew build
adb shell mkdir /sdcard/tmp
adb push build/outputs/apk/android-init-release.apk /sdcard/tmp/android-init-release.apk
adb shell pm install -r /sdcard/tmp/android-init-release.apk
adb shell rm /sdcard/tmp/android-init-release.apk
# adb shell am broadcast -a android.intent.action.BOOT_COMPLETED -p org.gemini.init
# adb reboot

