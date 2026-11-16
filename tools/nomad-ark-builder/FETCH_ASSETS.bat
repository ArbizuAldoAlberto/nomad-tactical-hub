@echo off
setlocal
set PROFILE=%1
if "%PROFILE%"=="" set PROFILE=--profile=tactical-seed
python "%~dp0FETCH_HEAVY_OFFLINE_ASSETS.py" %PROFILE%
endlocal
