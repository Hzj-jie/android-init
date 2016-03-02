#!/system/bin/sh

SDCARD=/mnt/ext_sdcard/
INTERNAL=/mnt/user/0/primary/

mkdir -p "$SDCARD/Pictures/WeChat"
mkdir -p "$SDCARD/Pictures/background images"
mkdir -p "$SDCARD/Pictures/MagazineUnlock"
mkdir -p "$SDCARD/Download"

while (true)
do
    if [ -d "$INTERNAL/tencent/MicroMsg/WeXin" ]
    then
        mv "$INTERNAL/tencent/MicroMsg/WeXin/*.mp4" "$SDCARD/DCIM/"
        mv "$INTERNAL/tencent/MicroMsg/WeiXin/*" "$SDCARD/Pictures/WeChat/"
        touch "$INTERNAL/tencent/MicroMsg/WeiXin/do.not.delete"
    fi

    if [ -d "$INTERNAL/tencent/MicroMsg/WeChat" ]
    then
        mv "$INTERNAL/tencent/MicroMsg/WeChat/*.mp4" "$SDCARD/DCIM/"
        mv "$INTERNAL/tencent/MicroMsg/WeChat/*" "$SDCARD/Pictures/WeChat/"
        touch "$INTERNAL/tencent/MicroMsg/WeChat/do.not.delete"
    fi

    rm "$SDCARD/Pictures/WeChat/do.not.delete"

    mv "$SDCARD/HDWallPaper/bigImage/*" "$SDCARD/Pictures/background images/"
    mv "$SDCARD/Pictures/OGQ/*" "$SDCARD/Pictures/background images/"

    mv "$INTERNAL/Download/*" "$SDCARD/Download/"
    mv "$INTERNAL/Pictures/Screenshots/*" "$SDCARD/Pictures/Screenshots/"
    sh "$SDCARD/init/copy-new.sh" "$INTERNAL/MagazineUnlock/" "$SDCARD/Pictures/MagazineUnlock/"

    mv "$SDCARD/Android/data/com.dv.adm/files/*" "$SDCARD/Download/"

    sleep 1800
done
