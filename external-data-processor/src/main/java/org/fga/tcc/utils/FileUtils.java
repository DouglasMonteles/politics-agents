package org.fga.tcc.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fga.tcc.entities.OpenDataBaseResponseList;
import org.fga.tcc.entities.OpenDataBaseSingleResponse;
import org.fga.tcc.entities.Voting;
import org.fga.tcc.utils.interfaces.FileBufferRead;
import org.fga.tcc.utils.interfaces.FileBufferWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static void saveFileSingleResult(String filePath, String fileContent) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = createFile(filePath);
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, mapper.readValue(fileContent, OpenDataBaseSingleResponse.class));
        } catch (IOException e) {
            System.out.println("Error saving json file");
        }
    }

    public static void saveFileResultList(String filePath, String fileContent) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = createFile(filePath);
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, mapper.readValue(fileContent, OpenDataBaseResponseList.class));
        } catch (IOException e) {
            System.out.println("Error saving json file");
        }
    }

    public static void saveTxtFile(String filePath, String fileContent) {
        ObjectMapper mapper = new ObjectMapper();

        if (fileContent != null && !fileContent.isEmpty()) {
            String normalizedRow = fileContent
                    .replaceAll("\"", "")
                    .replaceAll("\\n", "")
                    .replaceAll("\\t", "")
                    .replaceAll("\\r", "")
                    .trim();

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
                bufferedWriter.write(normalizedRow);
                LOGGER.info("Data saved in txt: " + filePath);
            } catch(IOException e) {
                LOGGER.error("Error saving txt file");
            }
        } else {
            LOGGER.warn("There is no data to be saved in txt file.");
        }
    }

    public static File createFile(String filePath) {
        File file = new File(filePath);

        // Create subdirectories if needed
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        return file;
    }

    public static boolean isFileAlreadyCreated(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static void writeFile(String path, FileBufferWriter fileBufferWriter) {
        if (!FileUtils.isFileAlreadyCreated(path)) {
            FileUtils.createFile(path);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            fileBufferWriter.processData(writer);
        } catch (IOException e) {
            LOGGER.error("Error: " + e.getMessage());
        } finally {
            LOGGER.info("Arquivo gravado com sucesso!");
        }
    }

    public static void readFile(String directoryPath, FileBufferRead fileBufferRead) {
        try {
            Path dirPath = Paths.get(directoryPath);
            DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.json");
            ObjectMapper objectMapper = new ObjectMapper();

            for (Path path : stream) {
                fileBufferRead.processData(path, objectMapper);
            }
        } catch (IOException | DirectoryIteratorException e) {
            e.printStackTrace();
        }
    }

}
