#!/system/bin/sh

# Returns 1 if internet is accessible

# bing.com is not blocked in mainland China
ping -W 1000 -c 4 www.bing.com > /dev/null 2>&1

if [ $? -eq 0 ]
then
  exit 1
else
  exit 0
fi
