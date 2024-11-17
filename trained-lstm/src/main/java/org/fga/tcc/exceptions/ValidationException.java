package org.fga.tcc.exceptions;

import java.io.Serial;

public class ValidationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3920456271066331951L;

    public ValidationException(String error) {
        super(error);
    }

}
