package ys.prototype.fmtaq2.domain.command;

interface CommandState<T extends Command> {

    void send(T command, CommandService commandService);

    void handleOkResponse(T command, CommandService commandService);

    void handleErrorResponse(T command, CommandService commandService);
}
