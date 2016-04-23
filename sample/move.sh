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
                mv $FROM_PATH/*.mp4 $SDCARD/DCIM/
                mv $FROM_PATH/* $SDCARD/Pictures/WeChat/
                touch "$FROM_PATH/do.not.delete"
                rm "$SDCARD/Pictures/WeChat/do.not.delete"
            fi
        done
    done

    mv $SDCARD/HDWallPaper/bigImage/* $SDCARD/Pictures/background\ images/
    mv $SDCARD/Pictures/OGQ/* $SDCARD/Pictures/background\ images/

    mv $INTERNAL/Download/* $SDCARD/Download/
    mv $INTERNAL/Pictures/Screenshots/* $SDCARD/Pictures/Screenshots/
    sh $SDCARD/init/copy-new.sh $INTERNAL/MagazineUnlock/ $SDCARD/Pictures/MagazineUnlock/

    mv $SDCARD/Android/data/com.dv.adm/files/* $SDCARD/Download/

    sleep 1800
done
