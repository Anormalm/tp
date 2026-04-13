package seedu.crypto1010.command;

import seedu.crypto1010.Parser;
import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.WalletManager;
import seedu.crypto1010.ui.CliVisuals;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TutorialCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: tutorial start
            Example: tutorial start
            
            Starts the tutorial which guides the user through using the simple commands
            """;

    private static final String ERROR_MESSAGE = "Please input the given command to continue\n" +
            "If you want to exit tutorial mode, type: tutorial exit";
    private static final String EXIT_MESSAGE = "Exiting tutorial...";
    private static final String WELCOME_MESSAGE = "Welcome! " +
            "This tutorial will guide you through the basics of a blockchain system. " +
            "We’ll learn about wallets, addresses, transactions, and blocks along the way.";
    private static final String INVALID_FORMAT_ERROR = "Error: Invalid tutorial format. Use tutorial start";
    private static final String MISSING_INPUT_ERROR = "Error: Tutorial requires interactive input.";
    private static final String INSTRUCTION_MESSAGE = "Enter the following command:";

    private static final String[] instructions = {
        "create w/alice",
        "create w/bob",
        "list",
        "keygen w/alice",
        "keygen w/bob",
        "list",
        "balance w/alice",
        "balance w/bob",
        "send w/bob amt/3 to/{alice's wallet address}",
        "balance w/alice",
        "balance w/bob",
        "help c/send",
        "history w/bob",
        "viewblock 2",
        "validate",
        "tutorial exit"
    };

    private static final String[] steps = {
        "Creating a wallet",
        "Creating a second wallet",
        "Listing wallets",
        "Generating keys for Alice",
        "Generating keys for Bob",
        "Listing wallets (with addresses",
        "Checking Alice's balance",
        "Checking Bob's balance",
        "Sending the transaction",
        "Checking Alice's balance after transaction",
        "Checking Bob's balance after transaction",
        "Learning more about send command",
        "Viewing transaction history",
        "Viewing a block",
        "Validating the blockchain",
        "Conclusion"
    };

    private static final String[] tutorialMessages = {
        """
            A wallet is your identity on the blockchain.
            It doesn't store coins directly — instead, it holds cryptographic keys that prove ownership.
            Think of it like a password manager for the blockchain.
            Let's create one for Alice.
            """,

        """
            Every participant on the blockchain needs their own wallet with a unique identity.
            Bob's wallet will be completely independent from Alice's — no central authority connects them.
            Let's create one for Bob too.
            """,

        """
            Now let's list both wallets.
            Pay attention to the Address column when the results appear.
            """,

        """
            Notice that neither wallet has an address or currency yet.
            For this tutorial, we will ignore currency.
            Addresses are derived from cryptographic keys, which we haven't generated.
            A wallet without keys is like a lock with no key: it exists, but can't do anything.
            Let's fix that for Alice first.
            
            We will now create a private key, derive a public key from it using elliptic curve math (secp256k1),
            then hash that into a unique address.
            """,

        """
            Alice now has a private key, a public key, and a unique address.
            The private key stays secret — it's what proves ownership.
            The address is what you share publicly to receive funds.
            Now let's generate the same for Bob.
            """,

        """
            Bob now has his own key pair too.
            Notice each address is completely unique, the pool of possible
            private keys is so astronomically large (2²⁵⁶) that generating the same one twice is virtually impossible.
            Let's list both wallets now to see how they look.
            """,

        """
            Both wallets now have a 42-character hexadecimal address starting with 0x.
            This is what you share publicly when you want to receive funds — like a bank account number,
            but without revealing anything about your private key.
            Let's check Alice's current balance.
            """,

        """
            This is the amount that Alice has before our transaction.
            In a blockchain balances aren't stored inside the wallet itself.
            The blockchain tallies all incoming and outgoing transactions to arrive at a balance.
            Let's check Bob's.
            """,


        """
            Now let's send some funds from Bob to Alice.
            When Bob sends money, his wallet uses his private key to cryptographically sign the transaction,
            proving he authorised it without ever revealing the key.
            Anyone on the network can verify this using his public key, but only Bob could have created that signature.
            """,

        "The transaction went through. Let's verify that Alice received the funds.",

        "Alice has 3 more dollars. Now let's check what Bob has left — see if the number matches what you'd expect.",

        """
            Bob lost more than $3. Why is this the case?
            The difference is a network fee — every transaction on a blockchain carries one,
            paid to the nodes that verify and record it.
            Without fees, there'd be no incentive to keep the network running.
            Let's look at the send command to understand how fees are determined.
            """,

        """
            You can see transactions support a speed setting — slow, standard, or fast.
            The faster you want your transaction confirmed, the higher the fee.
            Since we didn't specify one, it defaulted to standard.
            Let's pull up Bob's transaction history to see exactly what was charged.
            """,

        """
            There it is — $3 to Alice and $0.001 to the network, both permanently on record.
            This is the blockchain's public ledger in action: every transaction is fully
            auditable and nothing can be quietly altered or erased.
            Now let's look at the actual block that contains this transaction.
            """,

        """
            You can see Block 2 holds our transaction and links back to Block 1 via the previous hash.
            Each block's hash is computed from its contents plus the previous block's hash,
            so tampering with any block would break every block that follows it,
            making historical fraud practically impossible.
            Let's validate the whole chain to confirm everything is intact.
            """,

        """
            The chain is valid — every block checks out.
            You've now seen the full lifecycle of a blockchain transaction:
            wallets, key pairs, signing, sending, fees, blocks, and validation.
            This is the foundation that underlies Bitcoin, Ethereum, and thousands of other blockchains.
            """
    };

    private final String arguments;
    private boolean exitRequested;

    public TutorialCommand(String arguments) {
        super(HELP_DESCRIPTION);
        this.arguments = arguments;
        this.exitRequested = false;
    }

    public void execute (Blockchain blockchain, Scanner in) throws Crypto1010Exception {
        if (in == null) {
            throw new Crypto1010Exception(MISSING_INPUT_ERROR);
        }
        if (!arguments.equals("start")) {
            throw new Crypto1010Exception(INVALID_FORMAT_ERROR);
        }
        Blockchain tutorialBlockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        Parser parser = new Parser(walletManager);

        int index = 0;

        CliVisuals.printPanel("Tutorial", List.of(WELCOME_MESSAGE));
        while (index < instructions.length) {
            CliVisuals.printPanel("Tutorial Step " + (index + 1) + " - " + steps[index], buildStepLines(index));
            String input = in.nextLine().strip();
            if (input.equals("exit")) {
                exitRequested = true;
                new ExitCommand().execute(blockchain, in);
                return;
            }
            if (input.equals("tutorial exit")) {
                break;
            } else if (input.equals(instructions[index]) ||
                    (index == 8 && input.startsWith("send w/bob amt/3 to/"))) {
                Command c = parser.parse(input);
                try {
                    c.execute(tutorialBlockchain);
                    index++;
                } catch (Crypto1010Exception e) {
                    CliVisuals.printWarning(ERROR_MESSAGE);
                }
            } else {
                CliVisuals.printWarning(ERROR_MESSAGE);
            }
        }
        CliVisuals.printInfo(EXIT_MESSAGE);
    }

    public boolean isExitRequested() {
        return exitRequested;
    }

    private List<String> buildStepLines(int index) {
        List<String> lines = new ArrayList<>();
        lines.addAll(splitMultiline(tutorialMessages[index]));
        lines.add(INSTRUCTION_MESSAGE);
        lines.add(instructions[index]);
        return lines;
    }

    private List<String> splitMultiline(String text) {
        if (text == null || text.isBlank()) {
            return List.of("");
        }
        return List.of(text.split("\\R"));
    }
}
