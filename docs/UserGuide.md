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
  + #### [Save and terminate: `exit`](#exit-save-and-terminate)
+ #### [Coming Soon](#coming-soon)
+ #### [Command Summary](#command-summary)
+ #### [Data and Persistence](#data-and-persistence)
+ #### [FAQ](#faq)
---
## Quick Start
1. Install Java 17.
1. Clone this repository and open it in a terminal.
1. Run the application:
   ```bash
   ./gradlew run
   ```
   On Windows PowerShell:
   ```powershell
   .\gradlew run
   ```
1. Enter commands in the terminal.
1. At startup, choose `login` or `register`, then enter your username and password to access your account-specific wallets and blockchain data.
<br>

---
## Startup Authentication
- On launch, Crypto1010 requires an account before loading any wallets or blockchain data.
- Choose `register` if you are a new user. Registration logs you in immediately after the account is created.
- Choose `login` if you already have an account.
- Usernames are case-insensitive and must be 3-20 characters using letters, numbers, `_`, or `-`.
- Passwords must be at least 6 characters long.

## Features
> [!NOTE]
> ### **Command Formatting:**
> + First tokens must always be the command word.  
    e.g. in `viewblock INDEX`,  
    ✅ `viewblock 2`  
    ❌ `2 viewblock`  
    <br/>
> + Words in `UPPER_CASE` are the parameters to be supplied by the user.  
    These parameters **MUST** be filled in.  
    e.g. in `viewblock INDEX`,  
    ✅ `viewblock 2`  
    ❌ `viewblock`  
    <br/>
> + Parameters in the format `[UPPER_CASE]` are optional.  
    e.g. in `help [c/COMMAND]`  
    ✅ `help`  
    ✅ `help c/create`  
    <br/>
> + Parameters in the format `/type UPPER_CASE` must include `/type` in the input.   
    e.g. in `create w/WALLET_NAME`  
    ✅ `create w/alice`  
    ❌ `create alice`  
    <br/>
> + Parameters in the format `/type UPPER_CASE` must include the exact `/type` in the input.   
    e.g. in `create w/WALLET_NAME`  
    ✅ `create w/alice`  
    ❌ `create name/alice`  
    <br/>
> + Parameters in the format `/type UPPER_CASE` are prefix-based, and spacing support may differ by command parser implementation.  
    e.g. in `create w/WALLET_NAME`  
    ✅ `create w/alice`  
    ❌ `create alice`  
    <br/>
> + Parameters must be separated by spaces.   
    e.g. in `send w/WALLET_NAME to/RECIPIENT_ADDRESS amt/AMOUNT`  
    ✅ `send w/bob to/0x1111111111111111111111111111111111111111 amt/1.5`  
    ✅ `send    w/bob    to/0x1111111111111111111111111111111111111111    amt/1.5`  
    ❌ `send w/bobto/0x1111111111111111111111111111111111111111amt/1.5`  
    <br/>
> + Parameters that are numbers must be written in numerical form not spelled out, and must be non-negative.  
    e.g in `mark TASK_INDEX`  
    ✅ `viewblock 2`  
    ❌ `viewblock two`   
    ❌ `viewblock -2`  
    <br/>
> + Commands that do not take in parameters will ignore any parameter provided.  
    Such commands include `validate`.  
    e.g. in `validate`  
    `validate dsja 2190` will be interpreted as `validate`  
    <br/>
<br>

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
- Guides users on using all the basic commands available
- Does not affect wallets and blockchains, all changes made are isolated and temporary
- User can exit this mode by typing `tutorial exit` at anytime

### `create`: Create a wallet
Format: `create w/WALLET_NAME [curr/CURRENCY]`

- Creates a wallet for the current account and persists it on save/exit.
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

- Shows all wallets created in the current session.
- Wallets created with a specific currency display that currency in the list.

### `keygen`: Generate keys for a wallet
Format: `keygen w/WALLET_NAME`

- Generates a public/private key pair for an existing wallet.
- Fails if the wallet does not exist.
- Must be done to send transactions as keygen also creates wallet address

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

### `exit`: Save and terminate
Format: `exit`

- Saves blockchain data and exits the program.

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
- `exit`
<br>

---
## Data and Persistence
- Account credentials are stored in `data/accounts/credentials.txt`.
- Each account has its own blockchain data at `data/accounts/USERNAME/blockchain.json`.
- Each account has its own wallet names, wallet currencies, and wallet send history at `data/accounts/USERNAME/wallets.txt`.
- Generated keys and wallet addresses are not currently persisted; run `keygen` again after restarting if you need an address.
<br>

---
## FAQ
**Q**: Do different users share wallets and blockchain data?  
**A**: No. Each login account gets its own wallet list and blockchain file under its account directory.

**Q**: Where is my blockchain data stored?  
**A**: In `data/accounts/USERNAME/blockchain.json` for the currently logged-in account.

**Q**: Why is my wallet address missing after restart?  
**A**: Wallet names and send history are persisted, but generated keys and wallet addresses are not. Run `keygen w/WALLET_NAME` again.

**Q**: Can I transfer to a wallet name directly?  
**A**: `send` still requires a recipient address string in `to/`. For direct account-to-account transfer, use `crossSend acc/ACCOUNT_NAME amt/AMOUNT curr/CURRENCY`.

**Q**: What does `history` show?  
**A**: `history w/WALLET_NAME` shows the wallet's recorded outgoing send history, not every blockchain transfer involving that wallet.
