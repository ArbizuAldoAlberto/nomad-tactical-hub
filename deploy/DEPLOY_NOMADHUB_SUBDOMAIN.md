# 🌐 RUNBOOK DE DESPLIEGUE ATÓMICO — `https://nomadhub.aldoarbizu.com/` (`v2.0.0-omega-ark`)

Este runbook documenta los pasos de 1 línea para activar **`https://nomadhub.aldoarbizu.com/`** con calificación **Mozilla Observatory `A+`** y vincularlo con `aldoarbizu.com` y `arbizulabs.com`.

---

## 1. Configuración DNS en Cloudflare (`aldoarbizu.com`)

### Opción A — Vía GitHub Pages (Cero Costo de Servidor + CDN Global Automático)
El archivo [`web-ark/CNAME`](file:///e:/01_DESARROLLO/2026/01_ACTIVE/nomad-tactical-hub/web-ark/CNAME) ya contiene `nomadhub.aldoarbizu.com` y la rama `gh-pages` está publicada en GitHub.
1. En el panel DNS de Cloudflare de `aldoarbizu.com`, crea un registro **`CNAME`**:
   - **Name:** `nomadhub`
   - **Target:** `arbizualdoalberto.github.io`
   - **Proxy status:** `Proxied` (Nube naranja activa) o `DNS only` para emisión TLS automática.

### Opción B — Vía VPS Hetzner Ubuntu 24.04 (`Nginx` + `Let's Encrypt`)
Si prefieres servirlo directamente desde tu VPS Hetzner (`IP_VPS`):
```powershell
# 1. Transferir el paquete web estático y la configuración Nginx al VPS (PowerShell seguro con ;)
scp "E:\01_DESARROLLO\2026\01_ACTIVE\nomad-tactical-hub\dist\nomadhub-aldoarbizu-web-dist.zip" "E:\01_DESARROLLO\2026\01_ACTIVE\nomad-tactical-hub\deploy\nginx-nomadhub.aldoarbizu.com.conf" root@TU_IP_VPS:/tmp/

# 2. En el VPS Ubuntu: descomprimir en /var/www/nomadhub.aldoarbizu.com y activar Nginx + Certbot
ssh root@TU_IP_VPS "mkdir -p /var/www/nomadhub.aldoarbizu.com && unzip -o /tmp/nomadhub-aldoarbizu-web-dist.zip -d /var/www/nomadhub.aldoarbizu.com && cp /tmp/nginx-nomadhub.aldoarbizu.com.conf /etc/nginx/sites-available/nomadhub.aldoarbizu.com && ln -sf /etc/nginx/sites-available/nomadhub.aldoarbizu.com /etc/nginx/sites-enabled/ && nginx -t && systemctl reload nginx"
```

---

## 2. Snippet HTML de Integración para `aldoarbizu.com` y `arbizulabs.com`

Copia y pega esta tarjeta destacada en la sección de proyectos principales / filantrópicos de tu portfolio:

```html
<!-- TARJETA OFICIAL: NOMAD TACTICAL HUB & SOLIDARITY NETWORK (OMEGA v2.0) -->
<article class="project-card nomad-omega-card" style="background:#0E1116;border:1px solid #00F29D;border-radius:12px;padding:20px;color:#F0F6FC;">
  <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:10px;">
    <span style="background:#00F29D;color:#0E1116;font-weight:800;font-size:0.75rem;padding:3px 10px;border-radius:4px;">OPEN SOURCE HUMANITARIO · OFFLINE-FIRST</span>
    <span style="color:#00E5FF;font-family:monospace;font-size:0.8rem;">v2.0.0-omega-ark</span>
  </div>
  <h3 style="color:#00F29D;font-size:1.3rem;margin-bottom:8px;">🛰️ NOMAD Tactical Hub &amp; Solidarity Network</h3>
  <p style="color:#8B949E;font-size:0.92rem;line-height:1.5;margin-bottom:16px;">
    Ecosistema pentadimensional de supervivencia civil, triaje médico (MARCH/SRO OMS), telecomunicaciones de emergencia por radio analógica/digital (AFSK, SSTV Robot36, Morse 800Hz, LoRa Meshtastic), biblioteca universal auto-reparable en USB (PDF + EPUB Kindle + PAR2) y enrutador de IA adaptativa en 4 anillos.
  </p>
  <div style="display:flex;gap:10px;flex-wrap:wrap;">
    <a href="https://nomadhub.aldoarbizu.com/" target="_blank" rel="noopener" style="background:#00F29D;color:#0E1116;padding:10px 16px;border-radius:6px;font-weight:800;text-decoration:none;">🚀 Abrir Web App / PWA</a>
    <a href="https://github.com/ArbizuAldoAlberto/nomad-tactical-hub/releases/tag/v2.0.0-omega-ark" target="_blank" rel="noopener" style="background:#1F2630;color:#00E5FF;border:1px solid #00E5FF;padding:10px 16px;border-radius:6px;font-weight:700;text-decoration:none;">⬇️ Descargar APK &amp; Suite USB 128GB</a>
  </div>
</article>
```
