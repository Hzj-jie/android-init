#!/system/bin/sh

mkdir -p "$SDCARD/Pictures/WeChat"
mkdir -p "$SDCARD/Pictures/background images"
mkdir -p "$SDCARD/Pictures/MagazineUnlock"
mkdir -p "$SDCARD/Download"

for i in "$INTERNAL" "$SDCARD" "/storage/self/primary/"
do
    for j in "WeiXin" "WeChat"
    do
        if [ -d "$i/tencent/MicroMsg/$j" ]
        then
            FROM_PATH="$i/tencent/MicroMsg/$j"
        fi
        if [ ! -z "$FROM_PATH" ]
        then
            mv "$FROM_PATH/"*.mp4 "$SDCARD/DCIM/" 1>/dev/null 2>&1
            mv "$FROM_PATH/"* "$SDCARD/Pictures/WeChat/" 1>/dev/null 2>&1
            touch "$FROM_PATH/do.not.delete"
            rm "$SDCARD/Pictures/WeChat/do.not.delete" 1>/dev/null 2>&1
        fi
    done

    mv "$i/HDWallPaper/bigImage/"* "$SDCARD/Pictures/background images/" 1>/dev/null 2>&1
    mv "$i/Pictures/OGQ/"* "$SDCARD/Pictures/background images/" 1>/dev/null 2>&1
    mv "$i/Android/data/com.dv.adm/files/"* "$SDCARD/Download/" 1>/dev/null 2>&1
    mv "$i/Download/email/"* "$SDCARD/Pictures/" 1>/dev/null 2>&1
done

if [ "$INTERNAL" != "$SDCARD" ]
then
    mv "$INTERNAL/Download/"* "$SDCARD/Download/" 1>/dev/null 2>&1
    mv "$INTERNAL/Pictures/Screenshots/"* "$SDCARD/Pictures/Screenshots/" 1>/dev/null 2>&1
fi
sh "./copy-new.sh" "$INTERNAL/MagazineUnlock/" "$SDCARD/Pictures/MagazineUnlock/" 1>/dev/null 2>&1
