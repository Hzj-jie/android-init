#!/system/bin/sh

echo in looper.sh

sh ./looper-impl.sh

# 1200 is the maximum reliable seconds we can sleep, otherwise the delay may be
# too significant.
sleep 1200
