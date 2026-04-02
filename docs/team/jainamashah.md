# Jainam Shah - Project Portfolio Page

## Overview
Crypto1010 is a CLI blockchain wallet simulator for students to learn blockchain fundamentals such as wallet-based transfers, block linkage, and chain validation through hands-on command usage.

My focus was improving the transfer flow (`send`), including stronger argument handling, clearer behavior documentation, and UML artifacts that explain the design and data flow to future contributors.

## Summary of Contributions

### Code contributed
- [Code Dashboard link: [ADD_YOUR_DASHBOARD_LINK_HERE](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=jainam&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=)]

### Enhancements implemented
- Refactored `SendCommand` to improve readability and maintainability while preserving behavior.
- Reduced parsing duplication by introducing reusable token parsing helpers and extraction methods.
- Split long logic in `execute(...)` into focused helper methods (amount parsing, speed resolution, fee validation, summary printing).
- Preserved and validated existing behavior through targeted tests (`SendCommandTest`).

### Contributions to the User Guide
- Updated `send` command documentation to keep command format and behavior clear.
- Added a `Coming Soon` section describing planned account-switching capability and persistence scope.

### Contributions to the Developer Guide
- Added a dedicated `SendCommand` class diagram source:
  - `docs/diagrams/SendCommandClassDiagram.puml`
- Added/updated DG sections describing:
  - `send` command implementation details
  - design rationale and dependencies around `SendCommand`
  - UML diagram references for maintainers

### Contributions to team-based tasks
- Updated project documentation structure and cross-references between DG and diagram sources.
- Maintained branch hygiene by creating focused branches and commits for separate concerns (code quality vs documentation).

### Review/mentoring contributions
- [ADD_LINK_TO_REVIEW_1]
- [ADD_LINK_TO_REVIEW_2]
- [ADD_EXAMPLE_OF_HELPING_TEAMMATE]

### Contributions beyond the project team
- [ADD_BUG_REPORT_LINK_FOR_OTHER_TEAM]
- [ADD_FORUM_OR_TECHNICAL_SHARING_LINK]

## Optional: Developer Guide extracts
### SendCommand implementation and structure
I documented the send flow and design responsibilities in the DG, emphasizing:
- command-level validation responsibilities,
- delegation to `TransactionRecordingService`, and
- the UML view of static dependencies used in transfer execution.

### UML diagrams contributed
- `docs/diagrams/SendCommandClassDiagram.puml`

## Optional: User Guide extracts
### `send` command
I contributed to user-facing documentation of the `send` command format, required and optional parameters, and fee/speed behavior.

### Coming Soon
I added a planned feature note on account switching to communicate upcoming usability and persistence improvements.
