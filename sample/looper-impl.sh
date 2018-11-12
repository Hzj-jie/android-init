#!/system/bin/sh

echo in looper-impl.sh

# These actions require an Android activity, which will impact the user.
# Temporary disable this behavior: the following three commands almost not be
# able to be executed.
if [ "" != "true" ] || [ "$USER_PRESENT" != "true" ]
then
  sh ./auto-ringer.sh &
  sh ./media-scanner.sh &
fi

if [ "$MODEL" = "VTR-L29" ]
then
  sh ./move.sh &
fi
