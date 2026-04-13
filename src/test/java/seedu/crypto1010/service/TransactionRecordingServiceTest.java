package seedu.crypto1010.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Block;
import seedu.crypto1010.model.Blockchain;
// import seedu.crypto1010.model.KeyPair;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.math.BigDecimal;
// import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.Test;

class TransactionRecordingServiceTest {
    private static final String ETH_ADDRESS = "0x1111111111111111111111111111111111111111";

    @Test
    void recordTransfer_validRequest_recordsBlockchainAndHistory() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        Wallet bob = walletManager.createWallet("bob");
        TransactionRecordingService service = new TransactionRecordingService(walletManager);
        TransferRequest request = new TransferRequest(
                "bob",
                ETH_ADDRESS,
                new BigDecimal("2"),
                "standard",
                new BigDecimal("0.0010"),
                "rent");

        service.recordTransfer(request, blockchain);

        Block latestBlock = blockchain.getBlock(2);
        assertEquals(List.of(
                "bob -> " + ETH_ADDRESS + " : 2",
                "bob -> network-fee : 0.001"), latestBlock.getTransactions());
        assertEquals(List.of("to/" + ETH_ADDRESS + " amt/2 speed/standard fee/0.001 note/rent"),
                bob.getTransactionHistory());
    }

    //    @Test
    //    void recordTransfer_localRecipient_recordsCanonicalWalletNameOnBlockchain() throws Crypto1010Exception {
    //        Blockchain blockchain = Blockchain.createDefault();
    //        WalletManager walletManager = new WalletManager();
    //        walletManager.createWallet("bob");
    //        Wallet receiver = walletManager.createWallet("carol");
    //        receiver.setKeys(new Key[]{
    //            new Key(BigInteger.valueOf(3), BigInteger.valueOf(7), true),
    //            new Key(BigInteger.valueOf(3), BigInteger.valueOf(11), false)});
    //        TransactionRecordingService service = new TransactionRecordingService(walletManager);
    //        TransferRequest request = new TransferRequest(
    //                "bob",
    //                receiver.getAddress(),
    //                new BigDecimal("2"),
    //                "manual",
    //                BigDecimal.ZERO,
    //                null);
    //
    //        service.recordTransfer(request, blockchain);
    //
    //        assertEquals(List.of("bob -> carol : 2"), blockchain.getBlock(2).getTransactions());
    //    }

    @Test
    void recordTransfer_walletNotFound_throwsAndLeavesStateUnchanged() {
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
        assertEquals(2, blockchain.size());
        assertTrue(walletManager.getWallets().isEmpty());
    }

    @Test
    void recordTransfer_insufficientBalance_throwsAndLeavesStateUnchanged() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        Wallet alice = walletManager.createWallet("alice");
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
        assertEquals(2, blockchain.size());
        assertTrue(alice.getTransactionHistory().isEmpty());
    }
}
