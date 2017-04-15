#!/system/bin/sh

sh ./process-running.sh $1
while [ $? -eq 1 ]
do
    sleep 10
    sh ./process-running.sh $1
done
