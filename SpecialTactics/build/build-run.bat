@echo on
call build-config.bat
call %ANT_PATH% -f build.xml
%JAVA_X86_PATH% -jar SpecialTactics.jar