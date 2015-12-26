#!/system/bin/sh

touch /storage/emulated/0/tencent/MicroMsg/WeiXin/do.not.delete

while (true);
do
    mv /storage/emulated/0/tencent/MicroMsg/WeiXin/* /storage/sdcard1/DCIM/WeChat/
    mv /storage/sdcard1/DCIM/WeChat/do.not.delete /storage/emulated/0/tencent/MicroMsg/WeiXin/

    mv /storage/sdcard1/HDWallPaper/bigImage/* '/storage/sdcard1/background images/'
    mv /storage/sdcard1/Pictures/OGQ/* '/storage/sdcard1/background images/'
    
    sleep 1800
done;
