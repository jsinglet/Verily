@ECHO off


rem setx PATH "%1;%PATH%" /M

rem for /F "tokens=2* delims= " %%f IN ('reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v Path ^| findstr /i path') do set OLD_SYSTEM_PATH=%%g

rem setx.exe PATH "%1;%OLD_SYSTEM_PATH%;" -m

rem echo msgbox "Your Path has been updated to include the Verily Framework" > "%temp%\confirm.vbs"
wscript.exe "%temp%\confirm.vbs"



rem old path
rem %SystemRoot%\system32;%SystemRoot%;%SystemRoot%\System32\Wbem;%SYSTEMROOT%\System32\WindowsPowerShell\v1.0\;C:\Program Files\Microsoft SQL Server\110\Tools\Binn\;C:\Program Files\Microsoft\Web Platform Installer\;C:\Program Files (x86)\Microsoft ASP.NET\ASP.NET Web Pages\v1.0\;C:\Program Files (x86)\Windows Kits\8.0\Windows Performance Toolkit\;C:\Program Files (x86)\Microsoft SDKs\TypeScript\;C:\Program Files\apache-maven-3.1.1\bin;C:\Program Files\IzPack\bin