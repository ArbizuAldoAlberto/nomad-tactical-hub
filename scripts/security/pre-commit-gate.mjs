#!/usr/bin/env node
// ═══════════════════════════════════════════════════════════════════════════════
// ARBIZU LABS — Pre-Commit Security Gate v1.0
// Escáner preventivo de secretos con análisis de entropía Shannon + regex
// Bloquea commits que contengan API keys, tokens, credenciales o archivos .env
//
// Uso:
//   node scripts/security/pre-commit-gate.mjs          # Escanea staged files
//   node scripts/security/pre-commit-gate.mjs --all    # Escanea todo el repo
//   node scripts/security/pre-commit-gate.mjs --file <path>  # Escanea archivo específico
//
// Exit codes:
//   0 = Limpio (seguro para commit)
//   1 = Secreto(s) detectado(s) (commit ABORTADO)
// ═══════════════════════════════════════════════════════════════════════════════

import { execSync } from 'child_process';
import { readFileSync, existsSync } from 'fs';
import { basename, extname } from 'path';

// ─── Configuración de Detección ───────────────────────────────────────────────

const ENTROPY_THRESHOLD = 4.5; // Bits de Shannon — strings con alta aleatoriedad
const MIN_SECRET_LENGTH = 16;  // Longitud mínima para análisis de entropía
const MAX_FILE_SIZE = 512 * 1024; // 512 KB — evitar escanear binarios grandes

// Patrones de secretos conocidos (regex con nombre descriptivo)
const SECRET_PATTERNS = [
  // API Keys genéricas
  { name: 'API Key genérica',           re: /(?:api[_-]?key|apikey)\s*[:=]\s*['"]?([A-Za-z0-9_\-]{20,})['"]?/gi },
  { name: 'Secret Key genérica',        re: /(?:secret[_-]?key|secret)\s*[:=]\s*['"]?([A-Za-z0-9_\-]{20,})['"]?/gi },
  { name: 'Token genérico',             re: /(?:token|auth[_-]?token|access[_-]?token)\s*[:=]\s*['"]?([A-Za-z0-9_\-\.]{20,})['"]?/gi },
  { name: 'Password hardcodeada',       re: /(?:password|passwd|pwd)\s*[:=]\s*['"]([^'"]{8,})['"]?/gi },

  // Proveedores específicos
  { name: 'AWS Access Key',             re: /AKIA[0-9A-Z]{16}/g },
  { name: 'AWS Secret Key',             re: /(?:aws_secret_access_key|secret_access_key)\s*[:=]\s*['"]?([A-Za-z0-9/+=]{40})['"]?/gi },
  { name: 'GitHub Token (ghp/gho/ghu)', re: /gh[pousr]_[A-Za-z0-9_]{36,}/g },
  { name: 'Telegram Bot Token',         re: /[0-9]{8,10}:[A-Za-z0-9_-]{35}/g },
  { name: 'Binance API Key',            re: /(?:binance[_-]?(?:api)?[_-]?(?:key|secret))\s*[:=]\s*['"]?([A-Za-z0-9]{40,})['"]?/gi },
  { name: 'OpenAI API Key',             re: /sk-[A-Za-z0-9]{20,}/g },
  { name: 'Anthropic API Key',          re: /sk-ant-[A-Za-z0-9\-]{40,}/g },
  { name: 'Google AI / Gemini Key',     re: /AIza[A-Za-z0-9\-_]{35}/g },
  { name: 'Slack Token',                re: /xox[bpors]-[0-9]{10,}-[A-Za-z0-9]{10,}/g },
  { name: 'Stripe Key',                 re: /(?:sk|pk)_(?:test|live)_[A-Za-z0-9]{20,}/g },
  { name: 'Firebase Config',            re: /(?:firebase[_-]?(?:api[_-]?key|auth[_-]?domain|project[_-]?id|storage[_-]?bucket|messaging[_-]?sender|app[_-]?id))\s*[:=]\s*['"]([^'"]+)['"]/gi },
  { name: 'Resend API Key',             re: /re_[A-Za-z0-9]{20,}/g },
  { name: 'Supabase Key',              re: /(?:supabase[_-]?(?:key|anon|service))\s*[:=]\s*['"]?eyJ[A-Za-z0-9+/=]{50,}['"]?/gi },

  // Claves privadas
  { name: 'Private Key Header',         re: /-----BEGIN (?:RSA |EC |DSA |OPENSSH )?PRIVATE KEY-----/g },
  { name: 'Llave PGP Privada',          re: /-----BEGIN PGP PRIVATE KEY BLOCK-----/g },

  // Cadenas de conexión
  { name: 'Database URL',               re: /(?:postgres(?:ql)?|mysql|mongodb(?:\+srv)?|redis):\/\/[^\s'"]{10,}/gi },
  { name: 'Connection String',          re: /(?:connection[_-]?string|database[_-]?url|db[_-]?url)\s*[:=]\s*['"]?([^\s'"]{20,})['"]?/gi },

  // JWT / Bearer tokens hardcodeados
  { name: 'JWT Token',                  re: /eyJ[A-Za-z0-9_-]{10,}\.eyJ[A-Za-z0-9_-]{10,}\.[A-Za-z0-9_\-+/=]{10,}/g },
];

// Archivos que NUNCA deberían ser commiteados
const FORBIDDEN_FILES = [
  /^\.env$/,
  /^\.env\.local$/,
  /^\.env\.production$/,
  /^\.env\.development$/,
  /\.env\.local\.backup$/,
  /\.pem$/,
  /\.key$/,
  /\.p12$/,
  /\.jks$/,
  /\.keystore$/,
  /credentials\.json$/,
  /service[_-]?account.*\.json$/,
  /firebase-adminsdk.*\.json$/,
  // .npmrc solo si contiene auth tokens (verificado por contenido, no por nombre)
  // /\.npmrc$/,  // Deshabilitado: verificar contenido en lugar de nombre
];

// Archivos permitidos (excluidos del escaneo de contenido)
const SAFE_EXTENSIONS = new Set([
  '.md', '.txt', '.svg', '.png', '.jpg', '.jpeg', '.gif', '.ico', '.webp',
  '.woff', '.woff2', '.ttf', '.eot', '.mp4', '.webm', '.mp3', '.ogg',
  '.pdf', '.zip', '.tar', '.gz', '.lock', '.sum',
]);

const SAFE_FILENAMES = new Set([
  '.env.example', '.env.sample', '.env.template',
  'package-lock.json', 'pnpm-lock.yaml', 'yarn.lock',
  'uv.lock', 'Pipfile.lock', 'poetry.lock',
]);

// Patrones de rutas seguras (tests, documentación, i18n, ejemplos)
const SAFE_PATH_PATTERNS = [
  /\/tests?\//i,              // Directorio de tests
  /test_[^/]+\.py$/,          // Archivos test_*.py
  /\.test\.(ts|js|tsx|jsx)$/, // Archivos *.test.ts/js
  /\.spec\.(ts|js|tsx|jsx)$/, // Archivos *.spec.ts/js
  /\.mdx?$/,                  // Documentación Markdown
  /\/i18n\//,                 // Internacionalización
  /\/locales?\//,             // Archivos de locale
  /\/examples?\//,            // Directorio de ejemplos
  /\/fixtures?\//,            // Archivos de fixtures de test
  /config\.example\./,        // Archivos de configuración de ejemplo
  /scripts\/security\//,      // Propio escáner de seguridad DefenseClaw
];

// ─── Funciones Core ───────────────────────────────────────────────────────────

/**
 * Calcula la entropía de Shannon de una cadena (bits por carácter).
 * Strings aleatorias (contraseñas, tokens) tienen entropía alta (> 4.5).
 */
function shannonEntropy(str) {
  if (!str || str.length < MIN_SECRET_LENGTH) return 0;

  const freq = {};
  for (const ch of str) {
    freq[ch] = (freq[ch] || 0) + 1;
  }

  let entropy = 0;
  const len = str.length;
  for (const count of Object.values(freq)) {
    const p = count / len;
    if (p > 0) entropy -= p * Math.log2(p);
  }
  return entropy;
}

/**
 * Extrae strings de alta entropía de una línea (posibles secretos).
 */
function findHighEntropyStrings(line, lineNum) {
  const findings = [];

  // Buscar strings entre comillas o asignaciones
  const stringPatterns = [
    /['"]([A-Za-z0-9+/=_\-\.]{16,})['"]|[:=]\s*([A-Za-z0-9+/=_\-\.]{20,})/g,
  ];

  for (const pattern of stringPatterns) {
    let match;
    while ((match = pattern.exec(line)) !== null) {
      const candidate = match[1] || match[2];
      if (!candidate) continue;

      // Filtrar falsos positivos comunes
      if (candidate.match(/^(true|false|null|undefined|none|localhost|example|placeholder|your[_-])/i)) continue;
      if (candidate.match(/^(node_modules|dist|build|src|public|assets|components)/)) continue;
      if (candidate.match(/^(https?:\/\/|file:\/\/|mailto:)/)) continue;
      if (candidate.match(/^\d+\.\d+\.\d+/)) continue; // versiones semánticas

      const entropy = shannonEntropy(candidate);
      if (entropy >= ENTROPY_THRESHOLD) {
        findings.push({
          type: 'HIGH_ENTROPY',
          name: `String de alta entropía (${entropy.toFixed(2)} bits)`,
          line: lineNum,
          value: candidate.substring(0, 8) + '...' + candidate.substring(candidate.length - 4),
          entropy,
        });
      }
    }
  }

  return findings;
}

/**
 * Escanea el contenido de un archivo en busca de secretos.
 */
function scanFileContent(filePath) {
  const findings = [];
  const fileName = basename(filePath);
  const ext = extname(filePath).toLowerCase();

  // Verificar si el archivo debería existir en el commit
  for (const pattern of FORBIDDEN_FILES) {
    if (pattern.test(fileName)) {
      findings.push({
        type: 'FORBIDDEN_FILE',
        name: `Archivo prohibido: ${fileName}`,
        file: filePath,
        line: 0,
        value: 'Este archivo NUNCA debe ser commiteado',
        severity: 'CRITICAL',
      });
      return findings; // No necesitamos escanear el contenido
    }
  }

  // Omitir extensiones seguras y archivos de lock
  if (SAFE_EXTENSIONS.has(ext) || SAFE_FILENAMES.has(fileName)) {
    return findings;
  }

  // Omitir rutas seguras (tests, docs, i18n, ejemplos)
  const normalizedPath = filePath.replace(/\\/g, '/');
  if (SAFE_PATH_PATTERNS.some(p => p.test(normalizedPath))) {
    return findings;
  }

  // Verificar tamaño del archivo
  let content;
  try {
    content = readFileSync(filePath, 'utf-8');
  } catch {
    return findings; // Archivo binario o inaccesible
  }

  if (content.length > MAX_FILE_SIZE) {
    return findings; // Archivo demasiado grande para escanear
  }

  const lines = content.split('\n');

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    const lineNum = i + 1;

    // Omitir líneas explícitamente autorizadas con bypass controlado
    if (line.includes('// nosec') || line.includes('# nosec') || line.includes('/* nosec */') || line.includes('<!-- safe-bypass -->')) {
      continue;
    }

    // Omitir comentarios y líneas vacías
    if (line.trim().startsWith('//') && line.includes('example')) continue;
    if (line.trim().startsWith('#') && line.includes('example')) continue;
    if (line.trim().startsWith('<!--')) continue;

    // Buscar patrones de secretos conocidos
    for (const pattern of SECRET_PATTERNS) {
      // Resetear lastIndex para patrones globales
      pattern.re.lastIndex = 0;
      const match = pattern.re.exec(line);
      if (match) {
        // Filtrar falsos positivos en archivos .example y templates
        if (fileName.includes('example') || fileName.includes('template') || fileName.includes('sample')) continue;
        if (line.includes('YOUR_') || line.includes('your_') || line.includes('xxx') || line.includes('PLACEHOLDER')) continue;
        if (line.includes('firebaseio.com') || line.includes('firebaseapp.com')) continue;
        if (line.includes('process.env.') || line.includes('os.getenv(') || line.includes('os.environ[')) continue;
        if (line.includes('import.meta.env.')) continue;
        // Filtrar referencias a variables de instancia en Python (self._token, self._secret_key)
        if (line.includes('self._') || line.includes('self.')) {
          if (line.match(/self\.\w+\s*[:=]/)) continue;
        }
        // Filtrar definiciones de funciones y métodos
        if (line.match(/^\s*(def |async def |function |const |let |var )\w*(secret|token|key|password)/i)) continue;
        // Filtrar imports y type annotations
        if (line.match(/^\s*(import |from |type |interface |class )/)) continue;
        // Filtrar llamadas a stdlib (secrets.token_hex, secrets.token_urlsafe)
        if (line.includes('secrets.token_') || line.includes('secrets.compare_digest')) continue;
        // Filtrar logs y mensajes de error
        if (line.match(/\.(info|debug|warning|error|log)\s*\(/)) continue;

        const value = match[1] || match[0];
        findings.push({
          type: 'PATTERN_MATCH',
          name: pattern.name,
          file: filePath,
          line: lineNum,
          value: typeof value === 'string'
            ? value.substring(0, 8) + '***' + value.substring(Math.max(8, value.length - 4))
            : '[match]',
          severity: 'HIGH',
        });
      }
    }

    // Buscar strings de alta entropía (solo en archivos de código)
    if (['.js', '.ts', '.mjs', '.cjs', '.tsx', '.jsx', '.py', '.rb', '.go', '.rs', '.yaml', '.yml', '.toml', '.json'].includes(ext)) {
      const entropyFindings = findHighEntropyStrings(line, lineNum);
      findings.push(...entropyFindings.map(f => ({ ...f, file: filePath, severity: 'MEDIUM' })));
    }
  }

  return findings;
}

// ─── Ejecución Principal ──────────────────────────────────────────────────────

function main() {
  const args = process.argv.slice(2);
  let filesToScan = [];

  console.log('');
  console.log('╔══════════════════════════════════════════════════════════════╗');
  console.log('║  🛡️  ARBIZU LABS — Pre-Commit Security Gate v1.0           ║');
  console.log('║  DefenseClaw: Escaneo de Secretos & Entropía Shannon       ║');
  console.log('╚══════════════════════════════════════════════════════════════╝');
  console.log('');

  if (args.includes('--file') && args[args.indexOf('--file') + 1]) {
    // Escanear un archivo específico
    const targetFile = args[args.indexOf('--file') + 1];
    if (existsSync(targetFile)) {
      filesToScan = [targetFile];
    } else {
      console.error(`❌ Archivo no encontrado: ${targetFile}`);
      process.exit(1);
    }
  } else if (args.includes('--all')) {
    // Escanear todos los archivos tracked
    try {
      const output = execSync('git ls-files', { encoding: 'utf-8' });
      filesToScan = output.trim().split('\n').filter(Boolean);
    } catch {
      console.error('❌ Error al ejecutar git ls-files');
      process.exit(1);
    }
  } else {
    // Default: escanear archivos staged (pre-commit)
    try {
      const output = execSync('git diff --cached --name-only --diff-filter=ACMR', { encoding: 'utf-8' });
      filesToScan = output.trim().split('\n').filter(Boolean);
    } catch {
      console.error('❌ Error al obtener archivos staged');
      process.exit(1);
    }
  }

  if (filesToScan.length === 0) {
    console.log('ℹ️  No hay archivos para escanear.');
    process.exit(0);
  }

  console.log(`📋 Escaneando ${filesToScan.length} archivo(s)...`);
  console.log('');

  let allFindings = [];
  let scannedCount = 0;

  for (const file of filesToScan) {
    if (!existsSync(file)) continue;
    scannedCount++;
    const findings = scanFileContent(file);
    if (findings.length > 0) {
      allFindings.push(...findings);
    }
  }

  // Reporte de resultados
  if (allFindings.length === 0) {
    console.log(`✅ ${scannedCount} archivos escaneados — CERO SECRETOS DETECTADOS`);
    console.log('');
    console.log('🟢 Gate Status: PASS — Commit autorizado.');
    console.log('');
    process.exit(0);
  }

  // Agrupar por severidad
  const critical = allFindings.filter(f => f.severity === 'CRITICAL');
  const high = allFindings.filter(f => f.severity === 'HIGH');
  const medium = allFindings.filter(f => f.severity === 'MEDIUM');

  console.log('╔══════════════════════════════════════════════════════════════╗');
  console.log('║  🛑  SECRETOS DETECTADOS — COMMIT ABORTADO                 ║');
  console.log('╚══════════════════════════════════════════════════════════════╝');
  console.log('');

  if (critical.length > 0) {
    console.log('🔴 CRÍTICOS:');
    for (const f of critical) {
      console.log(`   ├─ ${f.name}`);
      console.log(`   │  📁 ${f.file}`);
      console.log(`   │  ⚠️  ${f.value}`);
      console.log('   │');
    }
  }

  if (high.length > 0) {
    console.log('🟠 ALTOS:');
    for (const f of high) {
      console.log(`   ├─ ${f.name} (L${f.line})`);
      console.log(`   │  📁 ${f.file}`);
      console.log(`   │  ⚠️  ${f.value}`);
      console.log('   │');
    }
  }

  if (medium.length > 0) {
    console.log('🟡 MEDIOS (posibles falsos positivos):');
    for (const f of medium.slice(0, 5)) { // Limitar a 5 para no saturar
      console.log(`   ├─ ${f.name} (L${f.line})`);
      console.log(`   │  📁 ${f.file}`);
      console.log('   │');
    }
    if (medium.length > 5) {
      console.log(`   └─ ... y ${medium.length - 5} más.`);
    }
  }

  console.log('');
  console.log(`📊 Resumen: ${critical.length} críticos, ${high.length} altos, ${medium.length} medios`);
  console.log('');

  // Solo bloquear por críticos y altos
  if (critical.length > 0 || high.length > 0) {
    console.log('🔴 Gate Status: FAIL — Commit ABORTADO por seguridad.');
    console.log('');
    console.log('💡 Remediación:');
    console.log('   1. Mover secretos a variables de entorno (.env / process.env.*)');
    console.log('   2. Asegurar que .env* está en .gitignore');
    console.log('   3. Si es falso positivo, agregar "// nosec" al final de la línea');
    console.log('');
    process.exit(1);
  }

  // Warnings de entropía no bloquean
  console.log('🟡 Gate Status: PASS CON ADVERTENCIAS — Revisar strings de alta entropía.');
  console.log('');
  process.exit(0);
}

main();
