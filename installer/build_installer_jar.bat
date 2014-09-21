@echo off
call include.bat
call "%izcompiler%" izpack_installer.xml -o "%ins_jar%"
echo "finished building jar installer"