# -*- coding: utf-8 -*-
"""
Upload Phase 12 assets to GitHub Release v2.0.0-omega-ark
"""
import json
import os
import subprocess
import urllib.request
import urllib.error
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DIST = ROOT / "dist"

def get_token():
    if token := os.environ.get("GITHUB_TOKEN"):
        return token
    try:
        p = subprocess.run(["git", "credential", "fill"], input="protocol=https\nhost=github.com\n", text=True, capture_output=True)
        for line in p.stdout.splitlines():
            if line.startswith("password="):
                return line.split("=", 1)[1]
    except Exception:
        pass
    return ""

TOKEN = get_token()

HEADERS = {
    "Authorization": f"Bearer {TOKEN}",
    "Accept": "application/vnd.github+json",
    "User-Agent": "NOMAD-Release-Uploader"
}

ASSETS_TO_UPLOAD = [
    DIST / "nomad-tactical-hub.apk",
    DIST / "NOMAD_ARK_USB_v2.0.0_OMEGA.zip",
    DIST / "NOMAD_KINDLE_EPUB_PACK_v2.0.0.zip",
    DIST / "nomadhub-aldoarbizu-web-dist.zip"
]

def get_release():
    url = "https://api.github.com/repos/ArbizuAldoAlberto/nomad-tactical-hub/releases/tags/v2.0.0-omega-ark"
    req = urllib.request.Request(url, headers=HEADERS)
    with urllib.request.urlopen(req) as resp:
        return json.loads(resp.read().decode())

def delete_asset(asset_id):
    url = f"https://api.github.com/repos/ArbizuAldoAlberto/nomad-tactical-hub/releases/assets/{asset_id}"
    req = urllib.request.Request(url, headers=HEADERS, method="DELETE")
    with urllib.request.urlopen(req) as resp:
        print(f"[OK] Asset {asset_id} deleted (HTTP {resp.status})")

def upload_asset(upload_url_template, file_path: Path):
    upload_url = upload_url_template.split("{")[0] + f"?name={file_path.name}"
    data = file_path.read_bytes()
    headers = {
        "Authorization": f"Bearer {TOKEN}",
        "Content-Type": "application/octet-stream",
        "Content-Length": str(len(data)),
        "User-Agent": "NOMAD-Release-Uploader"
    }
    req = urllib.request.Request(upload_url, data=data, headers=headers, method="POST")
    with urllib.request.urlopen(req) as resp:
        result = json.loads(resp.read().decode())
        print(f"[OK] Uploaded {file_path.name}: ID {result.get('id')}, Size: {result.get('size')} B")

def main():
    rel = get_release()
    print(f"Target Release: {rel.get('name')} (ID: {rel.get('id')})")
    upload_url_template = rel.get("upload_url")

    existing_assets = {a.get("name"): a.get("id") for a in rel.get("assets", [])}
    print("Existing release assets:", existing_assets)

    for asset_file in ASSETS_TO_UPLOAD:
        if not asset_file.exists():
            print(f"[ERROR] Local file not found: {asset_file}")
            continue

        if asset_file.name in existing_assets:
            old_id = existing_assets[asset_file.name]
            print(f"Deleting older asset {asset_file.name} (ID: {old_id})...")
            delete_asset(old_id)

        print(f"Uploading updated {asset_file.name} ({asset_file.stat().st_size:,} bytes)...")
        upload_asset(upload_url_template, asset_file)

    print("\n[SUCCESS] Todos los activos de la Fase 12 han sido cargados a GitHub Releases.")

if __name__ == "__main__":
    main()
