# -*- coding: utf-8 -*-
"""
Chrome Headless CDP Automated E2E Audit - Phase 13 Gold Master (100% Zero Placeholders & Multi-Domain)
"""
import asyncio
import base64
import json
import subprocess
import time
import urllib.request
from pathlib import Path
import websockets

CHROME_PATH = r"C:\Program Files\Google\Chrome\Application\chrome.exe"
PORT = 9299
ROOT = Path(__file__).resolve().parent.parent

TARGETS = [
    {
        "name": "NOMAD Tactical Hub (VPS Subdomain)",
        "url": "https://nomadhub.aldoarbizu.com/",
        "is_nomad": True,
    },
    {
        "name": "NOMAD Tactical Hub (GitHub Pages Mirror)",
        "url": "https://arbizualdoalberto.github.io/nomad-tactical-hub/",
        "is_nomad": True,
    },
    {
        "name": "Portfolio Principal (aldoarbizu.com)",
        "url": "https://aldoarbizu.com/",
        "is_nomad": False,
        "expect_link": "nomadhub.aldoarbizu.com"
    },
    {
        "name": "Arbizu Labs (arbizulabs.com)",
        "url": "https://arbizulabs.com/cases/nomad-tactical-hub",
        "is_nomad": False,
        "expect_link": "nomadhub.aldoarbizu.com"
    }
]

async def run_audit():
    cmd = [
        CHROME_PATH,
        "--headless=new",
        f"--remote-debugging-port={PORT}",
        "--disable-gpu",
        "--no-sandbox",
        "--window-size=1440,1080",
        "--user-data-dir=C:\\Users\\aldoa\\AppData\\Local\\Temp\\chrome_cdp_audit_gold_master"
    ]
    proc = subprocess.Popen(cmd)
    print(f"[CDP] Chrome Headless iniciado en puerto {PORT} (PID: {proc.pid})")
    time.sleep(2)

    audit_results = []

    try:
        v_url = f"http://127.0.0.1:{PORT}/json/version"
        with urllib.request.urlopen(v_url) as resp:
            v_data = json.loads(resp.read().decode())
            ws_url = v_data.get("webSocketDebuggerUrl")

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

            for target in TARGETS:
                print(f"\n{'='*70}\n[AUDITANDO] {target['name']}: {target['url']}\n{'='*70}")
                t_res = await send("Target.createTarget", {"url": "about:blank"})
                target_id = t_res.get("targetId")

                page_ws_url = f"http://127.0.0.1:{PORT}/json"
                with urllib.request.urlopen(page_ws_url) as r:
                    targets_list = json.loads(r.read().decode())
                    p_target = next(t for t in targets_list if t.get("id") == target_id)
                    p_ws_url = p_target.get("webSocketDebuggerUrl")

                async with websockets.connect(p_ws_url) as p_ws:
                    p_id = 1
                    console_errors = []
                    network_status = None

                    async def p_send(method, params=None):
                        nonlocal p_id
                        cur_id = p_id
                        p_id += 1
                        await p_ws.send(json.dumps({"id": cur_id, "method": method, "params": params or {}}))
                        while True:
                            r_raw = await p_ws.recv()
                            r = json.loads(r_raw)
                            # Collect console errors
                            if r.get("method") == "Runtime.consoleAPICalled":
                                if r.get("params", {}).get("type") == "error":
                                    console_errors.append(r.get("params"))
                            elif r.get("method") == "Runtime.exceptionThrown":
                                console_errors.append(r.get("params"))
                            elif r.get("method") == "Network.responseReceived":
                                resp_obj = r.get("params", {}).get("response", {})
                                if resp_obj.get("url") == target["url"] or resp_obj.get("url") + "/" == target["url"]:
                                    network_status = resp_obj.get("status")

                            if r.get("id") == cur_id:
                                return r.get("result", {})

                    await p_send("Page.enable")
                    await p_send("Runtime.enable")
                    await p_send("Network.enable")

                    nav = await p_send("Page.navigate", {"url": target["url"]})
                    await asyncio.sleep(4)

                    # Inspect DOM
                    dom_script = """(() => {
                        const html = document.documentElement.innerHTML;
                        const title = document.title;
                        const placeholders = [
                            "configuregate0",
                            "bc1qnomadark",
                            "SoLNomadArk",
                            "PLACEHOLDER"
                        ].filter(p => html.includes(p));

                        const realAddresses = {
                            evm: html.includes("0xb73ca1C37e9aA57F3051A0E8381Ca9CBA772A718"),
                            mp: html.includes("aldoarbizu"),
                            galicia: html.includes("ALDO.ARBIZU.GALICIA")
                        };

                        const vpsMirrors = {
                            apkMirror: html.includes("/downloads/nomad-tactical-hub.apk"),
                            zipMirror: html.includes("/downloads/NOMAD_ARK_USB_v2.0.0_OMEGA.zip")
                        };

                        let traumaGuardPassed = false;
                        const mecenazgoEl = document.getElementById("tab-mecenazgo");
                        if (mecenazgoEl) {
                            document.body.classList.add("mode-trauma");
                            const style = window.getComputedStyle(mecenazgoEl);
                            traumaGuardPassed = style.display === "none";
                            document.body.classList.remove("mode-trauma");
                        }

                        const brandTitle = document.getElementById("brandTitle")?.textContent || "";

                        return {
                            title,
                            brandTitle,
                            placeholdersFound: placeholders,
                            realAddresses,
                            vpsMirrors,
                            traumaGuardPassed,
                            hasExpectedLink: """ + (f'html.includes("{target.get("expect_link")}")' if target.get("expect_link") else "true") + """
                        };
                    })()"""

                    dom_eval = await p_send("Runtime.evaluate", {"expression": dom_script, "returnByValue": True})
                    dom_data = dom_eval.get("result", {}).get("value", {})

                    result_entry = {
                        "target": target["name"],
                        "url": target["url"],
                        "console_errors_count": len(console_errors),
                        "console_errors": [str(e) for e in console_errors[:3]],
                        "dom_data": dom_data
                    }
                    audit_results.append(result_entry)

                    print(f"  • Title: {dom_data.get('title')}")
                    print(f"  • Brand Title: {dom_data.get('brandTitle')}")
                    print(f"  • Placeholders detectados: {dom_data.get('placeholdersFound')} (Debe ser [])")
                    print(f"  • Console errors: {len(console_errors)}")
                    if target["is_nomad"]:
                        print(f"  • Rieles Reales Detectados: {dom_data.get('realAddresses')}")
                        print(f"  • Espejo VPS /downloads/: {dom_data.get('vpsMirrors')}")
                        print(f"  • Trauma Mode Guard: {'PASS' if dom_data.get('traumaGuardPassed') else 'FAIL'}")
                    else:
                        print(f"  • Enlace oficial nomadhub presente: {dom_data.get('hasExpectedLink')}")

                    # Capture screenshot for nomadhub VPS
                    if target["url"] == "https://nomadhub.aldoarbizu.com/":
                        ss = await p_send("Page.captureScreenshot", {"format": "png"})
                        img_data = base64.b64decode(ss.get("data", ""))
                        ss_path = ROOT / "dist" / "E2E_AUDIT_PHASE13_GOLD_MASTER.png"
                        ss_path.write_bytes(img_data)
                        print(f"  • Screenshot capturado: {ss_path} ({len(img_data):,} B)")

                await send("Target.closeTarget", {"targetId": target_id})

        # Save summary report
        report_file = ROOT / "dist" / "E2E_AUDIT_GOLD_MASTER_REPORT.json"
        report_file.write_text(json.dumps(audit_results, indent=2), encoding="utf-8")
        print(f"\n[OK] Reporte completo guardado en {report_file}")

    finally:
        proc.terminate()
        print("[CDP] Chrome Headless finalizado.")

if __name__ == "__main__":
    asyncio.run(run_audit())
