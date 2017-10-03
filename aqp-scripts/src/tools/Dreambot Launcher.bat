@Title Dreambot Launcher 1.0
@echo off
color 0a

ECHO    ___                     __        __    __                      __          
ECHO   / _ \_______ ___ ___ _  / /  ___  / /_  / /  ___ ___ _____  ____/ /  ___ ____
ECHO  / // / __/ -_) _ `/  ' \/ _ \/ _ \/ __/ / /__/ _ `/ // / _ \/ __/ _ \/ -_) __/
ECHO /____/_/  \__/\_,_/_/_/_/_.__/\___/\__/ /____/\_,_/\_,_/_//_/\__/_//_/\__/_/                                                                             
ECHO Version 1.0 @A q p
ECHO[      
IF EXIST "DBLauncher.jar" (
	SET /P update="Would you like to check for updates to Dreambot? (y/n): "
)
IF /I "%update%" == "y" (
	GOTO update
) ELSE (
	ECHO Attempting to launch Dreambot with a console attached...

	IF NOT EXIST "%UserProfile%\DreamBot\BotData\client.jar" (
		ECHO Could not find client.jar. Searched in: %UserProfile%\DreamBot\BotData\client.jar
		PAUSE
	) ELSE (
		ECHO Found client.jar
	)

	java -Xbootclasspath/p:%UserProfile%\DreamBot\BotData\client.jar -Xmx512M -jar %UserProfile%\DreamBot\BotData\client.jar
)
:update
ECHO Attempting to open DBLauncher.jar to check for updates.
java -jar DBLauncher.jar