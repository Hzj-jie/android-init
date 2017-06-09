#!/system/bin/sh

# Returns 1 if internet is accessible through $1

# www.bing.com is accessible within GFW.
ping -c 1 -I $1 www.bing.com > /dev/null 2>&1
RESULT=$?
if [ $RESULT -eq 0 ]
then
  exit 1
elif [ $RESULT -eq 2 ]
then
  # Operation not permitted
  ifconfig | grep $1 > /dev/null 2>&1
  if [ $? -eq 0 ]
  then
    ifconfig | grep -A 2 $1 | grep addr: > /dev/null 2>&1
    if [ $? -eq 0 ]
    then
      exit 1
    else
      exit 0
    fi
  else
    exit 0
  fi
else
  for i in {1 .. 5}
  do
    ping -c 1 -I rmnet0 www.bing.com > /dev/null 2>&1
    if [ $? -eq 0 ]
    then
      exit 1
    fi
  done
fi

exit 0
