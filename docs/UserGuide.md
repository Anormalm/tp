# Crypto1010 User Guide  
## Introduction
Crypto1010 is a command-line blockchain wallet simulator. It supports wallet creation, key generation, transfers, balance queries, and blockchain validation.

The application is designed for educational use and records transactions in a simple blockchain persisted as JSON.

---
## Table of Contents
+ #### [Quick Start](#quick-start)
+ #### [Features](#features)
  + #### [Display command help: `help`](#help-display-command-help)
  + #### [Create a wallet: `create`](#create-create-a-wallet)
  + #### [List wallets: `list`](#list-list-wallets)
  + #### [Generate keys for a wallet: `keygen`](#keygen-generate-keys-for-a-wallet)
  + #### [Show wallet balance: `balance`](#balance-show-wallet-balance)
  + #### [Create a transfer transaction: `send`](#send-create-a-transfer-transaction)
  + #### [Validate blockchain integrity: `validate`](#validate-validate-blockchain-integrity)
  + #### [View one block: `viewblock`](#viewblock-view-one-block)
  + #### [Save and terminate: `exit`](#exit-save-and-terminate)
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
---
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
    e.g. in `help [COMMAND]`  
    ✅ `help`  
    ✅ `help create`  
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
> + Parameters in the format `/type UPPER_CASE` will ignore all spaces after `/type`.  
    e.g. in `create w/WALLET_NAME`  
    `create w/     alice` &rarr; `Wallet created: alice`  
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
### `help`: Display command help
Format: `help [COMMAND]`

- If no command is provided (or an invalid one is provided), all commands are listed.
- If a valid command is provided, detailed help for that command is shown.

Examples:
- `help`
- `help send`

### `create`: Create a wallet
Format: `create w/WALLET_NAME`

- Creates a wallet in memory for the current session.
- Wallet names are unique (case-insensitive).

Examples:
- `create w/alice`
- `create w/bob`

### `list`: List wallets
Format: `list`

- Shows all wallets created in the current session.

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

### `validate`: Validate blockchain integrity
Format: `validate`

- Verifies hashes, previous-hash links, and transaction data quality for all blocks.
- Reports either success or the first detected failure reason.

### `viewblock`: View one block
Format: `viewblock INDEX`

- Shows block index, timestamp, previous hash, current hash, and all transactions.

Example:
- `viewblock 2`

### `exit`: Save and terminate
Format: `exit`

- Saves blockchain data and exits the program.
---
## Command Summary
- `help [COMMAND]`
- `create w/WALLET_NAME`
- `list`
- `keygen w/WALLET_NAME`
- `balance w/WALLET_NAME`
- `send w/WALLET_NAME to/RECIPIENT_ADDRESS amt/AMOUNT [speed/SPEED] [fee/FEE] [note/MEMO]`
- `validate`
- `viewblock INDEX`
- `exit`
---
## Data and Persistence
- Blockchain data is stored in `data/blockchain.json`.
- Transaction history persists across runs.
- Wallet definitions are in-memory only in the current version; recreate wallets when starting a new session.
---
## FAQ
**Q**: Where is my blockchain data stored?  
**A**: In `data/blockchain.json`.

**Q**: Why does `send` say wallet not found after restart?  
**A**: Wallets are not persisted in this version. Create the wallet again using `create NAME`.

**Q**: Can I transfer to a wallet name directly?  
**A**: No. `send` requires a recipient address string in `to/`.
