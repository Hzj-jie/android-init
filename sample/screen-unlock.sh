#!/system/bin/sh

echo in screen-unlock.sh

if [ -z "$CURRENT" ]
then
    echo No CURRENT defined, detect current script location
    export CURRENT=$(dirname "$(readlink -f "$0")")
fi

sh "$CURRENT/gps-on.sh"
