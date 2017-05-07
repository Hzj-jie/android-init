#!/system/bin/sh

echo in signal-strengths.sh

if [ "$USER_PRESENT" != "true" ]
then
  if [ "$SIGNAL_STRENGTH" -le 1 ]
  then
    sh ./enable-2g.sh
  else
    sh ./connected-to-tmobile-2g.sh
    if [ $? -eq 1 ]
    then
      if [ "$SIGNAL_STRENGTH" -ge 3 ]
      then
        sh ./enable-3g.sh
      fi
    else
      sh ./connected-to-tmobile-3g.sh
      if [ $? -eq 1 ]
      then
        sh ./enable-3g.sh
      else
        echo Unknown carrier, try 4g
        sh ./enable-4g.sh
      fi
    fi
  fi
fi
