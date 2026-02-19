# Contributing Guidelines

## Overview

Thank you for your interest in contributing to this project.

Contributions help improve stability, performance, and long-term maintainability.  
All contributions are reviewed before merging.

---

## Before Contributing

Please ensure:

- You have tested your changes
- Your changes do not introduce security risks
- Your code follows existing project structure
- Your commit messages are clear and descriptive

---

## Contribution Types

### Bug Fixes
- Must include reproduction steps
- Must not introduce breaking changes unless required

### Features
- Must be discussed before large implementation
- Must not break existing public functionality

### Documentation
- Must be accurate and concise
- Must reflect current project behavior

---

## Pull Request Process

1. Fork the repository
2. Create a feature or fix branch
3. Commit using clear commit messages
4. Open a Pull Request with detailed description

---

## Commit Message Guidelines

Recommended format:

type(scope): short description


Examples:

fix(gui): prevent unauthorized button trigger
refactor(storage): isolate player cache writes
docs(readme): update installation instructions


Common types:

- feat → New feature
- fix → Bug fix
- docs → Documentation changes
- refactor → Code restructuring
- perf → Performance improvement
- chore → Maintenance tasks

---

## Code Quality Expectations

Contributions should:

- Follow existing naming conventions
- Avoid unnecessary complexity
- Maintain consistent execution behavior
- Preserve backwards compatibility when possible

---

## Security Requirements

Contributions must not:

- Introduce permission bypass behavior
- Allow unintended command execution
- Modify global data without explicit documentation

Security-sensitive changes may require additional review.

---

## Testing Expectations

Before submitting:

- Test in clean environment
- Test multiplayer behavior if applicable
- Test permission-restricted scenarios
- Verify storage and scoreboard behavior

---

## Code of Conduct

Contributors are expected to:

- Be respectful and constructive
- Focus on technical discussion
- Avoid disruptive or harmful behavior

---

## Maintainer Rights

Maintainers may:

- Request changes before merging
- Reject contributions that do not meet standards
- Modify contributions to maintain project consistency

---

## Questions

If unsure about a contribution, open a discussion or draft pull request first.