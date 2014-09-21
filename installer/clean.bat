@echo off
call include.bat
del %ins_jar%
del %ins_exe%
del %bundle_targz%
rmdir %out_dir%
del "..\app\LanTools.exe"