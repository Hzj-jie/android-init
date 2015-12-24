#!/system/bin/sh

touch /storage/emulated/0/tencent/MicroMsg/WeiXin/do.not.delete

while (true);
do
    mv /storage/emulated/0/tencent/MicroMsg/WeiXin/* /storage/sdcard1/DCIM/WeChat/
    mv /storage/sdcard1/DCIM/WeChat/do.not.delete /storage/emulated/0/tencent/MicroMsg/WeiXin/
    sleep 1800
done;
