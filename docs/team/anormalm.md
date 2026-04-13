# Hu Lifan - Project Portfolio Page

## Overview
Crypto1010 is a CLI-based blockchain learning application that lets users create wallets, transfer funds, and validate chain integrity locally.  
My primary scope was implementation ownership of blockchain/transfer internals plus release hardening through bug triaging and fixes during PE-D.

## Summary of Contributions

### Code contributed
- [Functional code (Repo)](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=anormalm&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=Anormalm&tabRepo=AY2526S2-CS2113-F14-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

### Enhancements implemented
- Implemented and refined the `Blockchain`/`Block` subsystem as the single source of truth for:
  - block creation/linkage (`previousHash`, index continuity)
  - deterministic hash-based integrity checking
  - chain-wide validation semantics (structure, linkage, transaction format, running-balance checks)
- Integrated transfer recording flow so successful `send` operations append transactions through a controlled model path.
- Strengthened persistence safety by validating loaded blockchain data before accepting it into runtime.
- Added safeguards to prevent data overwrite when load fails by disabling save for affected components in that session.
- Added `viewchain` command support for compact blockchain overview (total blocks, total transactions, compact block list).
- Implemented CLI UI upgrades for jar users:
  - centralized visual rendering via `CliVisuals`
  - startup branding (logo + slogan)
  - authenticated shell prompt format (`username@crypto1010 ~`)
  - interactive tab auto-completion with pre-login and post-login suggestion scopes
- Added CLI robustness/security hardening:
  - sanitized terminal output to prevent control-sequence/ANSI injection from user-provided content
  - moved startup logo loading to bundled classpath resources (jar-safe, avoids CWD spoofing)
  - enforced wallet-name max length to reduce UI truncation/spoofing risk
  - upgraded account password storage to salted PBKDF2 hashing
  - implemented login brute-force throttling (temporary lockout after repeated failures)
  - added credential-file integrity signing and verification
  - added parser/storage size bounds to reduce oversized-input/file abuse risk
  - hardened save/rollback behavior to fail safe on persistence errors

### Bug fixing and triaging (PE-D focus)
- Triaged and resolved multiple PE-D findings across functionality and docs, including parser correctness, command behavior consistency, persistence edge cases, and tutorial/session flow.
- Representative issues fixed include:
  - `#183` (`CreateCommand.resolveArguments` dead no-op)
  - `#182` (`Wallet.setKeys` validation sequencing)
  - `#179` (`create ... curr/generic` handling)
  - `#167` (user-facing warning noise for invalid commands)
  - `#164` (DG startup instruction mismatch)
  - `#152` (tutorial `exit` behavior)
  - `#150` (extreme scientific notation freeze prevention)
  - `#141` (UG quick start for end users)
  - `#140` (blank blockchain file load behavior)
  - registration UX fix for immediate username validation on invalid input (related to `#138`)
- Triage approach used:
  - Reproduce from exact tester steps first
  - Classify as real bug vs accepted behavior
  - Apply minimal safe fix
  - Update tests/docs to prevent regressions and clarify expected behavior
  - Re-run unit, checkstyle, and text-ui regression after each fix batch

### Contributions to the User Guide
- Documented startup authentication behavior (`login`/`register`) and account-scoped data model.
- Updated command documentation for `crossSend` and `viewchain`, including examples and command summary entries.
- Updated quick-start and behavior notes to better reflect real user and jar-run paths.
- Maintained data and persistence notes to reflect account-specific storage paths and current address/key persistence behavior.
- Added/updated CLI usability documentation:
  - startup branding and authenticated prompt format
  - tab auto-completion scope before and after login
  - terminal constraints for completion behavior
- Updated security/persistence documentation:
  - password hashing and temporary login lockout behavior
  - credential integrity signature and key file paths
  - save-failure fail-safe behavior and keygen regeneration restriction

### Contributions to the Developer Guide
- Wrote/updated the Blockchain and Block implementation details:
  - architecture-level ownership of validation and append invariants
  - component-level design rationale and trade-offs
  - alternatives considered for validation and append APIs
- Added/updated UML diagrams for this area:
  - validation sequence diagram
  - send-to-append sequence diagram
  - blockchain/block class diagram
- Updated DG implementation and manual-testing details to stay aligned with current behavior after bug fixes.
- Added implementation notes for shell/completer architecture and mode switching between authentication and command phases.
- Added manual testing steps for tab completion scope transitions.
- Corrected wallet/crypto internals documentation to match actual `KeyPair` + secp256k1 implementation (removed stale RSA/`Key` references).
- Added DG coverage for authentication hardening (PBKDF2, lockout, credential-signature checks) and persistence/input hardening limits.

### Contributions to team-based tasks
- Helped align blockchain behavior with command and storage layers so validation rules are enforced consistently in CLI and load-time paths.
- Performed integration fixes across parser, command registry, tests, and docs to keep releases stable.
- Coordinated bug triaging decisions with teammates (fix now / doc clarify / accept-as-designed) to reduce duplicate work during PE.

### Review/mentoring contributions
- Reviewed teammate changes for command parsing, storage behavior, and test coverage consistency.
- Helped identify and prioritize correctness issues (load/save safety, parser edge cases, documentation mismatches) before merge.
- Supported teammate bug investigations by reproducing reports and narrowing root causes before patching.

### Contributions beyond the project team
- Reported and analyzed project-level defects through GitHub issues and followed through with implementation and documentation updates.
- Contributed structured PE-D triage reasoning (severity, reproducibility, expected behavior) that improved issue turnaround and consistency.
