#!/system/bin/sh

mkdir -p /storage/sdcard1/Pictures/WeChat
mkdir -p '/storage/sdcard1/Pictures/background images/'
mkdir -p /storage/sdcard1/Pictures/MagazineUnlock/

while (true)
do
    if [ -d /storage/emulated/0/tencent/MicroMsg/WeXin ]
    then
        mv /storage/emulated/0/tencent/MicroMsg/WeXin/*.mp4 /storage/sdcard1/DCIM/
        mv /storage/emulated/0/tencent/MicroMsg/WeiXin/* /storage/sdcard1/Pictures/WeChat/
        touch /storage/emulated/0/tencent/MicroMsg/WeiXin/do.not.delete
    fi

    if [ -d /storage/emulated/0/tencent/MicroMsg/WeChat ]
    then
        mv /storage/emulated/0/tencent/MicroMsg/WeChat/*.mp4 /storage/sdcard1/DCIM/
        mv /storage/emulated/0/tencent/MicroMsg/WeChat/* /storage/sdcard1/Pictures/WeChat/
        touch /storage/emulated/0/tencent/MicroMsg/WeChat/do.not.delete
    fi

    rm /storage/sdcard1/Pictures/WeChat/do.not.delete

    mv /storage/sdcard1/HDWallPaper/bigImage/* '/storage/sdcard1/Pictures/background images/'
    mv /storage/sdcard1/Pictures/OGQ/* '/storage/sdcard1/Pictures/background images/'

    mv /storage/emulated/0/Download/* /storage/sdcard1/Download/
    mv /storage/emulated/0/Pictures/Screenshots/* /storage/sdcard1/Pictures/Screenshots/
    sh /storage/sdcard1/init/copy-new.sh /storage/emulated/0/MagazineUnlock/ /storage/sdcard1/Pictures/MagazineUnlock/

    sleep 1800
done
