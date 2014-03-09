@ECHO off


setlocal ENABLEEXTENSIONS
set KEY_NAME=HKEY_CURRENT_USER\Environment
set VALUE_NAME=Path

FOR /F "tokens=2*" %%A IN ('REG.exe QUERY "%KEY_NAME%" /v "%VALUE_NAME%"') DO (set oldPath=%%B)

setx PATH "%oldPath%;%~1"

echo msgbox "Your Path has been updated to include the Verily Framework at: %~1" > "%temp%\confirm.vbs"
wscript.exe "%temp%\confirm.vbs"


echo java -classpath "target\dependency\*;%~1\lib\*" VerilyMain %%* >> %1\verily.bat


rem old user path

rem C:\Ruby193\bin;C:\Users\John\AppData\Roaming\cabal\bin;C:\Program Files\Verily\;C:\texlive\2013\bin\win32;C:\Program Files\apache-ant-1.9.2\bin;C:\Program Files\Oracle\VirtualBox;C:\Program Files\emacs24\emacs-24.3\bin;C:\Program Files (x86)\Graphviz2.34\bin;C:\Users\John\AppData\Local\Pandoc\;C:\Chocolatey\bin;C:\Users\John\AppData\Roaming\npm;C:\Program Files\Verily\

rem old system path

rem C:\Users\John\AppData\Roaming\cabal\bin;C:\Program Files (x86)\Haskell\bin;C:\Program Files (x86)\Haskell Platform\2013.2.0.0\lib\extralibs\bin;C:\Program Files (x86)\Haskell Platform\2013.2.0.0\bin;%SystemRoot%\system32;%SystemRoot%;%SystemRoot%\System32\Wbem;%SYSTEMROOT%\System32\WindowsPowerShell\v1.0\;C:\Program Files\Microsoft SQL Server\110\Tools\Binn\;C:\Program Files\Microsoft\Web Platform Installer\;C:\Program Files (x86)\Microsoft ASP.NET\ASP.NET Web Pages\v1.0\;C:\Program Files (x86)\Windows Kits\8.0\Windows Performance Toolkit\;C:\Program Files (x86)\Microsoft SDKs\TypeScript\;C:\Program Files\apache-maven-3.1.1\bin;C:\Program Files\IzPack\bin;C:\Program Files (x86)\GnuWin32\bin;C:\cygwin\bin;C:\cygwin\usr\bin;C:\Program Files (x86)\Haskell Platform\2013.2.0.0\mingw\bin;C:\Program Files\Mercurial\;C:\HashiCorp\Vagrant\bin;C:\Program Files\Java\jdk1.7.0_45\bin;C:\Program Files (x86)\Windows Live\Shared;C:\Program Files (x86)\WinMerge;C:\Program Files\nodejs\;C:\Program Files\PDFtk\bin\