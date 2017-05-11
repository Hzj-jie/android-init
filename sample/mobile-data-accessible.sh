#!/system/bin/sh

# Returns 1 if internet is accessible through mobile data

sh ./interface-data-accessible.sh rmnet0
exit $?
