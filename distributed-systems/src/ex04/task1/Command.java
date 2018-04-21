package ex04.task1;

import java.io.Serializable;
import java.util.UUID;

public class Command implements Serializable {
    private static final long serialVersionUID = 1L;

    private CommandType commandType;
    private String sender;
    private UUID cmdId;
    private String message;

    public Command(final CommandType commandType, final String sender, final String message) {
        this.commandType = commandType;
        this.sender = sender;
        this.message = message;
        cmdId = UUID.randomUUID();
    }

    public Command(final CommandType commandType, final String sender) {
        this.commandType = commandType;
        this.sender = sender;
        cmdId = UUID.randomUUID();
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getSender() {
        return sender;
    }

    public UUID getCmdId() {
        return cmdId;
    }

    public String getMessage() {
        return message;
    }
}
