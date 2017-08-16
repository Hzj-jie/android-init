#!/system/bin/sh

sh ./connected-to-tmobile.sh
if [ $? -eq 1 ]
then
  sh ./enable-3g.sh
else
  echo Unknown carrier, try 4g
  sh ./enable-4g.sh
fi
