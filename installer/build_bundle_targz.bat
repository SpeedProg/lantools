@echo off
call include.bat
"%zip7%" a -ttar -so "%bundle_targz_tarname%" "../app/*" | "%zip7%" a -si "%bundle_targz%"