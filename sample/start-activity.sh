#!/system/bin/sh

sh ./process-running.sh $1
if [ $? -eq 0 ]
then
    am start -W --user 0 "$1/$2" &
fi
