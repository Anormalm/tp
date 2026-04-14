package seedu.crypto1010.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Block;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

class TransactionRecordingServiceTest {
    private static final String ETH_ADDRESS = "0x1111111111111111111111111111111111111111";

    @Test
    void recordTransfer_validRequest_recordsBlockchainAndHistory() throws Crypto1010Exception {
        // createDefault() has genesis only (block 0)
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        Wallet bob = walletManager.createWallet("bob");
        // manually fund bob for this test - bypasses CreateCommand auto-funding
        blockchain.addTransactions(List.of("network -> bob : 5"));
        // blockchain is now: block 0 (genesis), block 1 (funding)

        TransactionRecordingService service = new TransactionRecordingService(walletManager);
        TransferRequest request = new TransferRequest(
                "bob",
                ETH_ADDRESS,
                new BigDecimal("1"),
                "standard",
                new BigDecimal("0.0010"),
                "rent");

        service.recordTransfer(request, blockchain);

        // send lands at block 2
        Block latestBlock = blockchain.getBlock(2);
        assertEquals(List.of(
                "bob -> " + ETH_ADDRESS + " : 1",
                "bob -> network-fee : 0.001"), latestBlock.getTransactions());
        // history: amt should reflect actual amount sent (1), not 2
        assertEquals(List.of("to/" + ETH_ADDRESS + " amt/1 speed/standard fee/0.001 note/rent"),
                     bob.getTransactionHistory());
    }

    @Test
    void recordTransfer_walletNotFound_throwsAndLeavesStateUnchanged() {
        // createDefault() has genesis only (block 0)
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        TransactionRecordingService service = new TransactionRecordingService(walletManager);
        TransferRequest request = new TransferRequest(
                "ghost",
                ETH_ADDRESS,
                new BigDecimal("1"),
                "standard",
                BigDecimal.ZERO,
                null);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> service.recordTransfer(request, blockchain));

        assertEquals("Error: Wallet not found.", exception.getMessage());
        // only genesis block, no changes
        assertEquals(1, blockchain.size());
        assertTrue(walletManager.getWallets().isEmpty());
    }

    @Test
    void recordTransfer_insufficientBalance_throwsAndLeavesStateUnchanged() throws Crypto1010Exception {
        // createDefault() has genesis only (block 0)
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        Wallet alice = walletManager.createWallet("alice");
        // manually fund alice with only 0.5 - guaranteed insufficient for amount 1
        blockchain.addTransactions(List.of("network -> alice : 0.5"));
        // blockchain: block 0 (genesis), block 1 (funding)

        TransactionRecordingService service = new TransactionRecordingService(walletManager);
        TransferRequest request = new TransferRequest(
                "alice",
                ETH_ADDRESS,
                new BigDecimal("1"),
                "standard",
                new BigDecimal("0.0010"),
                null);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> service.recordTransfer(request, blockchain));

        assertEquals("Error: Insufficient balance.", exception.getMessage());
        // still block 0 and block 1, send was rejected
        assertEquals(2, blockchain.size());
        assertTrue(alice.getTransactionHistory().isEmpty());
    }
}
