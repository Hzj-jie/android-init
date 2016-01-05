#!/system/bin/sh

ps "$1" | grep "$1" > /dev/null 2>&1
if [ $? != 0 ]
then
    am start -W --user 0 "$1/$2"
fi
