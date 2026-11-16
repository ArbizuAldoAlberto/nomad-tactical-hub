@echo off
title NOMAD NAIR ANILLO 2 - MOTOR IA LOCAL OFFLINE (:8081)
echo ============================================================================
echo  NOMAD ARK OMEGA // ANILLO 2: SERVIDOR IA LOCAL GGUF (PUERTO 8081)
echo ============================================================================
if exist "koboldcpp.exe" (
    koboldcpp.exe --model qwen2.5-1.5b-instruct-q4_k_m.gguf --port 8081 --contextsize 4096
) else if exist "llamafile.exe" (
    llamafile.exe -m qwen2.5-1.5b-instruct-q4_k_m.gguf --port 8081 -c 4096
) else (
    echo [INFO] Coloque koboldcpp.exe o llamafile.exe junto al modelo .gguf en esta carpeta,
    echo o ejecute FETCH_ASSETS.bat --profile=local-ai para descargar el modelo Qwen2.5-1.5B GGUF.
)
pause
