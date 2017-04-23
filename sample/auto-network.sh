#!/system/bin/sh

if [ -a "./stop-auto-network" ]
then
  echo "stop-auto-network"
else
  # Trigger signal-strengths.sh if no signal strengths change.
  sh ./signal-strengths.sh
fi
