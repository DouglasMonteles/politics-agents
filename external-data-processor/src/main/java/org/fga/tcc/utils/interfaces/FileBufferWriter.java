package org.fga.tcc.utils.interfaces;

import java.io.BufferedWriter;
import java.io.IOException;

public interface FileBufferWriter {
    
    void processData(BufferedWriter writer) throws IOException;
    
}
