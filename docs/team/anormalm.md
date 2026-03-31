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

### Contributions to the User Guide

### Contributions to the Developer Guide
- Wrote/updated the Blockchain and Block implementation details:
- architecture-level ownership of validation and append invariants.
- component-level design rationale and trade-offs.
- alternatives considered for validation and append APIs.
- Added/updated UML diagrams for this area:
- validation sequence diagram.
- send-to-append sequence diagram.
- blockchain/block class diagram.

### Contributions to team-based tasks
- Helped align blockchain behavior with command and storage layers so validation rules are enforced consistently in CLI and load-time paths.
- Contributed to documentation quality by expanding technical rationale, limitations, and future-extension direction for blockchain internals.

### Review/mentoring contributions
