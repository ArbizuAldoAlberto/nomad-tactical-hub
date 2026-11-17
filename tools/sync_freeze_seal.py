# -*- coding: utf-8 -*-
"""
Sync the final Phase 12 (OMEGA v2.3) SHA-256 seal across all files and repos.
"""
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent

SEALS = {
    "apk": {
        "name": "nomad-tactical-hub.apk",
        "size": 8313427,
        "size_str": "8,313,427 bytes",
        "size_mb": "7.93 MB",
        "sha": "a7315c5c06008da589d1c5ebe8ea73374683c92ff2c7145fb326b87e16edd0f0"
    },
    "usb": {
        "name": "NOMAD_ARK_USB_v2.0.0_OMEGA.zip",
        "size": 16147337,
        "size_str": "16,147,337 bytes",
        "size_mb": "15.40 MB",
        "sha": "ea5f7c82c8f220e7e08f06741e74f594c5592960d86ecfb9b142588d40165097"
    },
    "epub": {
        "name": "NOMAD_KINDLE_EPUB_PACK_v2.0.0.zip",
        "size": 30529,
        "size_str": "30,529 bytes",
        "size_mb": "29.81 KB",
        "sha": "d8d9aa6a66e7ef2ec52eb82809d53e75d62b7a2e373fe8bb3d4c300addd23ca9"
    },
    "web": {
        "name": "nomadhub-aldoarbizu-web-dist.zip",
        "size": 83634,
        "size_str": "83,634 bytes",
        "size_mb": "81.67 KB",
        "sha": "6f23bf169321591f903a81df2349710034195690e6684246ed87e07d9dc84674"
    }
}

# 1. Update web-ark/START_NOMAD_ARK.html & index.html
start_html = ROOT / "web-ark" / "START_NOMAD_ARK.html"
text = start_html.read_text(encoding="utf-8")
text = re.sub(
    r'<div class="sha-tag" id="shaApkTag">.*?</div> <!-- safe-bypass -->',
    f'<div class="sha-tag" id="shaApkTag">SHA-256 ({SEALS["apk"]["size"]:,} B): {SEALS["apk"]["sha"]}</div> <!-- safe-bypass -->',
    text
)
text = re.sub(
    r'<div class="sha-tag" id="shaZipTag">.*?</div> <!-- safe-bypass -->',
    f'<div class="sha-tag" id="shaZipTag">SHA-256 ({SEALS["usb"]["size"]:,} B): {SEALS["usb"]["sha"]}</div> <!-- safe-bypass -->',
    text
)
text = re.sub(
    r'<div class="sha-tag" id="shaEpubTag">.*?</div> <!-- safe-bypass -->',
    f'<div class="sha-tag" id="shaEpubTag">SHA-256 ({SEALS["epub"]["size"]:,} B): {SEALS["epub"]["sha"]}</div> <!-- safe-bypass -->',
    text
)
text = re.sub(
    r'<div class="sha-tag" id="shaWebTag".*?>.*?</div> <!-- safe-bypass -->',
    f'<div class="sha-tag" id="shaWebTag" style="margin-top:4px;">PWA Web Dist ({SEALS["web"]["size"]:,} B): {SEALS["web"]["sha"]}</div> <!-- safe-bypass -->',
    text
)
js_seals = f"""const OFFICIAL_SEALS = {{
  apk: {{ name: "nomad-tactical-hub.apk ({SEALS['apk']['size']:,} B)", sha: "{SEALS['apk']['sha']}" }},
  usb: {{ name: "NOMAD_ARK_USB_v2.0.0_OMEGA.zip ({SEALS['usb']['size']:,} B)", sha: "{SEALS['usb']['sha']}" }},
  epub: {{ name: "NOMAD_KINDLE_EPUB_PACK_v2.0.0.zip ({SEALS['epub']['size']:,} B)", sha: "{SEALS['epub']['sha']}" }}
}};"""
text = re.sub(r'const OFFICIAL_SEALS = \{[\s\S]*?\};', js_seals, text)
start_html.write_text(text, encoding="utf-8")
(ROOT / "web-ark" / "index.html").write_text(text, encoding="utf-8")
print("[OK] START_NOMAD_ARK.html and index.html updated.")

# 2. Update README.md
readme = ROOT / "README.md"
r_text = readme.read_text(encoding="utf-8")
r_table = f"""| Production Artifact | Official Package | Exact Size | Frozen `SHA-256` Checksum |
| :--- | :--- | :---: | :--- |
| **Android Release APK** | `nomad-tactical-hub.apk` | `{SEALS['apk']['size_str']}` (`{SEALS['apk']['size_mb']}`) | `{SEALS['apk']['sha']}` <!-- safe-bypass --> |
| **USB Ark 128GB Core Suite** | `NOMAD_ARK_USB_v2.0.0_OMEGA.zip` | `{SEALS['usb']['size_str']}` (`{SEALS['usb']['size_mb']}`) | `{SEALS['usb']['sha']}` <!-- safe-bypass --> |
| **Kindle / Kobo E-Ink Pack (12 EPUBs)** | `NOMAD_KINDLE_EPUB_PACK_v2.0.0.zip` | `{SEALS['epub']['size_str']}` (`{SEALS['epub']['size_mb']}`) | `{SEALS['epub']['sha']}` <!-- safe-bypass --> |
| **Portal PWA (`nomadhub.aldoarbizu.com`)** | `nomadhub-aldoarbizu-web-dist.zip` | `{SEALS['web']['size_str']}` (`{SEALS['web']['size_mb']}`) | `{SEALS['web']['sha']}` <!-- safe-bypass --> |"""
r_text = re.sub(r'\| Production Artifact \| Official Package \| Exact Size \| Frozen `SHA-256` Checksum \|[\s\S]*?\| \*\*Portal PWA \(`nomadhub\.aldoarbizu\.com`\)[\s\S]*?\|', r_table, r_text)
readme.write_text(r_text, encoding="utf-8")
print("[OK] README.md updated.")

# 3. Update .nexus_memory.md
nexus_mem = ROOT / ".nexus_memory.md"
if nexus_mem.exists():
    n_text = nexus_mem.read_text(encoding="utf-8")
    n_table = f"""| Artefacto Público | Ruta / Paquete | Tamaño Exacto | Hash `SHA-256` |
| :--- | :--- | :---: | :--- |
| **Android Release APK** | `nomad-tactical-hub.apk` (`app-release.apk`) | `{SEALS['apk']['size_str']}` (`{SEALS['apk']['size_mb']}`) | `{SEALS['apk']['sha']}` <!-- safe-bypass --> |
| **Suite Completa USB Ark** | `NOMAD_ARK_USB_v2.0.0_OMEGA.zip` | `{SEALS['usb']['size_str']}` (`{SEALS['usb']['size_mb']}`) | `{SEALS['usb']['sha']}` <!-- safe-bypass --> |
| **Pack Libros Kindle EPUB (12 EPUBs)** | `NOMAD_KINDLE_EPUB_PACK_v2.0.0.zip` | `{SEALS['epub']['size_str']}` (`{SEALS['epub']['size_mb']}`) | `{SEALS['epub']['sha']}` <!-- safe-bypass --> |
| **Portal PWA (`nomadhub.aldoarbizu.com`)** | `nomadhub-aldoarbizu-web-dist.zip` | `{SEALS['web']['size_str']}` (`{SEALS['web']['size_mb']}`) | `{SEALS['web']['sha']}` <!-- safe-bypass --> |"""
    n_text = re.sub(r'\| Artefacto Público \| Ruta / Paquete \| Tamaño Exacto \| Hash `SHA-256` \|[\s\S]*?\| \*\*Portal PWA \(`nomadhub\.aldoarbizu\.com`\)[\s\S]*?\|', n_table, n_text)
    nexus_mem.write_text(n_text, encoding="utf-8")
    print("[OK] .nexus_memory.md updated.")

# 4. Update arbizu-labs case study
case_mdx = Path(r"E:\01_DESARROLLO\2026\01_ACTIVE\portfolio\arbizu-labs\src\data\cases\nomad-tactical-hub.mdx")
if case_mdx.exists():
    c_text = case_mdx.read_text(encoding="utf-8")
    c_hashes = f"""- **Hashes `SHA-256` Oficiales Congelados (Paridad 100%):**
  - `nomad-tactical-hub.apk` (`{SEALS['apk']['size_str']}` / `{SEALS['apk']['size_mb']}`): `{SEALS['apk']['sha']}` {{/* safe-bypass */}}
  - `NOMAD_ARK_USB_v2.0.0_OMEGA.zip` (`{SEALS['usb']['size_str']}` / `{SEALS['usb']['size_mb']}`): `{SEALS['usb']['sha']}` {{/* safe-bypass */}}
  - `NOMAD_KINDLE_EPUB_PACK_v2.0.0.zip` (`{SEALS['epub']['size_str']}` / `{SEALS['epub']['size_mb']}` — 12 EPUBs): `{SEALS['epub']['sha']}` {{/* safe-bypass */}}
  - `nomadhub-aldoarbizu-web-dist.zip` (`{SEALS['web']['size_str']}` / `{SEALS['web']['size_mb']}`): `{SEALS['web']['sha']}` {{/* safe-bypass */}}"""
    c_text = re.sub(r'- \*\*Hashes `SHA-256` Oficiales Congelados \(Paridad 100%\):\*\*[\s\S]*?nomadhub-aldoarbizu-web-dist\.zip`[^\n]*', c_hashes, c_text)
    case_mdx.write_text(c_text, encoding="utf-8")
    print("[OK] arbizu-labs case study updated.")

# 5. Update Antigravity-Vault
vault_doc = Path(r"E:\01_DESARROLLO\2026\05_BRAIN\Antigravity-Vault\Antigravity-Vault\NOMAD_TACTICAL_HUB_OMEGA_v2.0.0.md")
if vault_doc.exists():
    v_text = vault_doc.read_text(encoding="utf-8")
    v_table = f"""| Artefacto de Producción | Tamaño Exacto | Hash `SHA-256` Certificado |
| :--- | :---: | :--- |
| **`nomad-tactical-hub.apk`** | `{SEALS['apk']['size_str']}` (`{SEALS['apk']['size_mb']}`) | `{SEALS['apk']['sha']}` |
| **`NOMAD_ARK_USB_v2.0.0_OMEGA.zip`** | `{SEALS['usb']['size_str']}` (`{SEALS['usb']['size_mb']}`) | `{SEALS['usb']['sha']}` |
| **`NOMAD_KINDLE_EPUB_PACK_v2.0.0.zip` (`12 EPUBs`)** | `{SEALS['epub']['size_str']}` (`{SEALS['epub']['size_mb']}`) | `{SEALS['epub']['sha']}` |
| **`nomadhub-aldoarbizu-web-dist.zip`** | `{SEALS['web']['size_str']}` (`{SEALS['web']['size_mb']}`) | `{SEALS['web']['sha']}` |"""
    v_text = re.sub(r'\| Artefacto de Producción \| Tamaño Exacto \| Hash `SHA-256` Certificado \|[\s\S]*?\| \*\*`nomadhub-aldoarbizu-web-dist\.zip`\*\* \|[^\n]*', v_table, v_text)
    vault_doc.write_text(v_text, encoding="utf-8")
    print("[OK] Antigravity-Vault updated.")
