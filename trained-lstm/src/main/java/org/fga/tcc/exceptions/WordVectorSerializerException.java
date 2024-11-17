package org.fga.tcc.exceptions;

import java.io.Serial;

public class WordVectorSerializerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3920456271066331951L;

    public WordVectorSerializerException(String error) {
        super(error);
    }

}
