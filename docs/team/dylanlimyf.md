# Dylan Lim - Project Portfolio Page

## Overview
Crypto1010 is a CLI blockchain wallet simulator for learning concepts such as wallet creation, transfers, validation, and persistence.

My focus was extending the app from a single-user wallet simulator into a multi-account system, while improving transfer-related workflows such as authentication, history tracking, and transaction recording.

## Summary of Contributions

### Code contributed
- [Functional code (Repo)](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=dylanlimyf&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=dylanlimyf&tabRepo=AY2526S2-CS2113-F14-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

### Enhancements implemented
- Added account authentication and account-scoped storage so each user has separate credentials, wallets, and blockchain data.
- Implemented `crossSend` for cross-account cryptocurrency transfers using currency-tagged wallets and account-aware storage loading.
- Added a `history` command to show outgoing wallet transaction history.
- Refactored transfer handling into `TransactionRecordingService` so blockchain updates and wallet history updates stay consistent.
- Improved `SendCommand` with broader address validation and clearer insufficient-balance handling.
- Earlier in the project, implemented and refined wallet creation, listing, balance queries, and wallet argument validation.

### Contributions to the User Guide
- Documented startup authentication and account-scoped persistence behavior.
- Added and updated command usage for `crossSend`, `history`, and wallet-related command formats.
- Updated examples, FAQ entries, and data storage notes to reflect the multi-account model.

### Contributions to the Developer Guide
- Added implementation details for authentication, account-scoped storage, and `crossSend`.
- Documented wallet currency tagging, `history`, and the transfer-recording design around `TransactionRecordingService`.
- Added guidance on wallet persistence and how to add new commands within the project structure.

### Contributions to team-based tasks
- Added targeted JUnit tests for authentication, account-scoped storage, cross-account transfer logic, and wallet validation.
- Worked on transaction and wallet consistency improvements that supported later send/history features.
- Contributed to team documentation updates for the UG, DG, and PPP.

### Review/mentoring contributions
- Worked with teammates to keep shared command, storage, and validation flows aligned as multi-account changes were integrated.
