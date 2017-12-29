package ys.prototype.fmtaq2.domain.command;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
class SequenceCommand extends Command {

    private UUID nextCommandId;

    protected SequenceCommand() {
    }

    public SequenceCommand(UUID id, UUID nextCommandId, String address, String body, SequenceTask sequenceTask) {
        super(id, address, body, sequenceTask);
        this.nextCommandId = nextCommandId;
    }

    @Override
    public void send(CommandService commandService) {
        CommandState<SequenceCommand> commandState = getTaskState();
        commandState.send(this, commandService);
    }

    @Override
    void handleOkResponse(CommandService commandService) {
        CommandState<SequenceCommand> commandState = getTaskState();
        commandState.handleOkResponse(this, commandService);
    }

    @Override
    void handleErrorResponse(CommandService commandService) {
        CommandState<SequenceCommand> commandState = getTaskState();
        commandState.handleErrorResponse(this, commandService);
    }

    private CommandState<SequenceCommand> getTaskState() {
        SequenceTask sequenceTask = (SequenceTask) getTask();
        return sequenceTask.getState();
    }

    boolean isFirst() {
        SequenceTask sequenceTask = (SequenceTask) getTask();
        return sequenceTask.isFirstCommandId(getId());
    }

    boolean isLast() {
        return nextCommandId == null;
    }

    UUID getNextCommandId() {
        return nextCommandId;
    }
}

@Entity
class SequenceTask extends Task {

    private UUID firstCommandId;

    protected SequenceTask() {
    }

    public SequenceTask(UUID id, String account, String serviceType, UUID firstCommandId) {
        super(id, account, serviceType);
        this.firstCommandId = firstCommandId;
    }

    boolean isFirstCommandId(UUID id) {
        return firstCommandId.equals(id);
    }

    CommandState<SequenceCommand> getState() {
        switch (getStatus()) {
            case NEW:
                return new SequenceCommandNewState();
            case RUN:
                return new SequenceCommandRunState();
            case OK:
                return new SequenceCommandEndState();
            case ERROR:
                return new SequenceCommandEndState();
            default:
                throw new RuntimeException("unknown task status: " + getStatus());
        }
    }
}

class SequenceCommandNewState implements CommandState<SequenceCommand> {

    @Override
    public void send(SequenceCommand sequenceCommand, CommandService commandService) {
        if (sequenceCommand.isFirst()) {
            commandService.send(sequenceCommand);
            sequenceCommand.setRunTaskStatus();
        }
    }

    @Override
    public void handleOkResponse(SequenceCommand sequenceCommand, CommandService commandService) {
        String message = String.format("try to handle 'OK' response in 'NEW' state. command: '%s'", sequenceCommand);
        throw new IllegalStateException(message);
    }

    @Override
    public void handleErrorResponse(SequenceCommand sequenceCommand, CommandService commandService) {
        String message = String.format("try to handle 'ERROR' response in 'NEW' state. command: '%s'", sequenceCommand);
        throw new IllegalStateException(message);
    }
}

class SequenceCommandRunState implements CommandState<SequenceCommand> {

    @Override
    public void send(SequenceCommand sequenceCommand, CommandService commandService) {
        String message = String.format("try to start command in 'RUN' state. command: '%s'", sequenceCommand);
        throw new IllegalStateException(message);
    }

    @Override
    public void handleOkResponse(SequenceCommand sequenceCommand, CommandService commandService) {
        if (sequenceCommand.isLast()) {
            sequenceCommand.setOkTaskStatus();
        } else {
            commandService.send(sequenceCommand.getNextCommandId());
        }
    }

    @Override
    public void handleErrorResponse(SequenceCommand sequenceCommand, CommandService commandService) {
        sequenceCommand.setErrorTaskStatus();
    }
}

class SequenceCommandEndState implements CommandState<SequenceCommand> {

    @Override
    public void send(SequenceCommand sequenceCommand, CommandService commandService) {
        String message = String.format("try to start command in 'OK' state. command: '%s'", sequenceCommand);
        throw new IllegalStateException(message);
    }

    @Override
    public void handleOkResponse(SequenceCommand sequenceCommand, CommandService commandService) {
        String message = String.format("try to handle 'OK' response in 'OK' state. command: '%s'", sequenceCommand);
        throw new IllegalStateException(message);
    }

    @Override
    public void handleErrorResponse(SequenceCommand sequenceCommand, CommandService commandService) {
        String message = String.format("try to handle 'ERROR' response in 'OK' state. command: '%s'", sequenceCommand);
        throw new IllegalStateException(message);
    }
}
