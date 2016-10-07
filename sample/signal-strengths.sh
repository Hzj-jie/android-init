#!/system/bin/sh

echo in signal-strengths.sh

if [ -z "$INTERNAL" ]
then
    echo No INTERNAL defined, use default value
    export INTERNAL=/storage/sdcard0/
fi

if [ -z "$SDCARD" ]
then
    echo No SDCARD defined, use default value as '$INTERNAL'
    # export SDCARD=/storage/sdcard1/
    export SDCARD=$INTERNAL
fi

if [ -z "$CURRENT" ]
then
    echo No CURRENT defined, detect current script location
    export CURRENT=$(dirname "$(readlink -f "$0")")
fi

echo LEVEL $LEVEL
