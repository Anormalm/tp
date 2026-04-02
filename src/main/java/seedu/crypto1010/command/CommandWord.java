package seedu.crypto1010.command;

public enum CommandWord {
    HELP("help", "lists all commands"),
    EXIT("exit", "exits the program"),
    TUTORIAL("tutorial", "gives instructions on how to use the commands"),

    // Wallet related commands
    CREATE("create", "creates a new wallet"),
    LIST("list", "lists all your wallets"),
    KEYGEN("keygen", "generates and displays key pair"),
    BALANCE("balance", "displays balance of wallet"),
    HISTORY("history", "shows the send history of a wallet"),
    SEND("send", "sends cryptocurrency from one wallet to another"),
    CROSSSEND("crossSend", "sends cryptocurrency to another account user"),

    // Blockchain related commands
    VALIDATE("validate", "validates block chain integrity"),
    VIEWCHAIN("viewchain", "displays blockchain overview"),
    VIEWBLOCK("viewblock", "displays details of the block");

    private final String command;
    private final String description;

    CommandWord(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCommand() {
        return command;
    }
}
