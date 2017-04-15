#!/system/bin/sh

# return 1 if process $1 is running, otherwise 0.

# In Android, ps won't output command line of grep
ps | grep $1 > /dev/null 2>&1

if [ $? -eq 0 ]
then
  exit 1
else
  exit 0
fi
