# Crypto1010 User Guide

## Introduction
Crypto1010 is a command-line blockchain wallet simulator. It supports wallet creation, key generation, transfers, balance queries, and blockchain validation.

The application is designed for educational use and records transactions in a simple blockchain persisted as JSON.

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

## Command Format
- Commands are case-sensitive.
- The first token is the command word (for example, `send`, `balance`).
- Arguments are separated by spaces.
- For `send`, use the required prefixes exactly: `w/`, `to/`, `amt/`.

## Features

### `help`: Display command help
Format: `help [COMMAND]`

- If no command is provided (or an invalid one is provided), all commands are listed.
- If a valid command is provided, detailed help for that command is shown.

Examples:
- `help`
- `help send`

### `create`: Create a wallet
Format: `create NAME`

- Creates a wallet in memory for the current session.
- Wallet names are unique (case-insensitive).

Examples:
- `create alice`
- `create bob`

### `list`: List wallets
Format: `list`

- Shows all wallets created in the current session.

### `keygen`: Generate keys for a wallet
Format: `keygen w/NAME`

- Generates a public/private key pair for an existing wallet.
- Fails if the wallet does not exist.

Example:
- `keygen w/alice`

### `balance`: Show wallet balance
Format: `balance NAME`

- Computes balance from blockchain transactions.
- Prints up to 8 decimal places.

Example:
- `balance bob`

### `send`: Create a transfer transaction
Format: `send w/WALLET_NAME to/RECIPIENT_ADDRESS amt/AMOUNT [speed/SPEED] [fee/FEE] [note/MEMO]`

- Required arguments: `w/`, `to/`, `amt/`
- Optional arguments: `speed/`, `fee/`, `note/`
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
- `INDEX` must be a non-negative integer.

Example:
- `viewblock 2`

### `exit`: Save and terminate
Format: `exit`

- Saves blockchain data and exits the program.

## Data and Persistence
- Blockchain data is stored in `data/blockchain.json`.
- Transaction history persists across runs.
- Wallet definitions are in-memory only in the current version; recreate wallets when starting a new session.

## FAQ
**Q**: Where is my blockchain data stored?  
**A**: In `data/blockchain.json`.

**Q**: Why does `send` say wallet not found after restart?  
**A**: Wallets are not persisted in this version. Create the wallet again using `create NAME`.

**Q**: Can I transfer to a wallet name directly?  
**A**: No. `send` requires a recipient address string in `to/`.

## Command Summary
- `help [COMMAND]`
- `create NAME`
- `list`
- `keygen w/NAME`
- `balance NAME`
- `send w/WALLET_NAME to/RECIPIENT_ADDRESS amt/AMOUNT [speed/SPEED] [fee/FEE] [note/MEMO]`
- `validate`
- `viewblock INDEX`
- `exit`
