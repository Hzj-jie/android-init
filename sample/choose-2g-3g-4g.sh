#!/system/bin/sh

# returns 1 if 2g is preferred, otherwise 0.

if [ "$SIGNAL_STRENGTH" -le 0 ]
then
  sh ./enable-2g.sh
  exit 1
else
  sh ./choose-3g-4g.sh
  exit 0
fi
