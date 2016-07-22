#!/system/bin/sh

mkdir -p "$SDCARD/Pictures/WeChat"
mkdir -p "$SDCARD/Pictures/background images"
mkdir -p "$SDCARD/Pictures/MagazineUnlock"
mkdir -p "$SDCARD/Download"

while (true)
do
    for i in "$INTERNAL" "$SDCARD"
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
    done

    mv "$SDCARD/HDWallPaper/bigImage/"* "$SDCARD/Pictures/background images/" 1>/dev/null 2>&1
    mv "$SDCARD/Pictures/OGQ/"* "$SDCARD/Pictures/background images/" 1>/dev/null 2>&1

    mv "$INTERNAL/Download/"* "$SDCARD/Download/" 1>/dev/null 2>&1
    mv "$INTERNAL/Pictures/Screenshots/"* "$SDCARD/Pictures/Screenshots/" 1>/dev/null 2>&1
    sh "$SDCARD/init/copy-new.sh" "$INTERNAL/MagazineUnlock/" "$SDCARD/Pictures/MagazineUnlock/" 1>/dev/null 2>&1

    mv "$SDCARD/Android/data/com.dv.adm/files/"* "$SDCARD/Download/" 1>/dev/null 2>&1

    sleep 1800
done
