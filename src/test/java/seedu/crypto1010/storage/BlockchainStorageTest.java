package seedu.crypto1010.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.model.Blockchain;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BlockchainStorageTest {
    @TempDir
    Path tempDir;

    private Path dataDir;
    private Path blockchainFile;

    @BeforeEach
    void setUp() {
        System.setProperty("crypto1010.dataDir", tempDir.toString());
        dataDir = tempDir;
        blockchainFile = dataDir.resolve("blockchain.json");
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("crypto1010.dataDir");
    }

    @Test
    void saveThenLoad_persistsBlockchainData() throws IOException {
        BlockchainStorage storage = new BlockchainStorage(BlockchainStorageTest.class);
        Blockchain blockchain = Blockchain.createDefault();

        storage.save(blockchain);
        Blockchain loaded = storage.load();

        assertEquals(blockchain.size(), loaded.size());
        assertEquals(blockchain.getBlock(0).getCurrentHash(), loaded.getBlock(0).getCurrentHash());
        assertEquals(blockchain.getBlock(1).getCurrentHash(), loaded.getBlock(1).getCurrentHash());
        assertEquals(blockchain.getBlock(1).getTransactions(), loaded.getBlock(1).getTransactions());
        assertTrue(loaded.validate().isValid());
    }

    @Test
    void load_missingFile_returnsDefaultBlockchain() throws IOException {
        Files.deleteIfExists(blockchainFile);
        BlockchainStorage storage = new BlockchainStorage(BlockchainStorageTest.class);

        Blockchain loaded = storage.load();

        assertEquals(2, loaded.size());
        assertTrue(loaded.validate().isValid());
    }

    @Test
    void load_blankFile_returnsDefaultBlockchain() throws IOException {
        Files.createDirectories(dataDir);
        Files.writeString(blockchainFile, "   \n\t", StandardCharsets.UTF_8);
        BlockchainStorage storage = new BlockchainStorage(BlockchainStorageTest.class);

        Blockchain loaded = storage.load();

        assertEquals(2, loaded.size());
        assertTrue(loaded.validate().isValid());
    }

    @Test
    void load_invalidBlockchain_throwsIOException() throws IOException {
        Files.createDirectories(dataDir);
        String invalidBlockchainJson = """
                {
                  "blocks": [
                    {
                      "index": 0,
                      "timestamp": "2026-02-12 14:30:21",
                      "previousHash": "0000000000000000",
                      "currentHash": "invalidhash",
                      "transactions": ["Genesis Block"]
                    }
                  ]
                }
                """;
        Files.writeString(blockchainFile, invalidBlockchainJson, StandardCharsets.UTF_8);
        BlockchainStorage storage = new BlockchainStorage(BlockchainStorageTest.class);

        IOException exception = assertThrows(IOException.class, storage::load);
        assertTrue(exception.getMessage().startsWith("Loaded blockchain is invalid:"));
    }
}
