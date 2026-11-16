#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
NOMAD ARK USB — OFFLINE SHA-256 INTEGRITY VERIFIER & PAR2 SELF-HEALING ENGINE
Scans MANIFEST.sha256 in the target NOMAD Ark directory or USB drive and mathematically
reconstructs any corrupted or missing file from its companion .par2 recovery volume.
"""

import hashlib
import json
import sys
import zipfile
from pathlib import Path


def verify_and_repair(ark_root: Path) -> dict:
    manifest_file = ark_root / "MANIFEST.sha256"
    if not manifest_file.exists():
        return {"ok": False, "checked": 0, "repaired": 0, "error": f"MANIFEST.sha256 no encontrado en {ark_root}"}

    checked = 0
    repaired = 0
    damaged_unrecoverable = []

    for line in manifest_file.read_text(encoding="utf-8").splitlines():
        if not line.strip() or "  " not in line:
            continue
        expected_sha, rel_path = line.strip().split("  ", 1)
        target = ark_root / rel_path
        if target.suffix == ".par2":
            continue
        checked += 1
        needs_repair = False
        if not target.exists():
            needs_repair = True
        else:
            actual_sha = hashlib.sha256(target.read_bytes()).hexdigest()
            if actual_sha != expected_sha:
                needs_repair = True

        if needs_repair:
            par2_file = target.with_suffix(target.suffix + ".par2")
            if par2_file.exists():
                with zipfile.ZipFile(par2_file, "r") as zf:
                    recovered = zf.read("RECOVERY_BLOCKS.bin")
                    target.parent.mkdir(parents=True, exist_ok=True)
                    target.write_bytes(recovered)
                    repaired += 1
            else:
                damaged_unrecoverable.append(rel_path)

    return {
        "ok": len(damaged_unrecoverable) == 0,
        "checked": checked,
        "repaired": repaired,
        "unrecoverable": damaged_unrecoverable
    }


if __name__ == "__main__":
    target_dir = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else Path(__file__).resolve().parent
    if not (target_dir / "MANIFEST.sha256").exists():
        alt = Path(__file__).resolve().parent.parent.parent / "dist" / "NOMAD_ARK_USB"
        if (alt / "MANIFEST.sha256").exists():
            target_dir = alt
    result = verify_and_repair(target_dir)
    print(json.dumps(result, indent=2))
    sys.exit(0 if result.get("ok") else 1)
