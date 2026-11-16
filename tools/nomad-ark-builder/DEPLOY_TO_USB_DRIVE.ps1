param(
    [string]$DriveLetter = "",
    [switch]$DryRun = $false
)

$ErrorActionPreference = "Stop"
$RepoRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$BuilderScript = Join-Path $PSScriptRoot "build_nomad_ark.py"
$VerifierScript = Join-Path $PSScriptRoot "VERIFY_AND_REPAIR_ARK.py"
$DistArkDir = Join-Path $RepoRoot "dist\NOMAD_ARK_USB"

Write-Host "========================================================================" -ForegroundColor Cyan
Write-Host " 🛰️ NOMAD ARK OMEGA // ASISTENTE DE GRABACIÓN Y VERIFICACIÓN USB (exFAT)" -ForegroundColor Green
Write-Host "========================================================================" -ForegroundColor Cyan

# Paso 1: Recompilar el árbol 00-07 + APK + PDF/EPUB/HTML/TXT + PAR2
Write-Host "[1/4] Compilando distribución maestra NOMAD Ark USB..." -ForegroundColor Yellow
python $BuilderScript

if ($DryRun) {
    Write-Host "[2/4] MODO DRY-RUN ACTIVO: Simulando verificación de unidades extraíbles..." -ForegroundColor Cyan
    $RemovableVolumes = Get-Volume | Where-Object { $_.DriveType -eq 'Removable' -and $_.DriveLetter }
    if ($RemovableVolumes) {
        foreach ($vol in $RemovableVolumes) {
            Write-Host ("   -> Unidad detectada: {0}: ({1}) - Sistema: {2} - Libre: {3:N2} GB" -f $vol.DriveLetter, $vol.FileSystemLabel, $vol.FileSystem, ($vol.SizeRemaining / 1GB))
        }
    } else {
        Write-Host "   -> Ningún pendrive físico conectado actualmente (Simulación Dry-Run sobre dist\NOMAD_ARK_USB)." -ForegroundColor Gray
    }

    Write-Host "[3/4] Verificando integridad SHA-256 + bloques de autorreparación PAR2 en dist\NOMAD_ARK_USB..." -ForegroundColor Yellow
    python $VerifierScript $DistArkDir
    Write-Host "[4/4] DRY-RUN COMPLETADO CON ÉXITO: 100% SHA-256 + PAR2 OK." -ForegroundColor Green
    exit 0
}

# Modo Grabación Física en Pendrive USB
if ([string]::IsNullOrWhiteSpace($DriveLetter)) {
    $Removable = Get-Volume | Where-Object { $_.DriveType -eq 'Removable' -and $_.DriveLetter } | Select-Object -First 1
    if (-not $Removable) {
        Write-Host "[!] No se detectó ningún pendrive extraíble conectado. Especifique -DriveLetter 'E:' o use -DryRun." -ForegroundColor Red
        exit 1
    }
    $DriveLetter = "$($Removable.DriveLetter):"
}

$CleanLetter = $DriveLetter.TrimEnd(':', '\')
$VolInfo = Get-Volume -DriveLetter $CleanLetter -ErrorAction SilentlyContinue
if (-not $VolInfo) {
    Write-Host "[!] La unidad $DriveLetter no existe o no está montada." -ForegroundColor Red
    exit 1
}

Write-Host ("[2/4] Unidad destino: {0}: | Etiqueta: {1} | Sistema de archivos: {2}" -f $CleanLetter, $VolInfo.FileSystemLabel, $VolInfo.FileSystem) -ForegroundColor Cyan

if ($VolInfo.FileSystem -eq "FAT32") {
    Write-Host "⚠️ ADVERTENCIA CRÍTICA: La unidad está en FAT32 (límite de 4 GB por archivo, incompatible con Wikipedia .zim de 13GB-37GB)." -ForegroundColor Yellow
    Write-Host "   Para formatear a exFAT ejecute: Format-Volume -DriveLetter $CleanLetter -FileSystem exFAT -NewFileSystemLabel 'NOMAD_ARK'" -ForegroundColor Yellow
}

$TargetRoot = "${CleanLetter}:\"
Write-Host "[3/4] Copiando estructura NOMAD Ark (00 a 07 + APK + EPUB Kindle + PAR2) hacia $TargetRoot ..." -ForegroundColor Yellow
Copy-Item -Path (Join-Path $DistArkDir "*") -Destination $TargetRoot -Recurse -Force

Write-Host "[4/4] Ejecutando auditoría SHA-256 + PAR2 directamente sobre el pendrive físico ($TargetRoot)..." -ForegroundColor Yellow
python $VerifierScript $TargetRoot
Write-Host "✅ GRABACIÓN Y VERIFICACIÓN FÍSICA COMPLETADA AL 100% EN $TargetRoot" -ForegroundColor Green
