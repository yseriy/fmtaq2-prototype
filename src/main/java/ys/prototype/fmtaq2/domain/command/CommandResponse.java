package ys.prototype.fmtaq2.domain.command;

import lombok.Data;

import java.util.UUID;

@Data
public class CommandResponse {

    private final UUID commandId;
    private final CommandResponseStatus status;
    private final String body;

    public CommandResponse(UUID commandId, CommandResponseStatus status, String body) {
        this.commandId = commandId;
        this.status = status;
        this.body = body;
    }
}
