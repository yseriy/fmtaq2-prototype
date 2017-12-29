package ys.prototype.fmtaq2.domain.command;

import org.springframework.stereotype.Component;

@Component
public interface CommandTransport {

    void send(Command command);
}
