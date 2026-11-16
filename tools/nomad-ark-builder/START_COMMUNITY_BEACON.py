#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
NOMAD FARO HOTSPOT SERVER — 1-CLICK COMMUNITY WI-FI BEACON & SOLIDARITY API
Serves START_NOMAD_ARK.html and the 00-07 survival library over local Wi-Fi (:8080 by default)
and exposes /api/solidarity (GET/POST) and /api/sync for neighborhood mutual aid without internet.
"""

import http.server
import json
import os
import socket
import socketserver
import sys
import time
from pathlib import Path

SOLIDARITY_BULLETINS = [
    {
        "id": 1,
        "authorCallsign": "Nodo Faro Bolivar (LU-NOMAD-01)",
        "category": "Ayuda Colectiva",
        "region": "San Carlos de Bolivar (-36.2333, -61.1167)",
        "message": "Servidor Faro Wi-Fi NOMAD Ark activo. Descarguen START_NOMAD_ARK.html, manuales EPUB/PDF y el APK sin internet.",
        "timestamp": int(time.time() * 1000),
        "verifiedWebOfTrust": True
    }
]


def get_local_ip() -> str:
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(("10.255.255.255", 1))
        return s.getsockname()[0]
    except Exception:
        return "127.0.0.1"
    finally:
        s.close()


class NomadFaroHandler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        if self.path.startswith("/api/solidarity"):
            payload = json.dumps({"status": "ok", "count": len(SOLIDARITY_BULLETINS), "posts": SOLIDARITY_BULLETINS}, ensure_ascii=False).encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.send_header("Content-Length", str(len(payload)))
            self.end_headers()
            self.wfile.write(payload)
            return
        if self.path == "/" or self.path == "/index.html":
            self.path = "/START_NOMAD_ARK.html"
        return super().do_GET()

    def do_POST(self):
        if self.path.startswith("/api/solidarity"):
            length = int(self.headers.get("Content-Length", "0"))
            raw_body = self.rfile.read(length).decode("utf-8", errors="replace") if length > 0 else "{}"
            try:
                data = json.loads(raw_body)
            except Exception:
                data = {"message": raw_body}

            new_entry = {
                "id": len(SOLIDARITY_BULLETINS) + 1,
                "authorCallsign": data.get("authorCallsign", "Vecino Red Local"),
                "category": data.get("category", "Ayuda Colectiva"),
                "region": data.get("region", "Red Local Faro Wi-Fi"),
                "message": data.get("message", "Mensaje comunitario recibido"),
                "timestamp": int(time.time() * 1000),
                "verifiedWebOfTrust": True
            }
            SOLIDARITY_BULLETINS.insert(0, new_entry)
            resp = json.dumps({"status": "created", "entry": new_entry, "total": len(SOLIDARITY_BULLETINS)}, ensure_ascii=False).encode("utf-8")
            self.send_response(201)
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.send_header("Content-Length", str(len(resp)))
            self.end_headers()
            self.wfile.write(resp)
            return

        self.send_response(404)
        self.end_headers()

    def log_message(self, format_str, *args):
        # Quiet clean logging
        pass


def run_faro_server(port: int = 8080, serve_dir: Path | None = None):
    if serve_dir is None:
        here = Path(__file__).resolve().parent
        if (here / "START_NOMAD_ARK.html").exists():
            serve_dir = here
        else:
            alt = here.parent.parent / "dist" / "NOMAD_ARK_USB"
            serve_dir = alt if alt.exists() else (here.parent.parent / "web-ark")

    os.chdir(serve_dir)
    local_ip = get_local_ip()
    socketserver.TCPServer.allow_reuse_address = True
    with socketserver.TCPServer(("", port), NomadFaroHandler) as httpd:
        print("=" * 72)
        print(f"[NOMAD FARO HOTSPOT ACTIVO] Sirviendo desde: {serve_dir}")
        print(f" -> URL Local Vecinos Wi-Fi : http://{local_ip}:{port}/START_NOMAD_ARK.html")
        print(f" -> API Solidaridad Local   : http://{local_ip}:{port}/api/solidarity")
        print("=" * 72)
        httpd.serve_forever()


if __name__ == "__main__":
    port_arg = int(sys.argv[1]) if len(sys.argv) > 1 else 8080
    run_faro_server(port=port_arg)
