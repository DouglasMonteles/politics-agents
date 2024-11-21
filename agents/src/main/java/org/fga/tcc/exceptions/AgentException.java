package org.fga.tcc.exceptions;

import java.io.Serial;

public class AgentException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4900466671825492432L;

    public AgentException(String error) {
        super(error);
    }

}
