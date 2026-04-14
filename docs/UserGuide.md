# Crypto1010 User Guide

## Introduction
Crypto1010 is a command-line blockchain wallet simulator. It supports account login/registration, wallet creation, key generation, wallet-to-address transfers, account-to-account transfers, balance queries, wallet history lookup, and blockchain validation.

The application is designed for educational use and records transactions in a simple blockchain persisted as JSON. Each account has its own isolated wallets, blockchain data, and transaction history after login.

---
## Table of Contents
- [Quick Start](#ug-quick-start)
- [Startup Authentication](#ug-startup-authentication)
- [Features](#ug-features)
  - [CLI Productivity Features](#ug-cli-productivity-features)
  - [Display command help: `help`](#cmd-help)
  - [Enter tutorial mode: `tutorial`](#cmd-tutorial)
  - [Create a wallet: `create`](#cmd-create)
  - [List wallets: `list`](#cmd-list)
  - [Generate keys for a wallet: `keygen`](#cmd-keygen)
  - [Show wallet balance: `balance`](#cmd-balance)
  - [Create a transfer transaction: `send`](#cmd-send)
  - [Cross-account transfer: `crossSend`](#cmd-crosssend)
  - [Show wallet send history: `history`](#cmd-history)
  - [Validate blockchain integrity: `validate`](#cmd-validate)
  - [View blockchain overview: `viewchain`](#cmd-viewchain)
  - [View one block: `viewblock`](#cmd-viewblock)
  - [Log out of the current account: `logout`](#cmd-logout)
  - [Save and terminate: `exit`](#cmd-exit)
- [Coming Soon](#ug-coming-soon)
- [Command Summary](#ug-command-summary)
- [Data and Persistence](#ug-data-and-persistence)
- [FAQ](#ug-faq)
---
<span id="ug-quick-start"></span>
## Quick Start
1. Install Java 17.
1. Download the latest `Crypto1010.jar` release file.
1. Open a terminal in the folder containing the jar and run:
   ```bash
   java -jar Crypto1010.jar
   ```
1. Enter commands in the terminal.
1. At startup, choose `login` or `register`, then enter your username and password to access your account-specific wallets and blockchain data.
1. (For developers running from source) clone this repository and run:
   ```bash
   ./gradlew run
   ```
   On Windows PowerShell:
   ```powershell
   .\gradlew run
   ```

---
<span id="ug-startup-authentication"></span>
## Startup Authentication
- On launch, Crypto1010 requires an account before loading any wallets or blockchain data.
- Choose `register` if you are a new user. Registration logs you in immediately after the account is created.
- Choose `login` if you already have an account.
- Use `logout` after login if you want to return to account access and switch users without closing the app.
- Before login, the account menu supports tab suggestions for `1`, `2`, `3`, `login`, `register`, and `exit`.
- Usernames are case-insensitive and must be 3-20 characters using letters, numbers, `_`, or `-`.
- Passwords must be at least 6 characters long.
- Passwords are stored as salted PBKDF2 hashes (not plaintext).
- After 5 failed login attempts for the same username, login is temporarily locked for 30 seconds.

<span id="ug-features"></span>
## Features
<span id="ug-cli-productivity-features"></span>
### CLI Productivity Features
- On launch, Crypto1010 prints an ASCII logo and startup slogan.
- During authenticated command mode, the prompt is `USERNAME@crypto1010 ~`.
- Tab auto-completion is context-aware:
  - Before login: suggests only `1`, `2`, `3`, `login`, `register`, `exit`.
  - After login: suggests command words and relevant prefixes/values (for example `w/`, `curr/`, `speed/`).
  - After logout: returns to pre-login suggestion scope.
- Tab completion requires a non-dumb interactive terminal. It may be unavailable in some IDE run consoles.

### Command Formatting
+ First token must be the command word.  
  e.g. in `viewblock INDEX`  
  [OK] `viewblock 2`  
  [X] `2 viewblock`
+ Words in `UPPER_CASE` are required parameters.  
  e.g. in `viewblock INDEX`  
  [OK] `viewblock 2`  
  [X] `viewblock`
+ Parameters in `[UPPER_CASE]` are optional.  
  e.g. in `help [c/COMMAND]`  
  [OK] `help`  
  [OK] `help c/create`
+ Prefix parameters must include the exact prefix.  
  e.g. in `create w/WALLET_NAME`  
  [OK] `create w/alice`  
  [X] `create alice`  
  [X] `create name/alice`
+ Parameters must be separated by spaces.  
  e.g. in `send w/WALLET_NAME to/RECIPIENT_ADDRESS amt/AMOUNT`  
  [OK] `send w/bob to/0x1111111111111111111111111111111111111111 amt/1.5`  
  [OK] `send    w/bob    to/0x1111111111111111111111111111111111111111    amt/1.5`  
  [X] `send w/bobto/0x1111111111111111111111111111111111111111amt/1.5`
+ Number parameters must be numeric and non-negative where required by the command.  
  e.g. in `viewblock INDEX`  
  [OK] `viewblock 2`  
  [X] `viewblock two`  
  [X] `viewblock -2`
+ Commands without parameters ignore extra trailing text.  
  e.g. `validate anything` is interpreted as `validate`.
+ Very long command lines are rejected.  
  Input must be 512 characters or fewer.

<span id="cmd-help"></span>
### `help`: Display command help
Format: `help [c/COMMAND]`

- If no command is provided, all commands are listed.
- If a valid command is provided, detailed help for that command is shown.

Examples:
- `help`
- `help c/send`

<span id="cmd-tutorial"></span>
### `tutorial`: Enter tutorial mode
Format: `tutorial start`

- Enters an interactive tutorial mode that guides you through the basic features of Crypto1010 step by step.
- Uses isolated temporary tutorial data and does not affect your account data.
- Type `tutorial exit` to leave tutorial mode.
- Type `exit` during tutorial to exit the app globally.

<span id="cmd-create"></span>
### `create`: Create a wallet
Format: `create w/WALLET_NAME [curr/CURRENCY]`

- Creates a wallet for the current account and persists it on save.
- Wallet names are unique (case-insensitive).
- `CURRENCY` can only be `eth` or `btc` for ethereum and bitcoin wallet types respectively.
- `curr/` is optional. Not including `curr/CURRENCY` results in a wallet with generic currency code.
- A wallet tagged with a specific currency can be used by `crossSend`.
- At most one wallet per specific currency is allowed in the same account.

Examples:
- `create w/alice`
- `create w/bob`
- `create w/main curr/btc`

<span id="cmd-list"></span>
### `list`: List wallets
Format: `list`

- Shows all wallets in the current account (including previously saved wallets loaded at login).
- Wallets created with a specific currency display that currency in the list.
- Wallets with keys generated will show address in the format of currency code.

<span id="cmd-keygen"></span>
### `keygen`: Generate keys for a wallet
Format: `keygen w/WALLET_NAME`

- Generates a public/private key pair for an existing wallet.
- Fails if the wallet does not exist.
- Fails if that wallet already has keys (key regeneration is blocked).
- Generates a wallet address for that wallet based on wallet's currency code.
- Key generation is required if you want this wallet to have a local address (for receiving to that local address).
- `send` does not require sender key generation.

Example:
- `keygen w/alice`

<span id="cmd-balance"></span>
### `balance`: Show wallet balance
Format: `balance w/WALLET_NAME`

- Computes balance from blockchain transactions.
- Prints up to 8 decimal places.

Example:
- `balance w/bob`

<span id="cmd-send"></span>
### `send`: Create a transfer transaction
Format: `send w/WALLET_NAME to/RECIPIENT_ADDRESS amt/AMOUNT [speed/SPEED] [fee/FEE] [note/MEMO]`

- Supported speed values: `slow`, `standard`, `fast`
- If `fee/` is omitted, fee is chosen by speed:
  - `slow`: `0.0005`
  - `standard`: `0.0010`
  - `fast`: `0.0020`
- If `fee/` is provided, it overrides speed-based fee.
- Address validation supports Ethereum and legacy Bitcoin address formats.
- Total deduction = `AMOUNT + FEE`.
- `note/` captures the remainder of input after it appears.
- `note/` must be placed at the last position.

Examples:
- `send w/bob to/0x1111111111111111111111111111111111111111 amt/1.5`
- `send w/bob to/0x1111111111111111111111111111111111111111 amt/2 speed/fast`
- `send w/bob to/0x1111111111111111111111111111111111111111 amt/2 fee/0.02 note/Urgent payment`

<span id="cmd-crosssend"></span>
### `crossSend`: Cross-account transfer
Format: `crossSend acc/ACCOUNT_NAME amt/AMOUNT curr/CURRENCY`

- Transfers `AMOUNT` from the current account's wallet tagged with `CURRENCY` to another account user.
- Only same-currency transfer is supported. No exchange or conversion is performed.
- The recipient account must exist.
- If the recipient account does not already have a wallet for `CURRENCY`, Crypto1010 creates one automatically.
- The current account must have exactly one wallet tagged with that `CURRENCY`, and it must have enough balance.

Examples:
- `crossSend acc/alice amt/2 curr/btc`
- `crossSend acc/bob amt/0.5 curr/eth`

<span id="cmd-history"></span>
### `history`: Show wallet send history
Format: `history w/WALLET_NAME`

- Shows the recorded outgoing transaction history for the wallet.
- Entries are displayed in chronological order, oldest first.
- If the wallet has no recorded send history, the app will say so.

Example:
- `history w/bob`

<span id="cmd-validate"></span>
### `validate`: Validate blockchain integrity
Format: `validate`

- Verifies hashes, previous-hash links, and transaction data quality for all blocks.
- Reports either success or the first detected failure reason.

<span id="cmd-viewchain"></span>
### `viewchain`: View blockchain overview
Format: `viewchain`

- Displays a blockchain summary with:
  - total number of blocks
  - total number of transactions across all blocks
  - compact list of blocks (index, transaction count, timestamp, shortened hash)

Example:
- `viewchain`

<span id="cmd-viewblock"></span>
### `viewblock`: View one block
Format: `viewblock INDEX`

- Shows block index, timestamp, previous hash, current hash, and all transactions.

Example:
- `viewblock 2`

<span id="cmd-logout"></span>
### `logout`: Log out of the current account
Format: `logout`

- Returns you to the account access menu without closing Crypto1010.
- After entering `logout`, Crypto1010 prompts for confirmation.
- Type `y` to confirm logout or `n` to stay in the current account.

<span id="cmd-exit"></span>
### `exit`: Save and terminate
Format: `exit`

- Exits the program.
- Data is saved when the current account data was loaded successfully.
- If load failed due to corrupted data, save is intentionally disabled to avoid overwriting files.
- If a save operation fails, the app exits to prevent further inconsistency.

---
<span id="ug-coming-soon"></span>
## Coming Soon
Based on planned work tracked in project discussions/issues, the next user-facing feature is:

### Cross-account address discovery (planned)
- Resolve local wallet addresses across accounts without requiring a direct account name transfer command.

This feature is not available yet in the current release.

---
<span id="ug-command-summary"></span>
## Command Summary
- `help [c/COMMAND]`
- `tutorial start`
- `create w/WALLET_NAME [curr/CURRENCY]`
- `list`
- `keygen w/WALLET_NAME`
- `balance w/WALLET_NAME`
- `send w/WALLET_NAME to/RECIPIENT_ADDRESS amt/AMOUNT [speed/SPEED] [fee/FEE] [note/MEMO]`
- `crossSend acc/ACCOUNT_NAME amt/AMOUNT curr/CURRENCY`
- `history w/WALLET_NAME`
- `validate`
- `viewchain`
- `viewblock INDEX`
- `logout`
- `exit`

---
<span id="ug-data-and-persistence"></span>
## Data and Persistence
- Account credentials are stored in `data/accounts/credentials.txt`.
- Credential-signing key is stored in `data/accounts/credentials.key`.
- Each account has its own blockchain data at `data/accounts/USERNAME/blockchain.json`.
- Each account has its own wallet names, wallet currencies, wallet send history, wallet address, and wallet key pairs saved at `data/accounts/USERNAME/wallets.txt`.
- Due to the nature of cryptocurrency and blockchain, as well as the intentional functionalities of balance and currency, it is advised not to tamper with saved values
as it can easily corrupt and prevent loading into new sessions, and hence saving of new data.
- Missing or blank blockchain files are treated as no data yet and default data is loaded.
- Corrupted blockchain or wallet data triggers safe fallback, and saving is disabled to avoid overwriting that account's files.
- Credential data supports an HMAC-signed format. Signed credential records are verified on load.

---
<span id="ug-faq"></span>
## FAQ
**Q**: Do different users share wallets and blockchain data?  
**A**: No. Each login account gets its own wallet list and blockchain file under its account directory.

**Q**: Where is my blockchain data stored?  
**A**: In `data/accounts/USERNAME/blockchain.json` for the currently logged-in account.

**Q**: Can I access and change wallet attributes in the text file directly?  
**A**: Tampering with saved files is heavily discouraged as many attributes are cryptographically determined, hence tampering can easily cause issues.
In the program, many edits to the save files will cause corruption, hence the file data will often not be loaded for safety purposes.

**Q**: Why am I blocked from login even with the correct password?  
**A**: After repeated failed attempts, that username is locked for 30 seconds. Wait and retry.

**Q**: Can I transfer to a wallet name directly?  
**A**: `send` requires a recipient address string in `to/`. For direct account-to-account transfer, use `crossSend acc/ACCOUNT_NAME amt/AMOUNT curr/CURRENCY`.

**Q**: What does `history` show?  
**A**: `history w/WALLET_NAME` shows the wallet's recorded outgoing send history, not every blockchain transfer involving that wallet.
