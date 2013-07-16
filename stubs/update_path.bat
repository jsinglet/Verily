@ECHO off


setx path %1;%PATH%

echo msgbox "Your Path has been updated to include CheckLT" > "%temp%\confirm.vbs"
wscript.exe "%temp%\confirm.vbs"
