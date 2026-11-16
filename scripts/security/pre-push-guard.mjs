#!/usr/bin/env node
// ═══════════════════════════════════════════════════════════════════════════════
// ARBIZU LABS — Pre-Push Branch Guard v1.0
// Bloquea pushes directos a ramas protegidas (main, master, production)
// Exige el uso de ramas aisladas (feature/*, fix/*, audit/*, feat/*, hotfix/*)
//
// Uso: Configurar como git hook pre-push
//   git config core.hooksPath .githooks
//
// Exit codes:
//   0 = Push autorizado (rama aislada o excepción)
//   1 = Push BLOQUEADO (intento de push directo a rama protegida)
// ═══════════════════════════════════════════════════════════════════════════════

import { execSync } from 'child_process';

const PROTECTED_BRANCHES = ['main', 'master', 'production', 'release'];

const ALLOWED_PREFIXES = [
  'feature/', 'feat/', 'fix/', 'bugfix/', 'hotfix/',
  'audit/', 'chore/', 'docs/', 'refactor/', 'test/',
  'ci/', 'build/', 'perf/', 'style/',
];

function main() {
  let currentBranch;
  try {
    currentBranch = execSync('git rev-parse --abbrev-ref HEAD', { encoding: 'utf-8' }).trim();
  } catch {
    // Si no se puede determinar la rama, permitir el push
    process.exit(0);
  }

  // Verificar si la rama actual es protegida
  if (!PROTECTED_BRANCHES.includes(currentBranch)) {
    // Rama aislada — push permitido
    process.exit(0);
  }

  // Verificar si hay un merge en curso (fast-forward desde feature branch es OK)
  try {
    const merging = execSync('git rev-parse -q --verify MERGE_HEAD', { encoding: 'utf-8', stdio: ['pipe', 'pipe', 'ignore'] }).trim();
    if (merging) {
      // Merge en curso — es un fast-forward legítimo desde feature branch
      process.exit(0);
    }
  } catch {
    // No hay merge en curso — continuar con el bloqueo
  }

  // Check: ¿El commit más reciente fue un merge commit? (indica merge desde feature)
  try {
    const revOutput = execSync('git rev-list --parents -n 1 HEAD', { encoding: 'utf-8' }).trim();
    const parents = revOutput.split(/\s+/).slice(1);
    if (parents.length > 1) {
      // Merge commit — resultado de un merge de feature branch
      console.log(`ℹ️  Push a '${currentBranch}' permitido: commit de merge detectado.`);
      process.exit(0);
    }
  } catch {
    // Ignorar errores
  }

  // Verificar variable de entorno de bypass (para CI/CD automation)
  if (process.env.NEXUS_FORCE_PUSH === 'true') {
    console.log(`⚠️  NEXUS_FORCE_PUSH=true — Bypass de branch guard activado.`);
    process.exit(0);
  }

  // BLOQUEAR
  console.log('');
  console.log('╔══════════════════════════════════════════════════════════════╗');
  console.log('║  🛑  PUSH DIRECTO BLOQUEADO — Gobernanza HITL              ║');
  console.log('╚══════════════════════════════════════════════════════════════╝');
  console.log('');
  console.log(`  Rama protegida: ${currentBranch}`);
  console.log('');
  console.log('  La política de ARBIZU LABS prohíbe pushes directos a ramas');
  console.log('  de producción. Debes trabajar en una rama aislada:');
  console.log('');
  console.log('    git checkout -b feature/tu-funcionalidad');
  console.log('    git checkout -b fix/tu-corrección');
  console.log('    git checkout -b audit/tu-auditoría');
  console.log('');
  console.log('  Luego, merge por fast-forward:');
  console.log(`    git checkout ${currentBranch}`);
  console.log('    git merge --ff-only feature/tu-funcionalidad');
  console.log(`    git push origin ${currentBranch}`);
  console.log('');
  console.log('  Bypass de emergencia (solo Director autorizado):');
  console.log('    NEXUS_FORCE_PUSH=true git push origin ' + currentBranch);
  console.log('');
  process.exit(1);
}

main();
