# Crypto1010 Developer Guide

## Acknowledgements
- Java Platform, Standard Edition 17 API documentation: https://docs.oracle.com/en/java/javase/17/docs/api/
- JUnit 5 User Guide (for unit testing approach and assertions): https://junit.org/junit5/docs/current/user-guide/
- Gradle User Manual (build and task orchestration): https://docs.gradle.org/current/userguide/userguide.html
- SHA-256 usage through Java `MessageDigest` API from the Java standard library documentation.

## Design & implementation
Crypto1010 is implemented as a modular command-line application with clear separation between authentication, input parsing, command execution, domain model, and persistence.

### High-level structure
- `Crypto1010` manages the main loop, input capture, and save/load lifecycle.
- `auth` package manages account registration, login, and password hashing.
- `Parser` maps raw user input to concrete command objects.
- `command` package implements user-facing functionality (`create`, `send`, `crossSend`, `balance`, etc.).
- `model` package contains core blockchain and wallet logic.
- `service` package centralizes transfer recording so blockchain writes and wallet history stay aligned, including cross-account transfers.
- `storage` package persists account credentials plus account-scoped blockchain and wallet data.

### Command execution flow
1. User authenticates through the startup login/register flow.
2. `Crypto1010` loads account-specific blockchain and wallet storage for the authenticated username.
3. User enters command text in the CLI.
4. `Parser` extracts command word and arguments.
5. A concrete `Command` subclass is instantiated.
6. `Command.execute(...)` mutates/queries model state.
7. `Crypto1010` saves account-scoped blockchain and wallet state after successful command execution.

### Adding a new command
- Add the new keyword and description to `CommandWord` so it is exposed through `help`.
- Implement a new `Command` subclass in the `command` package and keep command-specific validation there.
- Update `Parser.parse(...)` to return the new command and pass in `WalletManager` when the command needs wallet access.
- Add a focused JUnit test under `src/test/java/seedu/crypto1010/command` following the existing command test pattern.
- Add a manual test case in this guide so the CLI behaviour remains documented.

### Blockchain model
- A `Blockchain` stores an ordered list of `Block`.
- Each `Block` has:
  - index
  - timestamp
  - previous hash
  - transactions
  - current hash (SHA-256 of block payload)
- `validate()` verifies:
  - hash consistency
  - previous-hash linkage
  - transaction data quality

### Blockchain and Block subsystem
This section documents the enhancement: the blockchain core (`Blockchain`, `Block`) and integrity-first validation flow that powers `validate`, `send`, and persisted-chain loading.

#### Scope of enhancement
- Tamper-evident block representation with deterministic SHA-256 hashing.
- End-to-end chain integrity checks across index, hash linkage, and transaction semantics.
- Transaction-safe balance validation during chain verification.
- Controlled block append path for new transactions.
- Persistence gatekeeping: reject corrupted blockchain files at load time.

#### Architecture-level design
The blockchain subsystem is centered on two model classes:
- `Block`: immutable record of block payload fields and hash computation.
- `Blockchain`: ordered aggregate and the single authority for chain validation and block appending.

`ValidateCommand` delegates all integrity checks to `Blockchain.validate()`; command layer does not duplicate blockchain logic. `BlockchainStorage.load()` also invokes `validate()` after deserialization. This means both interactive validation and startup data loading use the same invariant checks, avoiding drift between runtime and persistence behavior.

At system level, the flow is:
1. Commands mutate/query blockchain through `Blockchain` APIs.
2. `Blockchain` owns block construction and linkage rules.
3. Storage serializes/deserializes raw state.
4. Validation logic gates acceptance of loaded data.

This keeps blockchain rules in one place and reduces the risk of inconsistent behavior between command paths.

#### Component-level design
`Block` design choices:
- Block state includes `index`, `timestamp`, `previousHash`, `transactions`, and `currentHash`.
- `computeCurrentHash()` recomputes SHA-256 from the full payload (`index|timestamp|previousHash|joinedTransactions`).
- `hasValidTransactionData()` enforces non-empty, non-blank transaction entries.

Why this design:
- Recompute support enables tamper detection without trusting stored hash values.
- Hashing full payload makes any transaction/link/timestamp modification detectable.
- Lightweight representation stays easy to serialize while still being integrity-aware.

`Blockchain` design choices:
- Internally stores blocks as a mutable list, exposed as unmodifiable view for read access.
- `addTransactions(...)` is the only append path: it always derives `newIndex = last.index + 1` and `previousHash = last.currentHash`.
- `validate()` runs deterministic checks in a fixed order and returns a structured `ValidationResult`.

Validation stages in `Blockchain.validate()`:
1. Structural checks:
   - chain non-empty
   - block index continuity (`block.index == iteration index`)
2. Cryptographic consistency:
   - `block.computeCurrentHash()` equals stored `block.currentHash`
3. Data quality:
   - no blank/null transactions
4. Genesis constraints:
   - previous hash equals fixed genesis predecessor
   - transaction data is exactly `Genesis Block`
5. Linkage checks for non-genesis blocks:
   - `block.previousHash == previousBlock.currentHash`
6. Economic/semantic checks:
   - transaction format must match `sender -> receiver : amount`
   - amount must be positive
   - sender must have sufficient running balance
   - exempt accounts (e.g., `network`, `network-fee`) bypass balance enforcement

#### Balance validation strategy
Validation uses a running `Map<String, BigDecimal>` keyed by normalized account names.
- For each parsed transaction:
  - sender balance is checked/decremented (unless exempt).
  - receiver balance is incremented (unless exempt).
- If any sender lacks funds at that point in chain order, validation fails immediately.

Rationale:
- Enforces causal ordering semantics (a transfer is only valid if funds existed before that transaction).
- Keeps validation deterministic and independent from wallet objects.
- Uses `BigDecimal` to avoid floating-point precision drift.

#### Block append behavior from transfer flow
`SendCommand` delegates to `TransactionRecordingService`, which composes one or two transaction strings (transfer + optional fee) and calls `Blockchain.addTransactions(...)`.

`Blockchain.addTransactions(...)`:
- rejects empty transaction batches.
- obtains the last block.
- builds a new block with current timestamp and last block hash as predecessor.
- computes new block hash at construction.
- appends to chain.

This ensures hash linkage is always created from canonical in-memory state, not externally supplied values.

#### Persistence interaction
`BlockchainStorage` serializes blocks to JSON and reconstructs them on load.
After parsing JSON into `Block` objects, it calls `validate()`. Invalid chains are rejected with an `IOException`, and app startup falls back to a safe default chain. This prevents partially tampered or malformed persisted state from silently entering runtime.

#### Alternatives considered
1. Recompute balance on demand for each transaction during validation without a running map:
   - Rejected because it is less efficient and harder to reason about for long chains.
2. Put transaction-format validation in command/service only:
   - Rejected because persisted data could bypass command checks; blockchain-level validation must be authoritative.
3. Allow direct block injection (`addBlock(Block)` API):
   - Rejected to reduce misuse risk. `addTransactions(...)` preserves index/hash derivation invariants.
4. Skip validation during storage load for faster startup:
   - Rejected because startup should fail-safe against tampered files.

#### Trade-offs and known limitations
- Chain validation currently scans all blocks each run (`O(n * txPerBlock)`), acceptable for project-scale data but not optimized for large ledgers.
- Hashing uses payload string concatenation; robust for current format but not yet versioned for schema evolution.
- Exempt account model is pragmatic for simulation and fees but not a full consensus/economic model.

#### Planned next-step extension (post-v2.1)
The current implementation validates a complete chain each time. A planned extension is an incremental validation cache:
- store the latest validated block hash and running balances snapshot.
- on append, validate only new blocks against cached state.
- invalidate cache automatically after file import/load mismatch.

Reason for planning this enhancement:
- keeps correctness guarantees while reducing repeated full-chain scans for larger datasets.
- preserves the current fail-safe model because full validation remains available as a fallback path.

#### UML diagrams for this enhancement (PlantUML)
This enhancement uses multiple UML diagram types to show both structure and runtime behavior:
- Validation Sequence: `docs/diagrams/BlockchainValidationSequence.puml`
- Append Sequence: `docs/diagrams/BlockAppendOnSendSequence.puml`
- Class: `docs/diagrams/BlockchainBlockClassDiagram.puml`


### Transaction and balance logic
Transactions are represented in this format:
`sender -> receiver : amount`

Balance for a wallet is computed by scanning all transactions:
- subtract amount when wallet is sender
- add amount when wallet is receiver

### Wallet currency tagging
- `Wallet` now stores an optional currency code in addition to its name.
- `create w/WALLET_NAME [curr/CURRENCY]` assigns the wallet a specific currency tag used by `crossSend`.
- Wallets without `curr/` are stored as `generic` and behave exactly like legacy wallets.
- `WalletStorage` remains backward-compatible:
  - old `W|name` lines load as `generic`
  - currency-tagged wallets persist as `W|name|currency`
- `WalletManager` enforces at most one wallet per specific currency per account so `crossSend curr/...` can resolve a sender wallet unambiguously.

### `send` command implementation
`SendCommand` uses prefix-based argument parsing:
- required: `w/`, `to/`, `amt/`
- optional: `speed/`, `fee/`, `note/`

Validation sequence:
1. parse prefixes
2. verify wallet exists
3. verify amount > 0
4. validate recipient address format
5. resolve fee (manual or speed-based)
6. pass the transfer to `TransactionRecordingService`

### SendCommand class diagram
The SendCommand class diagram documents the static structure of the send flow centered on command-level validation and service delegation.

Key design points shown in the diagram:
- `SendCommand` inherits from `Command`.
- `SendCommand` depends on `WalletManager` to validate sender wallet existence.
- `SendCommand` creates `TransferRequest` and delegates transfer persistence to `TransactionRecordingService`.
- `TransactionRecordingService` performs blockchain write operations through `Blockchain`.

Diagram source:
- `docs/diagrams/SendCommandClassDiagram.puml`

### Centralized transfer recording
- `TransactionRecordingService` is the single write path for successful transfers.
- It verifies the sender wallet exists and has sufficient balance for `amount + fee`.
- It records blockchain transactions and the sender wallet history from the same `TransferRequest`.
- Local recipient addresses are normalized to wallet names on-chain when a matching wallet exists.

### `crossSend` command implementation
- `CrossSendCommand` accepts `acc/`, `amt/`, and `curr/`.
- It resolves the sender from the current account's wallet tagged with the given currency.
- It verifies:
  - recipient account exists
  - sender and recipient accounts are different
  - amount is positive
  - the sender has sufficient balance
  - there is exactly one wallet for that currency in the current account
- `CrossAccountTransferService` loads the recipient account's wallet and blockchain storage, creates a recipient wallet for the same currency when missing, and appends mirrored transactions to the two account chains.
- Cross-account chain entries use an `external:` prefix, and `Blockchain.validate()` treats those synthetic accounts as exempt so recipient chains can accept inbound credit without requiring a local sender balance.

### `history` command implementation
- `HistoryCommand` reads the persisted wallet send history from `Wallet`.
- It validates `w/WALLET_NAME`, resolves the wallet case-insensitively through `WalletManager`, and prints numbered entries.
- The command is intentionally wallet-local: it shows recorded outgoing send history, not a reconstructed blockchain-wide ledger view.

### `viewchain` command implementation
- `ViewChainCommand` is a read-only blockchain overview command.
- It validates strict format (`viewchain` without extra arguments).
- It computes:
  - total blocks from `blockchain.getBlocks().size()`
  - total transactions by summing each block's transaction count
- It prints a compact per-block view containing index, transaction count, timestamp, and a shortened hash preview.
- This gives expert users a fast chain summary before drilling into individual blocks with `viewblock`.

### Persistence implementation
- `AccountStorage` persists hashed credentials in `data/accounts/credentials.txt`.
- `BlockchainStorage` serializes blockchain state to `data/accounts/USERNAME/blockchain.json`.
- `WalletStorage` persists wallet names, wallet currencies, and transaction history in `data/accounts/USERNAME/wallets.txt`.
- On startup, `Crypto1010` authenticates first, then loads blockchain and wallet data for the current account only.
- If loading fails, the app falls back to a default blockchain and/or an empty wallet list.

### UML diagrams
- Sequence diagram source: `docs/diagrams/BlockchainValidationSequence.puml`
- Sequence diagram source: `docs/diagrams/BlockAppendOnSendSequence.puml`
- Class diagram source: `docs/diagrams/BlockchainBlockClassDiagram.puml`
- Class diagram source: `docs/diagrams/SendCommandClassDiagram.puml`

## Product scope

### Target user profile
- Students and beginners learning blockchain fundamentals through a terminal-based workflow.
- Users who prefer lightweight, text-based interaction instead of a GUI.
- Developers who want a small Java codebase suitable for extension and experimentation.

### Value proposition
Crypto1010 provides a compact, practical environment to understand wallet transfers, block structure, hash linkage, and blockchain validation without requiring external infrastructure or a real network.

## User Stories

| Version | As a ... | I want to ... | So that I can ... |
|--------|----------|---------------|------------------|
| v1.0 | new user | view usage instructions | quickly learn available commands |
| v1.0 | user | create wallets | simulate distinct senders and receivers |
| v1.0 | user | list wallets | confirm available wallets in the current session |
| v1.0 | user | check wallet balance | verify transaction effects numerically |
| v1.0 | user | send funds with fee controls | model transfer and fee trade-offs |
| v2.1 | user | send funds to another account with the same currency | move balances between login accounts without exchanges |
| v1.0 | user | view my wallet send history | review past outgoing transfers |
| v1.0 | user | validate the blockchain | confirm chain integrity after modifications |
| v2.2 | expert user | view a blockchain overview | quickly inspect chain size and per-block summaries |
| v1.0 | user | inspect a specific block | view exact block-level transaction data |

### Planned enhancement: cross-account address resolution
- User story: As a user, I can send to another account using a wallet address instead of an account name.
- Persist generated wallet addresses across restarts.
- Extend recipient lookup beyond the current account without requiring `acc/ACCOUNT_NAME`.

## Non-Functional Requirements
- The application shall run on Java 17.
- The application shall be usable entirely via CLI input/output.
- Blockchain data shall persist locally in `data/accounts/USERNAME/blockchain.json`.
- Validation shall be deterministic for the same stored blockchain input.
- The codebase shall remain modular enough to support adding new commands with minimal cross-component changes.
- The project shall support automated unit testing via JUnit 5 and Gradle.

## Glossary
- Blockchain: Ordered chain of blocks linked by previous hashes.
- Block: A unit containing index, timestamp, previous hash, current hash, and transactions.
- Wallet: A logical identity used as sender/receiver in transactions.
- Transaction: A transfer record in the format `sender -> receiver : amount`.
- Validation: Integrity checks covering hash correctness, linkage, and transaction validity.
- Network fee account: Internal sink (`network-fee`) receiving fee deductions from `send`.

## Instructions for manual testing

### Prerequisites
- Java 17 installed and configured.
- Project cloned locally.

### Running the app
1. Run `./gradlew run` (or `.\gradlew run` on Windows PowerShell).

### Manual test cases
1. Authentication:
   - Launch the app.
   - Choose `register`.
   - Enter a username and password.
   - Expected: account is created and the app logs in to that account.
   - Relaunch the app and choose `login` with the same credentials.
   - Expected: login succeeds and the same account data is loaded.
1. Help
   - `help`
   - Expected: prints out the list of commands
   - `help c/list`
   - Expected: prints out details about the list command
2. Tutorial
   - `tutorial start`
   - Expected: begins interactive tutorial guiding through steps needed to make a simple transaction
   - `tutorial exit`
   - Expected: exits the interactive tutorial
3. Create wallets:
   - `create w/alice`
   - `create w/bob`
   - Expected: confirmation messages for each wallet.
   - `create w/main curr/btc`
   - Expected: wallet created message showing currency `btc`.
1. List wallets:
   - `list`
   - Expected: numbered wallet list including `alice`, `bob`, and any currency-tagged wallet with its currency shown.
1. Check balance:
   - `balance w/bob`
   - Expected: balance displayed with 8 decimal places.
1. Successful transfer:
   - `send w/bob to/0x1111111111111111111111111111111111111111 amt/1`
   - Expected: success output including wallet, recipient, amount, speed, and fee.
1. Invalid transfer format:
   - `send invalid`
   - Expected: invalid send format error.
1. Invalid recipient address:
   - `send w/bob to/not-an-address amt/1`
   - Expected: invalid recipient address error.
1. View wallet send history:
   - `history w/bob`
   - Expected: either numbered outgoing send history entries or a no-history message.
1. Successful cross-account transfer:
   - Register/login as account `sender`.
   - `create w/main curr/btc`
   - Ensure `main` has some balance in your test data.
   - Register a second account `receiver`.
   - `crossSend acc/receiver amt/1 curr/btc`
   - Expected: success output showing sender wallet, recipient account, recipient wallet, and currency.
   - Login as `receiver`.
   - `list`
   - Expected: a `btc` wallet exists if one was not already present.
1. Validate chain:
   - `validate`
   - Expected: valid-chain success message unless data is corrupted.
1. View blockchain overview:
   - `viewchain`
   - Expected: total block count, total transaction count, and compact block rows are printed.
1. View block details:
   - `viewblock 1`
   - Expected: full block metadata and transaction list.
1. Out-of-range block:
   - `viewblock 999`
   - Expected: block index out of range error.
1. Exit:
   - `exit`
   - Expected: program terminates and blockchain state is saved.

### Data reset / test isolation
- Delete or replace `data/accounts/USERNAME/blockchain.json` and `data/accounts/USERNAME/wallets.txt` to reset one account.
- Delete `data/accounts/credentials.txt` only if you also want to remove registered login accounts.
