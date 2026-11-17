# -*- coding: utf-8 -*-
"""
Apply Phase 13 Gold Master updates:
- Purge placeholders in Riel 2 (Crypto) -> 100% real multi-chain EVM address across Base/Polygon/BSC
- Add direct Hetzner VPS mirror download buttons (/downloads/...)
- Update titles to OMEGA v2.4 GOLD MASTER
- Bump service worker to nomad-ark-omega-v2.4.0
"""
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent

# 1. Update web-ark/START_NOMAD_ARK.html
start_html = ROOT / "web-ark" / "START_NOMAD_ARK.html"
text = start_html.read_text(encoding="utf-8")

# Update Title
text = text.replace(
    "<title>NOMAD Tactical Hub &amp; Solidarity Network // Estación Offline-First &amp; Biblioteca Universal (OMEGA v2.3)</title>",
    "<title>NOMAD Tactical Hub &amp; Solidarity Network // Estación Offline-First &amp; Biblioteca Universal (OMEGA v2.4 Gold Master)</title>"
)
text = text.replace(
    '<div class="brand-title" id="brandTitle">🛰️ NOMAD TACTICAL HUB &amp; SOLIDARITY NETWORK // OMEGA v2.3 HUD</div>',
    '<div class="brand-title" id="brandTitle">🛰️ NOMAD TACTICAL HUB &amp; SOLIDARITY NETWORK // OMEGA v2.4 GOLD MASTER</div>'
)
text = text.replace(
    '<span>100% Humanitario &amp; Libre de Paywalls · Versión OMEGA v2.3</span>',
    '<span>100% Humanitario &amp; Libre de Paywalls · Versión OMEGA v2.4 Gold Master</span>'
)
text = text.replace(
    '<footer>\n  <div>🛰️ <b>NOMAD Tactical Hub &amp; Solidarity Network // OMEGA v2.3</b>',
    '<footer>\n  <div>🛰️ <b>NOMAD Tactical Hub &amp; Solidarity Network // OMEGA v2.4 Gold Master</b>'
)

# Add VPS mirror download buttons in dl-cards
apk_card_old = """          <div class="sha-tag" id="shaApkTag">SHA-256 (8,313,427 B): 2485ec097303b673fdf720da1cdc3c9e87e203542b899358556571d1f1755c2c</div> <!-- safe-bypass -->
          <a class="btn primary" style="width:100%;margin-top:6px;" href="https://github.com/ArbizuAldoAlberto/nomad-tactical-hub/releases/download/v2.0.0-omega-ark/nomad-tactical-hub.apk">⬇️ DESCARGAR ANDROID .APK</a> <!-- safe-bypass -->"""

apk_card_new = """          <div class="sha-tag" id="shaApkTag">SHA-256 (8,313,427 B): 2485ec097303b673fdf720da1cdc3c9e87e203542b899358556571d1f1755c2c</div> <!-- safe-bypass -->
          <div style="display:flex;gap:6px;margin-top:6px;flex-wrap:wrap;">
            <a class="btn primary" style="flex:1;min-width:130px;" href="https://github.com/ArbizuAldoAlberto/nomad-tactical-hub/releases/download/v2.0.0-omega-ark/nomad-tactical-hub.apk">⬇️ GITHUB CDN (.APK)</a> <!-- safe-bypass -->
            <a class="btn" style="flex:1;min-width:130px;border-color:var(--accent);color:var(--accent);" href="https://nomadhub.aldoarbizu.com/downloads/nomad-tactical-hub.apk" download>⚡ ESPEJO VPS HETZNER</a>
          </div>"""
text = text.replace(apk_card_old, apk_card_new)

usb_card_old = """          <div class="sha-tag" id="shaZipTag">SHA-256 (16,144,766 B): 92fe9be2ac29e9865fdca357079d404d5432da362f23f50e507fda04d0b4a90e</div> <!-- safe-bypass -->
          <a class="btn cyan" style="width:100%;margin-top:6px;" href="https://github.com/ArbizuAldoAlberto/nomad-tactical-hub/releases/download/v2.0.0-omega-ark/NOMAD_ARK_USB_v2.0.0_OMEGA.zip">⬇️ DESCARGAR SUITE USB ARK (.ZIP)</a> <!-- safe-bypass -->"""

usb_card_new = """          <div class="sha-tag" id="shaZipTag">SHA-256 (16,144,766 B): 92fe9be2ac29e9865fdca357079d404d5432da362f23f50e507fda04d0b4a90e</div> <!-- safe-bypass -->
          <div style="display:flex;gap:6px;margin-top:6px;flex-wrap:wrap;">
            <a class="btn cyan" style="flex:1;min-width:130px;" href="https://github.com/ArbizuAldoAlberto/nomad-tactical-hub/releases/download/v2.0.0-omega-ark/NOMAD_ARK_USB_v2.0.0_OMEGA.zip">⬇️ GITHUB CDN (.ZIP)</a> <!-- safe-bypass -->
            <a class="btn" style="flex:1;min-width:130px;border-color:var(--cyan);color:var(--cyan);" href="https://nomadhub.aldoarbizu.com/downloads/NOMAD_ARK_USB_v2.0.0_OMEGA.zip" download>⚡ ESPEJO VPS HETZNER</a>
          </div>"""
text = text.replace(usb_card_old, usb_card_new)

epub_card_old = """          <div class="sha-tag" id="shaEpubTag">SHA-256 (30,410 B): b35baa37561b6a22cf53453e36db1d80126cbc07e6bef7751e5f5281a1119948</div> <!-- safe-bypass -->
          <div class="sha-tag" id="shaWebTag" style="margin-top:4px;">PWA Web Dist (83,384 B): 7619c1aba2aec3619652bf52ffcc6c23b9efd7db3b96baf26b7d72a50ec5ba39</div> <!-- safe-bypass -->
          <a class="btn amber" style="width:100%;margin-top:6px;" href="https://github.com/ArbizuAldoAlberto/nomad-tactical-hub/releases/download/v2.0.0-omega-ark/NOMAD_KINDLE_EPUB_PACK_v2.0.0.zip">⬇️ DESCARGAR PACK KINDLE (12 .EPUB)</a> <!-- safe-bypass -->"""

epub_card_new = """          <div class="sha-tag" id="shaEpubTag">SHA-256 (30,410 B): b35baa37561b6a22cf53453e36db1d80126cbc07e6bef7751e5f5281a1119948</div> <!-- safe-bypass -->
          <div class="sha-tag" id="shaWebTag" style="margin-top:4px;">PWA Web Dist (83,384 B): 7619c1aba2aec3619652bf52ffcc6c23b9efd7db3b96baf26b7d72a50ec5ba39</div> <!-- safe-bypass -->
          <div style="display:flex;gap:6px;margin-top:6px;flex-wrap:wrap;">
            <a class="btn amber" style="flex:1;min-width:130px;" href="https://github.com/ArbizuAldoAlberto/nomad-tactical-hub/releases/download/v2.0.0-omega-ark/NOMAD_KINDLE_EPUB_PACK_v2.0.0.zip">⬇️ GITHUB CDN (12 EPUB)</a> <!-- safe-bypass -->
            <a class="btn" style="flex:1;min-width:130px;border-color:var(--amber);color:var(--amber);" href="https://nomadhub.aldoarbizu.com/downloads/NOMAD_KINDLE_EPUB_PACK_v2.0.0.zip" download>⚡ ESPEJO VPS HETZNER</a>
          </div>"""
text = text.replace(epub_card_old, epub_card_new)

# Purge Riel 2 Crypto in HTML
crypto_html_old = """          <!-- RIEL 2: CRYPTO OFF-GRID CON QR SVG LOCAL -->
          <div class="card" style="background:#090D13;border-color:var(--border);">
            <span class="badge" style="color:var(--gold);border-color:var(--gold);">RIEL 2 · CRIPTOACTIVOS OFF-GRID (QR VECTORIAL)</span>
            <div style="display:flex;gap:6px;margin-top:6px;flex-wrap:wrap;">
              <button id="btnCryptoUsdt" class="primary" style="font-size:0.72rem;padding:4px 8px;min-height:30px;" onclick="selectCryptoRail('USDT')">USDT / USDC (Base L2/EVM)</button>
              <button id="btnCryptoBtc" style="font-size:0.72rem;padding:4px 8px;min-height:30px;" onclick="selectCryptoRail('BTC')">Bitcoin (BTC)</button>
              <button id="btnCryptoSol" style="font-size:0.72rem;padding:4px 8px;min-height:30px;" onclick="selectCryptoRail('SOL')">Solana (SOL)</button>
            </div>
            <div style="display:flex;gap:12px;align-items:center;margin-top:10px;">
              <svg id="qrCryptoSvg" width="82" height="82" viewBox="0 0 21 21" style="background:#FFF;padding:4px;border-radius:4px;flex-shrink:0;"></svg>
              <div style="flex:1;">
                <div id="cryptoNetworkLabel" style="font-size:0.74rem;color:var(--gold);font-weight:700;">Red: Base L2 / Polygon / BEP-20 (EVM)</div>
                <div id="cryptoAddressBox" class="result-box" style="margin-top:4px;font-size:0.68rem;word-break:break-all;">0xb73ca1C37e9aA57F3051A0E8381Ca9CBA772A718</div>
                <button style="margin-top:6px;width:100%;font-size:0.75rem;" onclick="copyCurrentCryptoAddress()">📋 Copiar Dirección Cripto</button>
              </div>
            </div>
          </div>"""

crypto_html_new = """          <!-- RIEL 2: CRYPTO OFF-GRID CON QR SVG LOCAL (100% REAL - MULTI-CADENA EVM) -->
          <div class="card" style="background:#090D13;border-color:var(--border);">
            <span class="badge" style="color:var(--gold);border-color:var(--gold);">RIEL 2 · CRIPTOACTIVOS OFF-GRID (MULTI-CADENA REAL)</span>
            <div style="display:flex;gap:6px;margin-top:6px;flex-wrap:wrap;">
              <button id="btnCryptoBase" class="primary" style="font-size:0.72rem;padding:4px 8px;min-height:30px;" onclick="selectCryptoRail('BASE')">Base L2 / Arbitrum</button>
              <button id="btnCryptoPolygon" style="font-size:0.72rem;padding:4px 8px;min-height:30px;" onclick="selectCryptoRail('POLYGON')">Polygon POS</button>
              <button id="btnCryptoBsc" style="font-size:0.72rem;padding:4px 8px;min-height:30px;" onclick="selectCryptoRail('BSC')">BNB Chain (BEP-20)</button>
            </div>
            <div style="display:flex;gap:12px;align-items:center;margin-top:10px;">
              <svg id="qrCryptoSvg" width="82" height="82" viewBox="0 0 21 21" style="background:#FFF;padding:4px;border-radius:4px;flex-shrink:0;"></svg>
              <div style="flex:1;">
                <div id="cryptoNetworkLabel" style="font-size:0.74rem;color:var(--gold);font-weight:700;">Red: Base L2 / Arbitrum / Optimism (EVM · Bajas Comisiones) [USDT · USDC · ETH]</div>
                <div id="cryptoAddressBox" class="result-box" style="margin-top:4px;font-size:0.68rem;word-break:break-all;">0xb73ca1C37e9aA57F3051A0E8381Ca9CBA772A718</div>
                <button style="margin-top:6px;width:100%;font-size:0.75rem;" onclick="copyCurrentCryptoAddress()">📋 Copiar Dirección Cripto (EVM)</button>
              </div>
            </div>
          </div>"""
text = text.replace(crypto_html_old, crypto_html_new)

# Purge Riel 2 Crypto in JS
crypto_js_old = """const CRYPTO_RAIL_DATA = {
  USDT: {
    net: "Red: Base L2 / Polygon / BEP-20 (EVM Compatible)",
    addr: "0xb73ca1C37e9aA57F3051A0E8381Ca9CBA772A718"
  },
  BTC: {
    net: "Red: Bitcoin (BTC Native SegWit / Lightning)",
    addr: "bc1qnomadarkhumanitarianwalletconfiguregate0"
  },
  SOL: {
    net: "Red: Solana (SOL Mainnet-Beta)",
    addr: "SoLNomadArkHumanitarianWalletConfigureGate0"
  }
};
let selectedCryptoKey = "USDT";

function selectCryptoRail(k) {
  selectedCryptoKey = k;
  ["Usdt", "Btc", "Sol"].forEach(s => {
    const b = document.getElementById("btnCrypto" + s);
    if (b) {
      if (s.toUpperCase() === k) {
        b.className = "primary";
      } else {
        b.className = "";
      }
    }
  });
  const data = CRYPTO_RAIL_DATA[k];
  const netLabel = document.getElementById("cryptoNetworkLabel");
  const addrBox = document.getElementById("cryptoAddressBox");
  if (netLabel) netLabel.textContent = data.net;
  if (addrBox) addrBox.textContent = data.addr;
  renderDeterministicQrSvg("qrCryptoSvg", data.addr);
}"""

crypto_js_new = """const CRYPTO_RAIL_DATA = {
  BASE: {
    net: "Red: Base L2 / Arbitrum / Optimism (EVM · Recomendada - Bajas Comisiones)",
    tokens: "USDT · USDC · ETH",
    addr: "0xb73ca1C37e9aA57F3051A0E8381Ca9CBA772A718"
  },
  POLYGON: {
    net: "Red: Polygon POS (EVM)",
    tokens: "USDT · USDC · POL / MATIC",
    addr: "0xb73ca1C37e9aA57F3051A0E8381Ca9CBA772A718"
  },
  BSC: {
    net: "Red: BNB Smart Chain (BEP-20 EVM)",
    tokens: "USDT · USDC · BNB",
    addr: "0xb73ca1C37e9aA57F3051A0E8381Ca9CBA772A718"
  }
};
let selectedCryptoKey = "BASE";

function selectCryptoRail(k) {
  selectedCryptoKey = k;
  ["Base", "Polygon", "Bsc"].forEach(s => {
    const b = document.getElementById("btnCrypto" + s);
    if (b) {
      b.className = (s.toUpperCase() === k) ? "primary" : "";
    }
  });
  const data = CRYPTO_RAIL_DATA[k];
  const netLabel = document.getElementById("cryptoNetworkLabel");
  const addrBox = document.getElementById("cryptoAddressBox");
  if (netLabel) netLabel.textContent = `${data.net} [${data.tokens}]`;
  if (addrBox) addrBox.textContent = data.addr;
  renderDeterministicQrSvg("qrCryptoSvg", data.addr);
}"""
text = text.replace(crypto_js_old, crypto_js_new)
text = text.replace('selectCryptoRail("USDT");', 'selectCryptoRail("BASE");')

start_html.write_text(text, encoding="utf-8")
(ROOT / "web-ark" / "index.html").write_text(text, encoding="utf-8")
print("[OK] START_NOMAD_ARK.html & index.html updated for Phase 13 Gold Master.")

# 2. Bump sw.js
sw_path = ROOT / "web-ark" / "sw.js"
sw_text = sw_path.read_text(encoding="utf-8")
sw_text = sw_text.replace('const CACHE_NAME = "nomad-ark-omega-v2.3.0";', 'const CACHE_NAME = "nomad-ark-omega-v2.4.0";')
sw_path.write_text(sw_text, encoding="utf-8")
print("[OK] sw.js bumped to nomad-ark-omega-v2.4.0.")

# 3. Update Android SurvivalHubScreen.kt
screen_path = ROOT / "app" / "src" / "main" / "java" / "com" / "example" / "ui" / "SurvivalHubScreen.kt"
screen_text = screen_path.read_text(encoding="utf-8")
screen_text = screen_text.replace(
    'text = "OMEGA v2.3 // ALDO ARBIZU · ARBIZU LABS"',
    'text = "OMEGA v2.4 GOLD MASTER // ALDO ARBIZU · ARBIZU LABS"'
)
screen_text = screen_text.replace(
    '• EVM Base L2 / Polygon: 0xb73ca1C37e9aA57F3051A0E8381Ca9CBA772A718',
    '• EVM Multi-Cadena (Base L2 / Polygon / BSC): 0xb73ca1C37e9aA57F3051A0E8381Ca9CBA772A718'
)
screen_path.write_text(screen_text, encoding="utf-8")
print("[OK] SurvivalHubScreen.kt updated for Phase 13 Gold Master.")

# 4. Update build_nomad_ark.py colophon
builder_path = ROOT / "tools" / "nomad-ark-builder" / "build_nomad_ark.py"
b_text = builder_path.read_text(encoding="utf-8")
b_text = b_text.replace(
    '- Web3 EVM (Base L2 / Polygon): 0xb73ca1C37e9aA57F3051A0E8381Ca9CBA772A718',
    '- Web3 Multi-Cadena (Base L2 / Polygon / BSC): 0xb73ca1C37e9aA57F3051A0E8381Ca9CBA772A718'
)
builder_path.write_text(b_text, encoding="utf-8")
print("[OK] build_nomad_ark.py updated for Phase 13 Gold Master.")
