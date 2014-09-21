@echo off
call include.bat
mkdir out
call build_launcher4j.bat
call build_installer_jar.bat
call build_installer_exe.bat
call build_bundle_targz.bat