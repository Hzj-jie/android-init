#!/system/bin/sh

if [ "$MODEL" == "CHM-UL00" ]
then
  am start --user 0 -d "wallpaper-changer://org.gemini.wallpaper-changer.change/?folder=$INTERNAL/MagazineUnlock" org.gemini.wallpaper_changer/.WallpaperChangerActivity &
else
  am start --user 0 -d "wallpaper-changer://org.gemini.wallpaper-changer.change/?folder=$SDCARD/Pictures/background%20images" org.gemini.wallpaper_changer/.WallpaperChangerActivity &
fi
