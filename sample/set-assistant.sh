#!/system/bin/sh

# To use a different assistant, run:
# am start --user 0 -d "set-assistant://org.gemini.set-assistant/<intent>" "org.gemini.set_assistant/.SetAssistantActivity"
# intent example:
#  com.google.android.googlequicksearchbox/com.google.android.voiceinteraction.GsaVoiceInteractionService
am start --user 0 "org.gemini.set_assistant/.SetAssistantActivity" &
