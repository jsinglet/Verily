@ECHO OFF

java -jar "%~dp0\lib\checkers.jar" -classpath "%~dp0\lib\checker-lattice-tainting-plugin.jar" -processor checkers.latticetainting.LatticeTaintingChecker %*
