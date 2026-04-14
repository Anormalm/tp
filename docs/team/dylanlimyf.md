# Dylan Lim - Project Portfolio Page

### Project: Crypto1010

Crypto1010 is a CLI blockchain wallet simulator for learning concepts such as wallet creation, transfers, validation, and persistence. My main scope was evolving the app from basic single-user wallet operations into a multi-account system with authenticated sessions, account-scoped persistence, and cross-account transfers.

Given below are my contributions to the project.

* **Code contributed:** [Functional code (RepoSense)](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=dylanlimyf&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=dylanlimyf&tabRepo=AY2526S2-CS2113-F14-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)
* Representative merged PRs: [#106](https://github.com/AY2526S2-CS2113-F14-4/tp/pull/106), [#133](https://github.com/AY2526S2-CS2113-F14-4/tp/pull/133), [#208](https://github.com/AY2526S2-CS2113-F14-4/tp/pull/208), [#210](https://github.com/AY2526S2-CS2113-F14-4/tp/pull/210)
* **Enhancements implemented:**
  * Implemented the early wallet-management foundation before the package rename from `seedu.duke` to `seedu.crypto1010`, including the `Wallet` model, `WalletManager`, `ListCommand`, and `BalanceCommand`.
  * Tightened command parsing and validation by enforcing the `w/` prefix for `create`, the `w/` prefix for `balance`, and rejecting wallet names with spaces.
  * Added `history` to display outgoing wallet transaction history.
  * **Legacy `SendCommand` work:** contributed earlier address-validation and transfer-flow refinements, including Bitcoin and Solana support at that stage, clearer insufficient-balance handling, self-transfer and balance-calculation fixes, and later the fix that made manual fee override correctly bypass speed validation. Some of that earlier address-support work has since been superseded by later changes to the command.
  * Refactored transfer execution into `TransactionRecordingService` to separate command parsing from transaction-recording logic.
  * Implemented authenticated multi-account support through `AuthenticationService`, `PasswordHasher`, `AccountStorage`, and account-scoped wallet/blockchain storage.
  * Implemented `CrossSendCommand` and `CrossAccountTransferService` for transfers between wallets owned by different authenticated accounts.
  * Added `LogoutCommand` so users can end the current session and switch accounts without restarting the application.
* **Contributions to testing:**
  * Added or expanded automated tests for `AuthenticationService`, account-scoped storage, `CrossSendCommand`, `HistoryCommand`, `LogoutCommand`, `SendCommand`, `CreateCommand`, `ListCommand`, and `BalanceCommand`.
  * Used tests to lock down parser changes and transfer edge cases such as insufficient balance, invalid wallet names, self-transfer behavior, and fee override handling.
* **Contributions to the User Guide:**
  * Documented startup authentication and account-scoped persistence behavior.
  * Added and updated command usage for `crossSend`, `history`, `logout`, and wallet-related command formats after parser changes.
  * Updated examples, FAQ entries, and data-storage notes to reflect the multi-account model.
  * Fixed User Guide table-of-contents and PDF anchor links so sections such as Quick Start, Startup Authentication, Command Summary, Data and Persistence, and FAQ navigate correctly.
* **Contributions to the Developer Guide:**
  * Added implementation details for authentication, account-scoped storage, and `crossSend`.
  * Documented wallet currency tagging, `history`, wallet persistence, and the transfer-recording design around `TransactionRecordingService`.
  * Updated the guide with account-switching design notes and command implementation guidance for future contributors.
* **Contributions to team-based tasks:**
  * Helped align command, storage, and transfer layers as the project moved from single-user behavior to authenticated multi-account sessions.
  * Kept tests and documentation in sync with behavior changes across wallet creation, transfer history, cross-account transfer, and logout/account switching.
  * Contributed project documentation updates for the User Guide, Developer Guide, About Us page, and team portfolio materials.
* **Contributions beyond the project team:**
  * **Bug fixing and triaging (PE-D focus):** reported bugs and suggestions for other teams during PE-D, and resolved multiple follow-up issues in our own project around command validation, send-flow behavior, and documentation correctness.
  * Representative fixes:
    * Fixed `send` so a manual `fee/` correctly bypasses `speed/` validation instead of being rejected inconsistently.
    * Fixed `balance` argument parsing by enforcing the `w/` wallet prefix and tightening validation.
    * Fixed wallet-name handling by rejecting whitespace-containing names and tightening command argument validation.
    * Fixed `send` error handling and tests around insufficient-balance behavior.
    * Fixed broken User Guide table-of-contents and PDF anchor links so generated documentation navigation works correctly.
  * PE-D issue tracker: [PE-D issues filed](https://github.com/NUS-CS2113-AY2526-S2/ped-dylanlimyf/issues)
