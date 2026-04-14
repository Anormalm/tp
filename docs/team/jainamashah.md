# Jainam Shah - Project Portfolio Page

## Overview
Crypto1010 is a CLI blockchain wallet simulator for students to learn blockchain fundamentals such as wallet-based transfers, block linkage, and chain validation through hands-on command usage.

My focus was improving the transfer flow (`send`), including stronger argument handling, clearer behavior documentation, and UML artifacts that explain the design and data flow to future contributors.

## Summary of Contributions

### Code contributed
- [Code Dashboard link: [View my code contributions](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=jainamashah&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=)]

### Enhancements implemented
- Designed and added new SendCommand UML diagrams (class, validation sequence, activity) in both PUML and PNG formats. Integrated these into the Developer Guide and referenced in PPP.
- Updated the Developer Guide and PPP to clarify and expand on transfer, validation, persistence, and testing contributions.
- Enforced key generation before send: updated send command logic and all related tests to require keygen before sending.
- Improved error handling and output for HelpCommand, CrossSendCommand, and BalanceCommand.
- Refactored CrossSendCommandTest and other test classes for clarity, consistency, and checkstyle compliance.
- Enhanced UI output: implemented table-like formatting, improved spacing, and headers for commands.
- Improved wallet safety and user-facing behavior (for example, key/address handling and balance-related edge cases).
- Enhanced transfer flow behavior, including fee-policy support (speed tiers and manual override) and clearer execution summaries.
- Added/updated comprehensive JUnit tests for `SendCommand`, `BalanceCommand`, `CrossSendCommand`, and related flows to ensure robust behavior and edge case coverage.
- Improved error messages and user feedback for invalid command formats and edge cases.

### Storage persistence contributions
- Fixed persistence and wallet bugs, improved save-to-disk and autosave logic, and clarified persistence documentation.
- Contributed to making storage behavior more stable and predictable during load/save cycles.
- Helped document and clarify persistence logic in the Developer Guide.

### Issue contributions on GitHub (summary)
- Resolved multiple v1.0 and v2.0 issues focused on command quality, wallet robustness, persistence reliability, and send-flow maintainability.
- Representative completed issues: improvements to command quality, wallet robustness, persistence reliability, and send-flow maintainability.
- Participated in PR reviews, issue triage, and team discussions to ensure smooth collaboration and high code quality.
- Maintained branch hygiene: frequent rebases, merges, and conflict resolutions to keep feature and documentation branches up to date.

### Contributions to the User Guide
- Updated `send` command documentation to keep command format and behavior clear.
- Added a `Coming Soon` section describing planned account-switching capability and persistence scope.
- Clarified error messages and command usage in the documentation for new users.

### Contributions to the Developer Guide
- Added/updated DG sections describing:
 - `send` command implementation details
 - design rationale and dependencies around `SendCommand`
 - UML diagram references for maintainers (including new SendCommand class, validation sequence, and activity diagrams)
 - Persistence and storage design for blockchain and wallet data
 - Documented and clarified the validation and error handling logic for transfers and persistence
 - Other refactoring changes
 - Wallet Persistence

### Contributions to team-based tasks
- Updated project documentation structure and cross-references between DG and diagram sources.
- Maintained branch hygiene by creating focused branches and commits for separate concerns (code quality vs documentation), and by frequent rebases and merges.
- Participated in team meetings and discussions to plan features and resolve blockers, and contributed to PR reviews and issue triage.

## Developer Guide extracts
### SendCommand implementation and structure
I documented the send flow and design responsibilities in the DG, emphasizing:
- command-level validation responsibilities,
- delegation to `TransactionRecordingService`, and
- the UML view of static dependencies used in transfer execution.

### UML diagrams contributed
- `docs/diagrams/SendCommandClassDiagram.puml`
