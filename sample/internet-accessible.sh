#!/system/bin/sh

# Returns 1 if internet is accessible

# bing.com is not blocked in mainland China
for i in {1 .. 5}
do
  ping -c 1 www.bing.com > /dev/null 2>&1
  if [ $? -eq 0 ]
  then
    exit 1
  fi
done

exit 0
