set JAVA_HOME=C:\green\ojdkbuild\java-11-openjdk-11.0.4-1\
gradle build -x test
set CLASSPATH=build\libs\podcast-dl.jar;libs\ithaka-audioinfo-1.0.jar
set PATH=%JAVA_HOME%\bin;%PATH%
java bf.pd.AudioCheck 2> nul