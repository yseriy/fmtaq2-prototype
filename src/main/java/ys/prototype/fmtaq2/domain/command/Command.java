package ys.prototype.fmtaq2.domain.command;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Inheritance
public abstract class Command {

    @Id
    private UUID id;
    private LocalDateTime startTimestamp;
    private LocalDateTime statusTimestamp;
    private String address;
    private String body;
    private CommandStatus commandStatus;

    @Version
    private Long version;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Task task;

    protected Command() {
    }

    Command(final UUID id, final String address, final String body, final Task task) {
        LocalDateTime createTime = LocalDateTime.now();
        this.id = id;
        this.startTimestamp = createTime;
        this.statusTimestamp = createTime;
        this.address = address;
        this.body = body;
        this.commandStatus = CommandStatus.REGISTERED;
        this.task = task;
    }

    public abstract void send(CommandService commandService);

    public void handleResponse(final CommandResponse commandResponse, final CommandService commandService) {
        statusTimestamp = LocalDateTime.now();

        switch (commandResponse.getStatus()) {
            case OK:
                commandStatus = CommandStatus.OK;
                handleOkResponse(commandService);
                break;
            case ERROR:
                commandStatus = CommandStatus.ERROR;
                handleErrorResponse(commandService);
                break;
            default:
                throw new RuntimeException("unknown commandResponse status: " + commandResponse.getStatus());
        }
    }

    abstract void handleOkResponse(CommandService commandService);

    abstract void handleErrorResponse(CommandService commandService);

    UUID getId() {
        return id;
    }

    Task getTask() {
        return task;
    }

    void setRunTaskStatus() {
        task.setRunStatus();
    }

    void setOkTaskStatus() {
        task.setOkStatus();
    }

    void setErrorTaskStatus() {
        task.setErrorStatus();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "-" + id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Command command = (Command) o;

        return id.equals(command.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
