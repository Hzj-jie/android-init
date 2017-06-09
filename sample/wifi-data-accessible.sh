#!/system/bin/sh

# Returns 1 if internet is accessible through wifi data

sh ./interface-data-accessible.sh wlan0
exit $?
