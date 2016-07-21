#!/system/bin/sh

ps | grep $1 >/dev/null 2>&1
while [ $? -eq 0 ]
do
    sleep 10
    ps | grep $1 >/dev/null 2>&1
done
