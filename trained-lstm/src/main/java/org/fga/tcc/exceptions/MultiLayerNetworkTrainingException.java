package org.fga.tcc.exceptions;

import java.io.Serial;

public class MultiLayerNetworkTrainingException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3920456271066331951L;

    public MultiLayerNetworkTrainingException(String error) {
        super(error);
    }

}
