package ys.prototype.fmtaq2.domain.command;

import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class CommandService {

    private final CommandRepository commandRepository;
    private final CommandTransport commandTransport;

    public CommandService(CommandRepository commandRepository, CommandTransport commandTransport) {
        this.commandRepository = commandRepository;
        this.commandTransport = commandTransport;
    }

    public void send(Command command) {
        commandTransport.send(command);
    }

    public void send(UUID commandId) {
        Command command = commandRepository.findOne(commandId);

        if (command == null) {
            throw new NoSuchElementException("cannot find command by id: " + commandId);
        }

        send(command);
    }
}
