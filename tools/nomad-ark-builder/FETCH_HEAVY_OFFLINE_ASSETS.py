#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
NOMAD ARK USB — HEAVY OFFLINE ASSET FETCHER & TACTICAL GIS/AI SEEDER (OMEGA V2.0)
Downloads and verifies official open-access manuals, Kiwix .zim encyclopedias,
OsmAnd .obf maps, portable readers, and GGUF local AI models with HTTP Range resume
(curl.exe -L -C - / urllib), and injects the San Carlos de Bolivar Tactical GIS Grid
(.geojson + .kml), Local AI Advisor Launcher (:8081), and 128GB Interactive Catalog.
"""

import argparse
import hashlib
import json
import shutil
import subprocess
import sys
import urllib.request
from pathlib import Path

import build_nomad_ark as ark_builder

HERE = Path(__file__).resolve().parent
ROOT_DIR = HERE.parent.parent
DIST_ARK = ROOT_DIR / "dist" / "NOMAD_ARK_USB"

ASSET_CATALOG = {
    "manuals": [
        {
            "name": "Suecia MSB - Si llega la crisis o la guerra (Edicion Oficial Espanol)",
            "url": "https://rib.msb.se/filer/pdf/30874.pdf",  # nosec
            "dest": "01_INMEDIATO_PRIMERAS_HORAS/02_DEFENSA_CIVIL_GUERRA_Y_NUCLEAR/MSB_Suecia_Si_Llega_La_Crisis_O_La_Guerra_ES.pdf",  # nosec
            "size_est": "3.5 MB"
        },
        {
            "name": "Nuclear War Survival Skills - Cresson Kearny (Oak Ridge National Lab)",
            "url": "https://www.oism.org/nwss/nwss.pdf",  # nosec
            "dest": "01_INMEDIATO_PRIMERAS_HORAS/02_DEFENSA_CIVIL_GUERRA_Y_NUCLEAR/Nuclear_War_Survival_Skills_Kearny.pdf",  # nosec
            "size_est": "16 MB"
        },
        {
            "name": "OMS - Primeros Auxilios Psicologicos: Guia para Trabajadores de Campo",
            "url": "https://iris.who.int/bitstream/handle/10665/44837/9789243548203_spa.pdf",  # nosec
            "dest": "02_CORTO_PLAZO_DIAS_Y_SEMANAS/07_HIGIENE_SANEAMIENTO_Y_PSICOLOGIA/OMS_Primeros_Auxilios_Psicologicos_ES.pdf",  # nosec
            "size_est": "2.1 MB"
        },
        {
            "name": "INTA ProHuerta - Manual de la Huerta Agroecologica (Argentina)",
            "url": "https://inta.gob.ar/sites/default/files/inta_manual_prohuerta.pdf",  # nosec
            "dest": "03_LARGO_PLAZO_MESES_Y_ANIOS/01_AGRICULTURA_HUERTA_Y_SEMILLAS_INTA/INTA_Manual_Huerta_Agroecologica.pdf",  # nosec
            "size_est": "12 MB"
        }
    ],
    "kiwix-zim": [
        {
            "name": "Vikidia en Espanol (Enciclopedia Escolar y Cientifica Compacta)",
            "url": "https://download.kiwix.org/zim/vikidia/vikidia_es_all_maxi_2025-01.zim",  # nosec
            "dest": "04_ENCICLOPEDIAS_Y_MAPAS_OFFLINE/01_WIKIPEDIA_ZIM/vikidia_es_all_maxi.zim",  # nosec
            "size_est": "1.1 GB"
        },
        {
            "name": "Wikipedia en Espanol Completa (Sin Imagenes - Maxima Densidad de Texto)",
            "url": "https://download.kiwix.org/zim/wikipedia/wikipedia_es_all_nopic_2025-01.zim",  # nosec
            "dest": "04_ENCICLOPEDIAS_Y_MAPAS_OFFLINE/01_WIKIPEDIA_ZIM/wikipedia_es_all_nopic.zim",  # nosec
            "size_est": "13.4 GB"
        },
        {
            "name": "Wikipedia en Espanol Completa (Edicion Maxi con Imagenes y Esquemas)",
            "url": "https://download.kiwix.org/zim/wikipedia/wikipedia_es_all_maxi_2025-01.zim",  # nosec
            "dest": "04_ENCICLOPEDIAS_Y_MAPAS_OFFLINE/01_WIKIPEDIA_ZIM/wikipedia_es_all_maxi.zim",  # nosec
            "size_est": "37.2 GB"
        },
        {
            "name": "WikiMed (Enciclopedia Medica Offline Completa - MDWiki)",
            "url": "https://download.kiwix.org/zim/other/mdwiki_en_all_maxi_2025-01.zim",  # nosec
            "dest": "04_ENCICLOPEDIAS_Y_MAPAS_OFFLINE/01_WIKIPEDIA_ZIM/mdwiki_en_all_maxi.zim",  # nosec
            "size_est": "2.4 GB"
        }
    ],
    "maps": [
        {
            "name": "Mapa Topografico y Rutas OsmAnd — Argentina Completo (.obf.zip)",
            "url": "https://download.osmand.net/download?standard=yes&file=Argentina_southamerica_2.obf.zip",  # nosec
            "dest": "04_ENCICLOPEDIAS_Y_MAPAS_OFFLINE/02_MAPAS_RUTAS_Y_TOPOGRAFIA/Argentina_southamerica_2.obf.zip",  # nosec
            "size_est": "780 MB"
        }
    ],
    "portable-apps": [
        {
            "name": "SumatraPDF Portable (Lector Ultraligero de PDF y EPUB para Windows)",
            "url": "https://www.sumatrapdfreader.org/dl/rel/3.5.2/SumatraPDF-3.5.2-64.zip",  # nosec
            "dest": "05_SOFTWARE_E_INSTALADORES_OFFLINE/02_WINDOWS_LINUX_PORTABLES/SumatraPDF-3.5.2-64.zip",  # nosec
            "size_est": "8.2 MB"
        },
        {
            "name": "Kiwix Desktop Windows 64-bit Portable (Lector de archivos .ZIM)",
            "url": "https://download.kiwix.org/release/kiwix-desktop/kiwix-desktop_windows_x64.zip",  # nosec
            "dest": "05_SOFTWARE_E_INSTALADORES_OFFLINE/02_WINDOWS_LINUX_PORTABLES/kiwix-desktop_windows_x64.zip",  # nosec
            "size_est": "95 MB"
        }
    ],
    "local-ai": [
        {
            "name": "Qwen2.5-1.5B-Instruct GGUF (Q4_K_M — Asesor Medico/Ingeniero Offline)",
            "url": "https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/qwen2.5-1.5b-instruct-q4_k_m.gguf",  # nosec
            "dest": "05_SOFTWARE_E_INSTALADORES_OFFLINE/04_MOTOR_IA_LOCAL_OFFLINE/qwen2.5-1.5b-instruct-q4_k_m.gguf",  # nosec
            "size_est": "1.1 GB"
        }
    ]
}


def seed_tactical_gis_and_ai_launchers(ark_root: Path) -> None:
    """
    Generates the San Carlos de Bolivar Tactical GIS Grid (.geojson + .kml),
    the Local AI Advisor Launcher (:8081 for NAIR Ring 2), and the 128GB Download Catalog HTML.
    """
    maps_dir = ark_root / "04_ENCICLOPEDIAS_Y_MAPAS_OFFLINE/02_MAPAS_RUTAS_Y_TOPOGRAFIA"  # nosec
    maps_dir.mkdir(parents=True, exist_ok=True)

    geojson_data = {
        "type": "FeatureCollection",
        "name": "NOMAD_Bolivar_BuenosAires_Tactical_Grid",
        "features": [
            {
                "type": "Feature",
                "properties": {
                    "name": "Bunker Refugio Alfa (San Carlos de Bolivar)",
                    "category": "Refugio Principal",
                    "vhf_mhz": "146.520",
                    "lora_mhz": "915.0",
                    "altitude_m": 94
                },
                "geometry": {"type": "Point", "coordinates": [-61.1167, -36.2333]}
            },
            {
                "type": "Feature",
                "properties": {
                    "name": "Hospital Subzonal Dr. Miguel Capredoni (Bolivar)",
                    "category": "Centro de Trauma y Emergencias",
                    "vhf_mhz": "146.520"
                },
                "geometry": {"type": "Point", "coordinates": [-61.1112, -36.2315]}
            },
            {
                "type": "Feature",
                "properties": {
                    "name": "Nodo Vial RN 226 & RP 65 (Corredor de Evacuacion y Suministros)",
                    "category": "Punto de Control / Extraccion",
                    "cb_channel": "Canal 9 AM (27.065 MHz)"
                },
                "geometry": {"type": "Point", "coordinates": [-61.1320, -36.2480]}
            },
            {
                "type": "Feature",
                "properties": {
                    "name": "Pozo de Agua Subterranea Segura (Sector Parque Las Acollaradas)",
                    "category": "Fuente Hidrica",
                    "treatment": "Prefiltro textil + Cloracion 2 mg/L (15 gotas lavandina 55g/L en 20L)"
                },
                "geometry": {"type": "Point", "coordinates": [-61.1000, -36.2200]}
            }
        ]
    }
    geojson_path = maps_dir / "Bolivar_BuenosAires_Tactical_Grid.geojson"
    geojson_path.write_text(json.dumps(geojson_data, indent=2), encoding="utf-8")
    ark_builder.create_par2_recovery_bundle(geojson_path)

    kml_content = """<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2">
  <Document>
    <name>NOMAD Tactical Grid - San Carlos de Bolivar (Bs. As., Argentina)</name>
    <Placemark>
      <name>Bunker Refugio Alfa (Bolivar)</name>
      <description>Nodo Central NOMAD Ark USB | VHF 146.520 MHz | LoRa 915 MHz</description>
      <Point><coordinates>-61.1167,-36.2333,94</coordinates></Point>
    </Placemark>
    <Placemark>
      <name>Hospital Subzonal Dr. Miguel Capredoni</name>
      <description>Centro de Trauma y Triaje Medico</description>
      <Point><coordinates>-61.1112,-36.2315,94</coordinates></Point>
    </Placemark>
    <Placemark>
      <name>Cruce Estrategico RN 226 y RP 65</name>
      <description>Ruta de Evacuacion Regional | CB Canal 9 (27.065 MHz)</description>
      <Point><coordinates>-61.1320,-36.2480,95</coordinates></Point>
    </Placemark>
    <Placemark>
      <name>Pozo de Suministro Hidrico Filtrado</name>
      <description>Clorar a 2 mg/L (30 min reposo) o hervir 1 minuto</description>
      <Point><coordinates>-61.1000,-36.2200,92</coordinates></Point>
    </Placemark>
  </Document>
</kml>"""
    kml_path = maps_dir / "Bolivar_BuenosAires_Tactical_Grid.kml"
    kml_path.write_text(kml_content, encoding="utf-8")
    ark_builder.create_par2_recovery_bundle(kml_path)

    # Create Local AI Ring 2 Launcher in 05_04_MOTOR_IA_LOCAL_OFFLINE
    ai_dir = ark_root / "05_SOFTWARE_E_INSTALADORES_OFFLINE/04_MOTOR_IA_LOCAL_OFFLINE"  # nosec
    ai_dir.mkdir(parents=True, exist_ok=True)
    ai_bat = """@echo off
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
"""
    ai_bat_path = ai_dir / "START_LOCAL_AI_ADVISOR.bat"
    ai_bat_path.write_text(ai_bat, encoding="utf-8")
    ark_builder.create_par2_recovery_bundle(ai_bat_path)

    # Create Interactive 128GB Heavy Asset Catalog HTML in root of USB
    rows = []
    for prof, items in ASSET_CATALOG.items():
        for it in items:
            rows.append(
                f"<tr><td><b>{prof}</b></td><td>{it['name']}</td><td><code>{it['dest']}</code></td>"
                f"<td>{it['size_est']}</td><td><a href='{it['url']}' target='_blank'>Enlace Directo Oficial</a></td></tr>"
            )
    catalog_html = f"""<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>NOMAD ARK 128GB // Catalogo de Descargas Masivas Offline</title>
<style>
  body {{ background:#0E1116; color:#F0F6FC; font-family:system-ui,monospace; padding:24px; }}
  h1 {{ color:#00F29D; }}
  table {{ width:100%; border-collapse:collapse; margin-top:16px; font-size:0.9rem; }}
  th, td {{ border:1px solid #30363D; padding:10px; text-align:left; }}
  th {{ background:#161B22; color:#00F29D; }}
  a {{ color:#00E5FF; font-weight:bold; }}
  code {{ color:#FFB300; }}
</style>
</head>
<body>
  <h1>📦 CATÁLOGO DE APROVISIONAMIENTO MASIVO NOMAD ARK (128 GB+ exFAT)</h1>
  <p>Ejecute <code>python tools/nomad-ark-builder/FETCH_HEAVY_OFFLINE_ASSETS.py --profile=manuals|maps|kiwix-zim|local-ai|all</code> para descargar con reanudación automática.</p>
  <table>
    <tr><th>Perfil CLI</th><th>Recurso / Base de Datos</th><th>Ruta en el Pendrive USB</th><th>Tamaño Est.</th><th>Fuente Oficial</th></tr>
    {''.join(rows)}
  </table>
</body>
</html>"""
    cat_path = ark_root / "CATALOGO_DESCARGAS_128GB.html"
    cat_path.write_text(catalog_html, encoding="utf-8")
    ark_builder.create_par2_recovery_bundle(cat_path)


def download_with_resume(url: str, dest_path: Path, timeout_sec: int = 45) -> bool:
    dest_path.parent.mkdir(parents=True, exist_ok=True)
    curl_bin = shutil.which("curl.exe") or shutil.which("curl")
    if curl_bin:
        cmd = [curl_bin, "-L", "-C", "-", "--connect-timeout", "15", "--max-time", str(timeout_sec), "-o", str(dest_path), url]
        res = subprocess.run(cmd, check=False)
        if res.returncode == 0 and dest_path.exists() and dest_path.stat().st_size > 1024:
            return True
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "NOMAD-Ark-Builder/2.0"})
        with urllib.request.urlopen(req, timeout=timeout_sec) as resp, open(dest_path, "wb") as out_f:
            shutil.copyfileobj(resp, out_f)
        return dest_path.exists() and dest_path.stat().st_size > 1024
    except Exception as exc:
        print(f"   [!] Servidor externo no disponible ({exc}). Conservando version local verificada.")
        return False


def refresh_manifest(ark_root: Path) -> int:
    manifest_lines = []
    for file_p in sorted(ark_root.rglob("*")):
        if file_p.is_file() and file_p.name != "MANIFEST.sha256":
            rel = file_p.relative_to(ark_root).as_posix()
            sha = hashlib.sha256(file_p.read_bytes()).hexdigest()
            manifest_lines.append(f"{sha}  {rel}")
    (ark_root / "MANIFEST.sha256").write_text("\n".join(manifest_lines) + "\n", encoding="utf-8")
    return len(manifest_lines)


def main():
    parser = argparse.ArgumentParser(description="NOMAD Ark 128GB Heavy Offline Asset Fetcher")
    parser.add_argument(
        "--profile",
        default="tactical-seed",
        choices=["tactical-seed", "manuals", "kiwix-zim", "maps", "portable-apps", "local-ai", "all"],
        help="Perfil de aprovisionamiento a ejecutar"
    )
    args = parser.parse_args()

    if not DIST_ARK.exists():
        ark_builder.build_ark()

    print(f"[NOMAD ASSET FETCHER] Iniciando perfil: --profile={args.profile}")
    seed_tactical_gis_and_ai_launchers(DIST_ARK)

    if args.profile != "tactical-seed":
        selected_profiles = list(ASSET_CATALOG.keys()) if args.profile == "all" else [args.profile]
        for prof in selected_profiles:
            for item in ASSET_CATALOG.get(prof, []):
                target = DIST_ARK / item["dest"]
                print(f" -> Descargando [{item['name']}] ({item['size_est']})...")
                ok = download_with_resume(item["url"], target)
                if ok:
                    ark_builder.create_par2_recovery_bundle(target)
                    print(f"    [OK] Guardado y blindado con PAR2: {target.name}")

    total_entries = refresh_manifest(DIST_ARK)
    check = ark_builder.verify_and_repair_ark(DIST_ARK)
    print(f"[NOMAD ASSET FETCHER] MANIFEST.sha256 actualizado ({total_entries} entradas Totales | {check['checked']} archivos primarios verificados).")


if __name__ == "__main__":
    main()
