# -*- coding: utf-8 -*-
"""
Chrome Headless CDP automated E2E audit on https://nomadhub.aldoarbizu.com/
"""
import asyncio
import json
import subprocess
import time
import urllib.request
from pathlib import Path
import websockets

CHROME_PATH = r"C:\Program Files\Google\Chrome\Application\chrome.exe"
PORT = 9298
ROOT = Path(__file__).resolve().parent.parent

async def run_cdp_audit():
    # 1. Start Chrome Headless
    cmd = [
        CHROME_PATH,
        "--headless=new",
        f"--remote-debugging-port={PORT}",
        "--disable-gpu",
        "--no-sandbox",
        "--user-data-dir=C:\\Users\\aldoa\\AppData\\Local\\Temp\\chrome_cdp_audit_p12"
    ]
    proc = subprocess.Popen(cmd)
    print(f"[CDP] Chrome Headless iniciado en puerto {PORT} (PID: {proc.pid})")
    time.sleep(2)

    try:
        # 2. Get WebSocket debugger URL
        v_url = f"http://127.0.0.1:{PORT}/json/version"
        with urllib.request.urlopen(v_url) as resp:
            v_data = json.loads(resp.read().decode())
            ws_url = v_data.get("webSocketDebuggerUrl")
            print(f"[CDP] WebSocket URL: {ws_url}")

        async with websockets.connect(ws_url) as ws:
            msg_id = 1
            async def send(method, params=None):
                nonlocal msg_id
                m_id = msg_id
                msg_id += 1
                payload = {"id": m_id, "method": method, "params": params or {}}
                await ws.send(json.dumps(payload))
                while True:
                    res_raw = await ws.recv()
                    res = json.loads(res_raw)
                    if res.get("id") == m_id:
                        return res.get("result", {})

            # Create target page
            target = await send("Target.createTarget", {"url": "about:blank"})
            target_id = target.get("targetId")
            print(f"[CDP] Target creado: {target_id}")

            page_ws_url = f"http://127.0.0.1:{PORT}/json"
            with urllib.request.urlopen(page_ws_url) as resp:
                targets = json.loads(resp.read().decode())
                page_target = next(t for t in targets if t.get("id") == target_id)
                page_ws = page_target.get("webSocketDebuggerUrl")

            async with websockets.connect(page_ws) as p_ws:
                p_id = 1
                async def p_send(method, params=None):
                    nonlocal p_id
                    cur_id = p_id
                    p_id += 1
                    await p_ws.send(json.dumps({"id": cur_id, "method": method, "params": params or {}}))
                    while True:
                        r_raw = await p_ws.recv()
                        r = json.loads(r_raw)
                        if r.get("id") == cur_id:
                            return r.get("result", {})

                await p_send("Page.enable")
                await p_send("Runtime.enable")

                print("[CDP] Navegando a https://nomadhub.aldoarbizu.com/ ...")
                await p_send("Page.navigate", {"url": "https://nomadhub.aldoarbizu.com/"})
                await asyncio.sleep(4)

                # Evaluate DOM checks in browser
                eval_script = """(() => {
                    const title = document.title;
                    const hud = document.getElementById("brandTitle")?.textContent || "";
                    const mecenazgoTab = !!document.getElementById("tab-mecenazgo");
                    const fileDropZone = !!document.getElementById("fileDropZone");
                    const bfRuntime = document.getElementById("bfRuntimeBox")?.textContent || "";
                    const iceCard = document.getElementById("iceSummaryBox")?.textContent || "";
                    const clockPill = document.getElementById("hudClockPill")?.textContent || "";
                    const qrPortfolio = document.getElementById("qrPortfolioSvg")?.children.length || 0;
                    const qrLabs = document.getElementById("qrLabsSvg")?.children.length || 0;
                    const qrCrypto = document.getElementById("qrCryptoSvg")?.children.length || 0;

                    // Test trauma mode guard
                    document.body.classList.add("mode-trauma");
                    const mecenazgoEl = document.getElementById("tab-mecenazgo");
                    const style = mecenazgoEl ? window.getComputedStyle(mecenazgoEl) : null;
                    const isHiddenInTrauma = style ? style.display === "none" : false;
                    document.body.classList.remove("mode-trauma");

                    return {
                        title,
                        hud,
                        mecenazgoTab,
                        fileDropZone,
                        bfRuntimePresent: bfRuntime.length > 0,
                        iceCardPresent: iceCard.length > 0,
                        clockPill,
                        qrPortfolioRectCount: qrPortfolio,
                        qrLabsRectCount: qrLabs,
                        qrCryptoRectCount: qrCrypto,
                        isHiddenInTrauma
                    };
                })()"""

                res = await p_send("Runtime.evaluate", {"expression": eval_script, "returnByValue": True})
                val = res.get("result", {}).get("value", {})
                print("\n" + "="*80)
                print("AUDITORÍA FORENSE CHROME HEADLESS CDP — ENTORNO DE PRODUCCIÓN REAL")
                print("="*80)
                for k, v in val.items():
                    print(f"  • {k}: {v}")
                print("="*80)

                # Capture screenshot
                ss = await p_send("Page.captureScreenshot", {"format": "png"})
                import base64
                img_data = base64.b64decode(ss.get("data", ""))
                ss_path = ROOT / "dist" / "E2E_AUDIT_PHASE12_SCREENSHOT.png"
                ss_path.write_bytes(img_data)
                print(f"[OK] Captura E2E de producción guardada en: {ss_path} ({len(img_data):,} bytes)")

    finally:
        proc.terminate()
        print("[CDP] Proceso Chrome Headless cerrado.")

if __name__ == "__main__":
    asyncio.run(run_cdp_audit())
