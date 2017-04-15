#!/system/bin/sh

dumpsys netstats | tr '\n' '\t' | cut -f 4 | tr '"' '\t' | cut -f 2
