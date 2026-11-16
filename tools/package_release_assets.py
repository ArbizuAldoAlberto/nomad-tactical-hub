# -*- coding: utf-8 -*-
"""
Deterministic packaging of release assets with fixed timestamps (2026-11-16 20:00:00).
"""
import hashlib
import shutil
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DIST = ROOT / "dist"
DIST_ARK = DIST / "NOMAD_ARK_USB"
WEB_ARK = ROOT / "web-ark"
EPUB_DIR = DIST_ARK / "05_SOFTWARE_E_INSTALADORES_OFFLINE" / "03_FORMATOS_EPUB_PARA_KINDLE"
APK_SRC = ROOT / "app" / "build" / "outputs" / "apk" / "release" / "app-release.apk"
APK_DST = DIST / "nomad-tactical-hub.apk"

FIXED_TIME = (2026, 11, 16, 20, 0, 0)

def sha256_file(p: Path) -> str:
    h = hashlib.sha256()
    with open(p, "rb") as f:
        while chunk := f.read(65536):
            h.update(chunk)
    return h.hexdigest()

def add_deterministic_file(zf: zipfile.ZipFile, file_p: Path, arcname: str):
    data = file_p.read_bytes()
    zinfo = zipfile.ZipInfo(filename=arcname, date_time=FIXED_TIME)
    zinfo.compress_type = zipfile.ZIP_DEFLATED
    zinfo.external_attr = 0o644 << 16
    zf.writestr(zinfo, data)

def package():
    DIST.mkdir(parents=True, exist_ok=True)

    # 1. nomad-tactical-hub.apk
    if APK_SRC.exists():
        shutil.copy2(APK_SRC, APK_DST)
        print(f"[OK] APK copied to {APK_DST}")

    # 2. NOMAD_ARK_USB_v2.0.0_OMEGA.zip
    usb_zip = DIST / "NOMAD_ARK_USB_v2.0.0_OMEGA.zip"
    if usb_zip.exists():
        usb_zip.unlink()
    with zipfile.ZipFile(usb_zip, "w") as zf:
        for file_p in sorted(DIST_ARK.rglob("*")):
            if file_p.is_file():
                arcname = file_p.relative_to(DIST_ARK).as_posix()
                add_deterministic_file(zf, file_p, arcname)
    print(f"[OK] Deterministic USB zip packaged: {usb_zip}")

    # 3. NOMAD_KINDLE_EPUB_PACK_v2.0.0.zip
    epub_zip = DIST / "NOMAD_KINDLE_EPUB_PACK_v2.0.0.zip"
    if epub_zip.exists():
        epub_zip.unlink()
    with zipfile.ZipFile(epub_zip, "w") as zf:
        for file_p in sorted(EPUB_DIR.glob("*.epub")):
            add_deterministic_file(zf, file_p, file_p.name)
    print(f"[OK] Deterministic Kindle EPUB pack packaged: {epub_zip}")

    # 4. nomadhub-aldoarbizu-web-dist.zip
    web_zip = DIST / "nomadhub-aldoarbizu-web-dist.zip"
    if web_zip.exists():
        web_zip.unlink()
    with zipfile.ZipFile(web_zip, "w") as zf:
        for file_p in sorted(WEB_ARK.rglob("*")):
            if file_p.is_file():
                arcname = file_p.relative_to(WEB_ARK).as_posix()
                add_deterministic_file(zf, file_p, arcname)
    print(f"[OK] Deterministic Web dist zip packaged: {web_zip}")

    artifacts = [
        APK_DST,
        usb_zip,
        epub_zip,
        web_zip
    ]

    results = {}
    print("\n" + "="*80)
    print("SELLO CRIPTOGRÁFICO OFICIAL DE FASE 12 (OMEGA v2.3) - REPRODUCIBLE BUILD")
    print("="*80)
    for p in artifacts:
        sha = sha256_file(p)
        size = p.stat().st_size
        results[p.name] = {"sha": sha, "size": size}
        sha_file = p.with_suffix(p.suffix + ".sha256")
        sha_file.write_text(f"{sha}  {p.name}\n", encoding="utf-8")
        print(f"• {p.name} ({size:,} B)")
        print(f"  SHA-256: {sha}")
    print("="*80)
    return results

if __name__ == "__main__":
    package()
