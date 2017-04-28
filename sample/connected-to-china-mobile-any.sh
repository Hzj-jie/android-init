#!/system/bin/sh

echo $CARRIER | grep -i "china mobile" > /dev/null 2>&1
if [ $? -eq 0 ]
then
  exit 1
fi

echo $CARRIER | grep -i "chinamobile" > /dev/null 2>&1
if [ $? -eq 0 ]
then
  exit 1
fi

exit 0
