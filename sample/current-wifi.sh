#!/system/bin/sh

# Won't work
echo START current-wifi.sh
dumpsys netstats | tr '\n' '\t' | cut -f 4 | tr '"' '\t' | cut -f 2
echo END current-wifi.sh
