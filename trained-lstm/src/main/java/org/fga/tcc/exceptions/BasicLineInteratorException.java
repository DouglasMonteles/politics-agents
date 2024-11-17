package org.fga.tcc.exceptions;

import java.io.Serial;

public class BasicLineInteratorException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3920456271066331951L;

    public BasicLineInteratorException(String error) {
        super(error);
    }

}
