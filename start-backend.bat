@echo off
set JAVA_HOME=C:\Users\carlos\Desktop\sts-4.32.0.RELEASE\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_21.0.8.v20250724-1412\jre
set PATH=%JAVA_HOME%\bin;%PATH%
echo Usando Java:
java -version
echo.
echo Arrancando Spring Boot...
call mvnw.cmd spring-boot:run
pause
