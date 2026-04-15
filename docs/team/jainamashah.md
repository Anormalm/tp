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
- Resolved a wide range of issues across multiple releases, including bug fixes, enhancements, and documentation updates.
- Representative completed issues include:
    - [#82: Update Developer Guide and User Guide and PPP](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/82): Coordinated and contributed to major documentation updates for the Developer Guide, User Guide, and PPP.
    - [#168: `send` rounds very small remaining balances down to `0`, hiding non-zero funds](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/168): Fixed rounding logic in the send command to ensure accurate display of small balances.
    - [#178: send works without keygen (contradicts User Guide)](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/178): Enforced key generation before sending, aligning implementation with documentation.
    - [#78: Better CLI UX (Formatting Upgrade)](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/78): Improved CLI formatting for a more user-friendly experience.
    - [#68: Extracting a shared utility method from BalanceCommand.java](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/68): Refactored code to improve maintainability and reduce duplication.
    - [#66: Extracting validation and output in SendCommand.java](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/66): Separated validation and output logic for better code clarity.
    - [#8: Record successful send transactions to wallet history and autosave](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/8): Implemented autosave and transaction history recording for send operations.
    - [#7: Add fee policy for send: default speed, speed tiers, manual override](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/7): Added flexible fee policy options to the send command.
    - [#6: Implement send command with amount/address validation](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/6): Developed the core send command with robust validation.
    - [#56: Blockchain data doesn't persist across sessions](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/56): Fixed a critical bug where blockchain data was not reliably saved and loaded.
    - [#55: balance command doesn't check for if wallet name is valid](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/55): Improved validation in the balance command.
    - [#54: help c/COMMAND_NAME should show ideal format for command input](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/54): Enhanced the help command to display correct command formats.
    - [#53: standardize balance for all wallets generated](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/53): Standardized wallet balance initialization and display.
    - [#52: Wallet address shown before key generation](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/52): Ensured wallet addresses are only shown after key generation.
    - [#48: Save to hard disk not working](https://github.com/AY2526S2-CS2113-F14-4/tp/issues/48): Fixed a major persistence bug where data was not being saved to disk.
- These issues span persistence, validation, CLI UX, documentation, and feature enhancements. Each fix or enhancement involved careful testing and collaboration to ensure robust, user-friendly behavior.

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
- `docs/diagrams/SendCommandActivity.puml`
- `docs/diagrams/SendCommandValidationSequence.puml`
