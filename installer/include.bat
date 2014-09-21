@echo off
for /f %%i in ('java -jar ..\app\LanTools.jar --vstring') do set version=%%i
set out_dir=.\out
set ins_jar=%out_dir%\LanTools-%version%-installer.jar
set ins_exe=%out_dir%\LanTools-%version%-installer.exe
set bundle_targz=%out_dir%\LanTools-%version%.tar.gz
set bundle_targz_tarname=LanTools-%version%.tar
set izpack=C:\Program Files (x86)\IzPack
set izpack2exe=%izpack%\utils\wrappers\izpack2exe\izpack2exe.py
set upx=%izpack%\utils\wrappers\izpack2exe\upx.exe
set izcompiler=%izpack%\bin\compile.bat
set zip7=%izpack%\utils\wrappers\izpack2exe\7za.exe