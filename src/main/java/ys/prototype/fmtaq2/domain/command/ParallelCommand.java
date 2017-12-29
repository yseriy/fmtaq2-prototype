package ys.prototype.fmtaq2.domain.command;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
class ParallelCommand extends Command {

    protected ParallelCommand() {
    }

    public ParallelCommand(UUID id, String address, String body, ParallelTask parallelTask) {
        super(id, address, body, parallelTask);
    }

    @Override
    public void send(CommandService commandService) {
        CommandState<ParallelCommand> commandState = getTaskState();
        commandState.send(this, commandService);
    }

    @Override
    void handleOkResponse(CommandService commandService) {
        CommandState<ParallelCommand> commandState = getTaskState();
        commandState.handleOkResponse(this, commandService);
    }

    @Override
    void handleErrorResponse(CommandService commandService) {
        CommandState<ParallelCommand> commandState = getTaskState();
        commandState.handleErrorResponse(this, commandService);
    }

    private CommandState<ParallelCommand> getTaskState() {
        ParallelTask parallelTask = (ParallelTask) getTask();
        return parallelTask.getState();
    }

    boolean isLast() {
        ParallelTask parallelTask = (ParallelTask) getTask();
        return parallelTask.isLastCommand();
    }

    void reduceCommandCounter() {
        ParallelTask parallelTask = (ParallelTask) getTask();
        parallelTask.reduceCommandCounter();
    }
}

@Entity
class ParallelTask extends Task {

    private Integer commandCounter;

    protected ParallelTask() {
    }

    public ParallelTask(UUID id, String account, String serviceType) {
        super(id, account, serviceType);
        this.commandCounter = commandCounter;
    }

    void setCommandCounter(Integer commandCounter) {
        this.commandCounter = commandCounter;
    }

    boolean isLastCommand() {
        return commandCounter == 0;
    }

    void reduceCommandCounter() {
        commandCounter--;
    }

    CommandState<ParallelCommand> getState() {
        switch (getStatus()) {
            case NEW:
                return new ParallelCommandNewState();
            case RUN:
                return new ParallelCommandRunState();
            default:
                throw new RuntimeException("unknown task status: " + getStatus());
        }
    }
}

class ParallelCommandNewState implements CommandState<ParallelCommand> {

    @Override
    public void send(ParallelCommand command, CommandService commandService) {
        commandService.send(command);
        command.setRunTaskStatus();
    }

    @Override
    public void handleOkResponse(ParallelCommand parallelCommand, CommandService commandService) {

    }

    @Override
    public void handleErrorResponse(ParallelCommand parallelCommand, CommandService commandService) {

    }
}

class ParallelCommandRunState implements CommandState<ParallelCommand> {

    @Override
    public void send(ParallelCommand command, CommandService commandService) {

    }

    @Override
    public void handleOkResponse(ParallelCommand command, CommandService commandService) {
        command.reduceCommandCounter();

        if (command.isLast()) {
            command.setOkTaskStatus();
        }
    }

    @Override
    public void handleErrorResponse(ParallelCommand command, CommandService commandService) {
        command.reduceCommandCounter();

        if (command.isLast()) {
            command.setErrorTaskStatus();
        }
    }
}
