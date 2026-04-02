# Hu Lifan - Project Portfolio Page

## Overview
Crypto1010 is a CLI-based blockchain learning application that lets users create wallets, send funds, and validate chain integrity locally.  
My main scope was the blockchain core (`Blockchain`, `Block`) and the transfer-to-block recording pipeline, with emphasis on deterministic validation and tamper detection.

## Summary of Contributions

### Code contributed
- [Functional code (Repo)](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=anormalm&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=Anormalm&tabRepo=AY2526S2-CS2113-F14-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

### Enhancements implemented
- Implemented and refined the `Blockchain`/`Block` subsystem as the single source of truth for:
- block creation/linkage (`previousHash`, index continuity).
- deterministic hash-based integrity checking.
- chain-wide validation semantics (structure, linkage, transaction format, and running-balance checks).
- Integrated transfer recording flow so successful `send` operations append transactions through a controlled model path.
- Strengthened persistence safety by validating loaded blockchain data before accepting it into runtime.
- Added safeguards to prevent data overwrite when load fails by disabling save for affected components in that session.
- Added the `viewchain` command to provide expert users with a compact blockchain summary (total blocks, total transactions, compact block list).

### Contributions to the User Guide
- Documented startup authentication behavior (`login`/`register`) and account-scoped data model.
- Updated command documentation for `crossSend` and `viewchain`, including examples and command summary entries.
- Maintained data and persistence notes to reflect account-specific storage paths and current address/key persistence behavior.

### Contributions to the Developer Guide
- Wrote/updated the Blockchain and Block implementation details:
- architecture-level ownership of validation and append invariants.
- component-level design rationale and trade-offs.
- alternatives considered for validation and append APIs.
- Added/updated UML diagrams for this area:
- validation sequence diagram.
- send-to-append sequence diagram.
- blockchain/block class diagram.
- Added implementation notes and manual test guidance for newly introduced command behavior (`viewchain`) and account-scoped persistence flow.

### Contributions to team-based tasks
- Helped align blockchain behavior with command and storage layers so validation rules are enforced consistently in CLI and load-time paths.
- Contributed to documentation quality by expanding technical rationale, limitations, and future-extension direction for blockchain internals.
- Performed integration fixes across parser, command registry, tests, and docs to keep feature additions release-ready.

### Review/mentoring contributions
- Reviewed teammate changes for command parsing, storage behavior, and test coverage consistency.
- Helped identify and prioritize correctness issues (load/save safety, parser edge cases, documentation mismatches) before merge.

### Contributions beyond the project team
- Reported and analyzed project-level defects through GitHub issues and followed through with implementation and documentation updates.
