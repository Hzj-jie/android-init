#!/system/bin/sh

touch /storage/emulated/0/tencent/MicroMsg/WeiXin/do.not.delete
touch /storage/emulated/0/tencent/MicroMsg/WeChat/do.not.delete

while (true);
do
    mv /storage/emulated/0/tencent/MicroMsg/WeiXin/* /storage/sdcard1/DCIM/WeChat/
    mv /storage/sdcard1/DCIM/WeChat/do.not.delete /storage/emulated/0/tencent/MicroMsg/WeiXin/

    mv /storage/emulated/0/tencent/MicroMsg/WeChat/* /storage/sdcard1/DCIM/WeChat/
    mv /storage/sdcard1/DCIM/WeChat/do.not.delete /storage/emulated/0/tencent/MicroMsg/WeChat/

    mv /storage/sdcard1/HDWallPaper/bigImage/* '/storage/sdcard1/background images/'
    mv /storage/sdcard1/Pictures/OGQ/* '/storage/sdcard1/background images/'

    mv /storage/emulated/0/Download/* /storage/sdcard1/Download/
    
    sleep 1800
done;
