# Crypto1010 User Guide

## Introduction
Crypto1010 is a command-line blockchain wallet simulator. It supports account login/registration, wallet creation, key generation, wallet-to-address transfers, account-to-account transfers, balance queries, wallet history lookup, and blockchain validation.

The application is designed for educational use and records transactions in a simple blockchain persisted as JSON. Each account has its own isolated wallets, blockchain data, and transaction history after login.

---
## Table of Contents
+ #### [Quick Start](#quick-start)
+ #### [Startup Authentication](#startup-authentication)
+ #### [Features](#features)
  + #### [Display command help: `help`](#help-display-command-help)
  + #### [Enter tutorial mode: `tutorial`](#tutorial-enter-tutorial-mode)
  + #### [Create a wallet: `create`](#create-create-a-wallet)
  + #### [List wallets: `list`](#list-list-wallets)
  + #### [Generate keys for a wallet: `keygen`](#keygen-generate-keys-for-a-wallet)
  + #### [Show wallet balance: `balance`](#balance-show-wallet-balance)
  + #### [Create a transfer transaction: `send`](#send-create-a-transfer-transaction)
  + #### [Cross-account transfer: `crossSend`](#crosssend-cross-account-transfer)
  + #### [Show wallet send history: `history`](#history-show-wallet-send-history)
  + #### [Validate blockchain integrity: `validate`](#validate-validate-blockchain-integrity)
  + #### [View blockchain overview: `viewchain`](#viewchain-view-blockchain-overview)
  + #### [View one block: `viewblock`](#viewblock-view-one-block)
  + #### [Log out of the current account: `logout`](#logout-log-out-of-the-current-account)
  + #### [Save and terminate: `exit`](#exit-save-and-terminate)
+ #### [Coming Soon](#coming-soon)
+ #### [Command Summary](#command-summary)
+ #### [Data and Persistence](#data-and-persistence)
+ #### [FAQ](#faq)
---
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
## Startup Authentication
- On launch, Crypto1010 requires an account before loading any wallets or blockchain data.
- Choose `register` if you are a new user. Registration logs you in immediately after the account is created.
- Choose `login` if you already have an account.
- Use `logout` after login if you want to return to account access and switch users without closing the app.
- Usernames are case-insensitive and must be 3-20 characters using letters, numbers, `_`, or `-`.
- Passwords must be at least 6 characters long.

## Features
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

### `help`: Display command help
Format: `help [c/COMMAND]`

- If no command is provided, all commands are listed.
- If a valid command is provided, detailed help for that command is shown.

Examples:
- `help`
- `help c/send`

### `tutorial`: Enter tutorial mode
Format: `tutorial start`

- Enters an interactive tutorial mode that guides you through the basic features of Crypto1010 step by step.
- Uses isolated temporary tutorial data and does not affect your account data.
- Type `tutorial exit` to leave tutorial mode.
- Type `exit` during tutorial to exit the app globally.

### `create`: Create a wallet
Format: `create w/WALLET_NAME [curr/CURRENCY]`

- Creates a wallet for the current account and persists it on save.
- Wallet names are unique (case-insensitive).
- `curr/` is optional.
- A wallet tagged with a specific currency can be used by `crossSend`.
- At most one wallet per specific currency is allowed in the same account.

Examples:
- `create w/alice`
- `create w/bob`
- `create w/main curr/btc`

### `list`: List wallets
Format: `list`

- Shows all wallets in the current account (including previously saved wallets loaded at login).
- Wallets created with a specific currency display that currency in the list.

### `keygen`: Generate keys for a wallet
Format: `keygen w/WALLET_NAME`

- Generates a public/private key pair for an existing wallet.
- Fails if the wallet does not exist.
- Generates a wallet address for that wallet.
- Key generation is required if you want this wallet to have a local address (for receiving to that local address).
- `send` does not require sender key generation.

Example:
- `keygen w/alice`

### `balance`: Show wallet balance
Format: `balance w/WALLET_NAME`

- Computes balance from blockchain transactions.
- Prints up to 8 decimal places.

Example:
- `balance w/bob`

### `send`: Create a transfer transaction
Format: `send w/WALLET_NAME to/RECIPIENT_ADDRESS amt/AMOUNT [speed/SPEED] [fee/FEE] [note/MEMO]`

- Supported speed values: `slow`, `standard`, `fast`
- If `fee/` is omitted, fee is chosen by speed:
  - `slow`: `0.0005`
  - `standard`: `0.0010`
  - `fast`: `0.0020`
- If `fee/` is provided, it overrides speed-based fee.
- Address validation supports Ethereum, Bitcoin, and Solana address formats.
- Total deduction = `AMOUNT + FEE`.
- `note/` captures the remainder of input after it appears, so place it last.

Examples:
- `send w/bob to/0x1111111111111111111111111111111111111111 amt/1.5`
- `send w/bob to/0x1111111111111111111111111111111111111111 amt/2 speed/fast`
- `send w/bob to/0x1111111111111111111111111111111111111111 amt/2 fee/0.02 note/Urgent payment`

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

### `history`: Show wallet send history
Format: `history w/WALLET_NAME`

- Shows the recorded outgoing transaction history for the wallet.
- Entries are displayed in chronological order, oldest first.
- If the wallet has no recorded send history, the app will say so.

Example:
- `history w/bob`

### `validate`: Validate blockchain integrity
Format: `validate`

- Verifies hashes, previous-hash links, and transaction data quality for all blocks.
- Reports either success or the first detected failure reason.

### `viewchain`: View blockchain overview
Format: `viewchain`

- Displays a blockchain summary with:
  - total number of blocks
  - total number of transactions across all blocks
  - compact list of blocks (index, transaction count, timestamp, shortened hash)

Example:
- `viewchain`

### `viewblock`: View one block
Format: `viewblock INDEX`

- Shows block index, timestamp, previous hash, current hash, and all transactions.

Example:
- `viewblock 2`

### `logout`: Log out of the current account
Format: `logout`

- Returns you to the account access menu without closing Crypto1010.
- After entering `logout`, Crypto1010 prompts for confirmation.
- Type `y` to confirm logout or `n` to stay in the current account.

### `exit`: Save and terminate
Format: `exit`

- Exits the program.
- Data is saved when the current account data was loaded successfully.
- If load failed due to corrupted data, save is intentionally disabled to avoid overwriting files.

---
## Coming Soon
Based on planned work tracked in project discussions/issues, the next user-facing feature is:

### Cross-account address discovery (planned)
- Resolve local wallet addresses across accounts without requiring a direct account name transfer command.
- Persist generated keys and wallet addresses across restarts so account-to-account interactions are easier to continue.

This feature is not available yet in the current release.

---
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
## Data and Persistence
- Account credentials are stored in `data/accounts/credentials.txt`.
- Each account has its own blockchain data at `data/accounts/USERNAME/blockchain.json`.
- Each account has its own wallet names, wallet currencies, and wallet send history at `data/accounts/USERNAME/wallets.txt`.
- Generated keys and wallet addresses are not currently persisted; run `keygen` again after restarting if you need an address.
- Missing or blank blockchain files are treated as no data yet and default data is loaded.
- Corrupted blockchain or wallet data triggers safe fallback, and saving is disabled to avoid overwriting that account's files.

---
## FAQ
**Q**: Do different users share wallets and blockchain data?  
**A**: No. Each login account gets its own wallet list and blockchain file under its account directory.

**Q**: Where is my blockchain data stored?  
**A**: In `data/accounts/USERNAME/blockchain.json` for the currently logged-in account.

**Q**: Why is my wallet address missing after restart?  
**A**: Wallet names and send history are persisted, but generated keys and wallet addresses are not. Run `keygen w/WALLET_NAME` again.

**Q**: Can I transfer to a wallet name directly?  
**A**: `send` requires a recipient address string in `to/`. For direct account-to-account transfer, use `crossSend acc/ACCOUNT_NAME amt/AMOUNT curr/CURRENCY`.

**Q**: What does `history` show?  
**A**: `history w/WALLET_NAME` shows the wallet's recorded outgoing send history, not every blockchain transfer involving that wallet.
