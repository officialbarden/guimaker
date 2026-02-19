# Security Policy

## Overview
This project is maintained for global public use and distribution. Security, stability, and predictable behavior are treated as core design requirements.

Any behavior enabling unintended command execution, data corruption, privilege escalation, or cross-player data access is considered a security issue.

---

## Supported Versions

Security updates are applied to:

- Latest stable release
- Currently maintained major version (if applicable)

Experimental, preview, snapshot, or archived builds are provided without security guarantees.

---

## Reporting Vulnerabilities

If you discover a security issue:

Please avoid public disclosure until a fix is available.

Recommended process:

1. Contact project maintainers privately
2. Provide clear technical reproduction steps
3. Include environment details (game version, loader, datapack version, etc.)
4. Allow reasonable time for investigation and patch development

Public exploit demonstrations should only be shared after a patch is released.

---

## Scope

### In Scope
- Unauthorized command execution
- Permission bypass
- Scoreboard or storage corruption
- GUI or item system abuse
- Cross-player data access or leakage
- Forced execution through indirect triggers

### Out of Scope
- Base game engine bugs
- Conflicts caused by third-party mods or plugins
- Client-side rendering or resource pack issues
- Server misconfiguration outside datapack control

---

## Secure Development Guidelines

### Input Validation
- Player-controlled values must never be executed without validation
- Dynamic selectors must be constrained or sanitized
- External triggers must be verified before execution

### Data Isolation
- Namespaced storage separation is required
- Global state modification must be intentional and documented
- Cross-system writes must be minimized

### Permission Enforcement
- Sensitive actions must include explicit permission checks
- Hidden or indirect execution paths must be validated
- Admin-level functionality must never rely on client-side state

---

## Dependency Security

If embedded libraries are used:

- Upstream source integrity should be verified
- Local modifications should be documented
- Updates should be reviewed before merging

---

## Release Security Checklist

Before release:

- Debug or test functions removed
- Permission bypass tests completed
- Storage isolation verified
- Scoreboard cleanup verified
- GUI trigger validation tested

---

## Responsible Disclosure

Project maintainers aim to:

- Respond to valid reports within a reasonable timeframe
- Investigate and reproduce reported issues
- Provide fixes or mitigations when possible
- Credit reporters if they choose to be acknowledged

---

## Security Philosophy

Security is treated as a continuous process.  
Stability and predictable execution behavior are prioritized over convenience when conflicts arise.
