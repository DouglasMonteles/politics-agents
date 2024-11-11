package org.fga.tcc.utils.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;

public interface FileBufferRead {
    
    void processData(Path path, ObjectMapper objectMapper) throws IOException;
    
}
