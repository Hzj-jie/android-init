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
            mv "$FROM_PATH/"*.mp4 "$SDCARD/DCIM/"
            mv "$FROM_PATH/"* "$SDCARD/Pictures/WeChat/"
            touch "$FROM_PATH/do.not.delete"
            rm "$SDCARD/Pictures/WeChat/do.not.delete"
        fi
    done

    mv "$i/bluetooth/Screenshot_"* "$SDCARD/Pictures/Screenshots/"
    mv "$i/bluetooth/"* "$SDCARD/Pictures/WeChat/"
    mv "$i/HDWallPaper/bigImage/"* "$SDCARD/Pictures/background images/"
    mv "$i/Pictures/OGQ/"* "$SDCARD/Pictures/background images/"
    mv "$i/Android/data/com.dv.adm/files/"* "$SDCARD/Download/"
    mv "$i/Download/email/"* "$SDCARD/Pictures/"
    mv "$i/X1-DV/XDV_media/"* "$SDCARD/DCIM/Camera/"
done

if [ "$INTERNAL" != "$SDCARD" ]
then
    mv "$INTERNAL/Download/"* "$SDCARD/Download/"
    mv "$INTERNAL/Pictures/Screenshots/"* "$SDCARD/Pictures/Screenshots/"
    mv "$INTERNAL/Sounds/"* "$SDCARD/Sounds/"
fi
sh "./copy-new.sh" "$INTERNAL/MagazineUnlock/" "$SDCARD/Pictures/MagazineUnlock/"
