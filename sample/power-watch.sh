#!/system/bin/sh

while (true)
do
  echo dumpsys power @ `date`
  dumpsys power
  sleep 1800
done
