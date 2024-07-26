:run
subconverter.exe >> sub_%date:~0,4%%date:~5,2%%date:~8,2%.log  2>&1
goto run