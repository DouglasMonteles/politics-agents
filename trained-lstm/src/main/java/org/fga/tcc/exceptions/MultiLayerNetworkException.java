package org.fga.tcc.exceptions;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.Serial;

public class MultiLayerNetworkException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3920456271066331951L;

    public MultiLayerNetworkException(String error) {
        super(error);
    }

}
