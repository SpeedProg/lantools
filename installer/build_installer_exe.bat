@echo off
call include.bat
python "%izpack2exe%" --file "%ins_jar%" --with-7z="%zip7%" --with-upx="%upx%" --output="%ins_exe%"