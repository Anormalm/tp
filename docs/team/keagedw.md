# Keagan Edward Wangsakentjana - Project Portfolio Page

## Overview
Crypto1010 is a CLI focused cryptocurrency, blockchain, and digital wallet simulator, aimed to educate and familiarise students on the fundamentals of cryptocurrency. The program teaches students how to create wallets, key generation, crypto transactions, RSA encryption, blockchain validation, and more.

My main contribution was in the creation and integration of the public and private keys system (`keygen`), integral for wallet creation, addressing, and transaction.

## Summary of Contributions

### Code contributed
- [Functional code (Repo)](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=keagedw&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

### Enhancements implemented
- Implemented `keygen` functionality
- Created `KeyPair` class for use by `Wallet` that cryptographically derives private and public keys for wallet identity
- `KeyPair` cryptographically derives valid Ethereum or Bitcoin addresses depending on currency type of wallet.
- Implemented wallet address generation based on wallet currency identity to facilitate transactions
- Implemented key pair, address, and currency type persistence.

### Contributions to the User Guide
- Wrote main flow and content of the User Guide
- Included most functions
- Created table of contents
- Implemented section links

### Contributions to team-based tasks
- Helped align transaction behaviour by unifying `send` command behaviour to wallet address and key usage
- Helped align CreateCommand with Wallet and WalletManager to enforce currency and wallet type constraints
- Helped align send vs crossSend priority and functionality delineation
- Aligned currency across classes, ensuring common accepted formats and types
- Designed and Implemented multiple tests across modules including BlockChain, Wallet, WalletManager, 

### Bug fixing and triaging (PE-D focus)
- Resolved multiple bugs flagged from PE-D, particularly those related to Wallet, WalletManager, KeyPair,
CreateCommand, SendCommand, and BlockChain.
- Issues resolved:
  - `#169` (Users are able to overwrite the address of their wallets silently)
  - `#171` (`send` silently swallows `fee/` into note text when `fee/` is placed after `note/`)
  - `#176` (Insufficient wallet balance)
  - `#156` (Creating wallet `network` results in a permanent negative balance and unusable wallet)
  - `#147` (Undocumented limitation: Wallet keys can be overwritten by running multiple `keygen` commands)
