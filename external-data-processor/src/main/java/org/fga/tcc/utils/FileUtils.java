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
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    private static final Float TRAIN_LINES_PERCENT = 0.8f;
    private static final String NEGATIVE_FILE = "0.txt";
    private static final String POSITIVE_FILE = "1.txt";

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

    public static void flattenLinesTxtFile(String parentDir) {
        String negativeFile = "0.txt";
        String positiveFile = "1.txt";

        try {
            // Lista para armazenar pares de arquivos encontrados em cada subdiretório
            List<Path[]> filePairs = new ArrayList<>();
            List<String> pathsAlreadyAdded = new ArrayList<>();

            // Percorre todos os arquivos e diretórios dentro do diretório pai
            Files.walkFileTree(Paths.get(parentDir), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    // Verifica se o arquivo é "0.txt" ou "1.txt"
                    if (file.getFileName().toString().equals(negativeFile) || file.getFileName().toString().equals(positiveFile)) {
                        Path sibling = file.getParent().resolve(file.getFileName().toString().equals(negativeFile) ? positiveFile : negativeFile);
                        if (Files.exists(sibling) && (!pathsAlreadyAdded.contains(file.toString()) && !pathsAlreadyAdded.contains(sibling.toString()))) {
                            filePairs.add(new Path[]{file, sibling});
                            pathsAlreadyAdded.add(file.toString());
                            pathsAlreadyAdded.add(sibling.toString());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            // Processa cada par de arquivos
            for (Path[] pair : filePairs) {
                equalizeFiles(pair[0], pair[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void splitFilesInTrainAndTest(String parentDir) {
        try {
            // Lista para armazenar pares de arquivos encontrados em cada subdiretório
            List<Path[]> filePairs = new ArrayList<>();
            List<String> pathsAlreadyAdded = new ArrayList<>();

            // Percorre todos os arquivos e diretórios dentro do diretório pai
            Files.walkFileTree(Paths.get(parentDir), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    // Verifica se o arquivo é "0.txt" ou "1.txt"
                    if (file.getFileName().toString().equals(NEGATIVE_FILE) || file.getFileName().toString().equals(POSITIVE_FILE)) {
                        Path sibling = file.getParent().resolve(file.getFileName().toString().equals(NEGATIVE_FILE) ? POSITIVE_FILE : NEGATIVE_FILE);
                        if (Files.exists(sibling) && (!pathsAlreadyAdded.contains(file.toString()) && !pathsAlreadyAdded.contains(sibling.toString()))) {
                            filePairs.add(new Path[]{file, sibling});
                            pathsAlreadyAdded.add(file.toString());
                            pathsAlreadyAdded.add(sibling.toString());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            // Processa cada par de arquivos
            for (Path[] pair : filePairs) {
                Path file1Path = pair[0];
                Path file2Path = pair[1];

                List<String> file1Lines = new ArrayList<>(Files.readAllLines(file1Path));
                List<String> file2Lines = new ArrayList<>(Files.readAllLines(file2Path));

                int trainLinesFile1 = (int)(TRAIN_LINES_PERCENT * file1Lines.size());
                int trainLinesFile2 = (int)(TRAIN_LINES_PERCENT * file1Lines.size());

                // train
                List<String> truncatedTrainFile1Lines = file1Lines.stream().toList().subList(0, trainLinesFile1);
                List<String> truncatedTrainFile2Lines = file2Lines.stream().toList().subList(0, trainLinesFile2);

                generateDirectoryWithSampleData(
                        file1Path,
                        file2Path,
                        truncatedTrainFile1Lines,
                        truncatedTrainFile2Lines,
                        "train"
                );

                // test
                List<String> truncatedTestFile1Lines = file1Lines.stream().toList().subList(trainLinesFile1 + 1, file1Lines.size());
                List<String> truncatedTestFile2Lines = file2Lines.stream().toList().subList(trainLinesFile2 + 1, file2Lines.size());

                generateDirectoryWithSampleData(
                        file1Path,
                        file2Path,
                        truncatedTestFile1Lines,
                        truncatedTestFile2Lines,
                        "test"
                );

                System.out.println("Arquivos de treino e de teste criados");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateRawVotesToWordVector(String parentDir) {
        try {
            // Lista para armazenar pares de arquivos encontrados em cada subdiretório
            List<Path[]> filePairs = new ArrayList<>();
            List<String> pathsAlreadyAdded = new ArrayList<>();

            // Percorre todos os arquivos e diretórios dentro do diretório pai
            Files.walkFileTree(Paths.get(parentDir), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    // Verifica se o arquivo é "0.txt" ou "1.txt"
                    if (file.getFileName().toString().equals(NEGATIVE_FILE) || file.getFileName().toString().equals(POSITIVE_FILE)) {
                        Path sibling = file.getParent().resolve(file.getFileName().toString().equals(NEGATIVE_FILE) ? POSITIVE_FILE : NEGATIVE_FILE);
                        if (Files.exists(sibling) && (!pathsAlreadyAdded.contains(file.toString()) && !pathsAlreadyAdded.contains(sibling.toString()))) {
                            filePairs.add(new Path[]{file, sibling});
                            pathsAlreadyAdded.add(file.toString());
                            pathsAlreadyAdded.add(sibling.toString());
                        }
                    }
                    return FileVisitResult.SKIP_SIBLINGS;
                }
            });

            for (Path[] pair : filePairs) {
                Path file1Path = pair[0];
                Path file2Path = pair[1];

                List<String> allLines = new ArrayList<>();
                allLines.addAll(Files.readAllLines(file1Path));
                allLines.addAll(Files.readAllLines(file2Path));

                String rawProposalsPath = file1Path.toString().replaceAll(NEGATIVE_FILE, "") + "ProposalWordVector.txt";

                Files.write(Path.of(rawProposalsPath), allLines);

                System.out.println("Arquivo para o WordVector criado");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateCategoriesFile(String parentDir) {
        try {
            // Lista para armazenar pares de arquivos encontrados em cada subdiretório
            List<Path[]> filePairs = new ArrayList<>();
            List<String> pathsAlreadyAdded = new ArrayList<>();

            // Percorre todos os arquivos e diretórios dentro do diretório pai
            Files.walkFileTree(Paths.get(parentDir), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    // Verifica se o arquivo é "0.txt" ou "1.txt"
                    if (file.getFileName().toString().equals(NEGATIVE_FILE) || file.getFileName().toString().equals(POSITIVE_FILE)) {
                        Path sibling = file.getParent().resolve(file.getFileName().toString().equals(NEGATIVE_FILE) ? POSITIVE_FILE : NEGATIVE_FILE);
                        if (Files.exists(sibling) && (!pathsAlreadyAdded.contains(file.toString()) && !pathsAlreadyAdded.contains(sibling.toString()))) {
                            filePairs.add(new Path[]{file, sibling});
                            pathsAlreadyAdded.add(file.toString());
                            pathsAlreadyAdded.add(sibling.toString());
                        }
                    }
                    return FileVisitResult.SKIP_SIBLINGS;
                }
            });

            for (Path[] pair : filePairs) {
                Path file1Path = pair[0];
                String rawProposalsPath = file1Path.toString().replaceAll(NEGATIVE_FILE, "") + "categories.txt";
                List<String> categories = List.of(
                        "0, against",
                        "1, favor"
                );

                Files.write(Path.of(rawProposalsPath), categories);

                System.out.println("Arquivo de categories.txt criado");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateDirectoryWithSampleData(
            Path file1Path,
            Path file2Path,
            List<String> truncatedTrainFile1Lines,
            List<String> truncatedTrainFile2Lines,
            String newDirectory
    ) throws IOException {
        String file1TrainPath = file1Path.toString().replaceAll(NEGATIVE_FILE, newDirectory + File.separator + NEGATIVE_FILE);
        String file2TrainPath = file2Path.toString().replaceAll(POSITIVE_FILE, newDirectory + File.separator + POSITIVE_FILE);

        if (!FileUtils.isFileAlreadyCreated(file1TrainPath)) {
            FileUtils.createFile(file1TrainPath);
        }

        if (!FileUtils.isFileAlreadyCreated(file2TrainPath)) {
            FileUtils.createFile(file2TrainPath);
        }

        Files.write(Path.of(file1TrainPath), truncatedTrainFile1Lines);
        Files.write(Path.of(file2TrainPath), truncatedTrainFile2Lines);
    }

    // Método para igualar o número de linhas entre dois arquivos
    private static void equalizeFiles(Path file1Path, Path file2Path) {
        try {
            Set<String> file1Lines = new HashSet<>(Files.readAllLines(file1Path));
            Set<String> file2Lines = new HashSet<>(Files.readAllLines(file2Path));

            int minLines = Math.min(file1Lines.size(), file2Lines.size());

            List<String> truncatedFile1Lines = file1Lines.stream().toList().subList(0, minLines);
            List<String> truncatedFile2Lines = file2Lines.stream().toList().subList(0, minLines);

            Files.write(file1Path, truncatedFile1Lines);
            Files.write(file2Path, truncatedFile2Lines);

            System.out.println("Arquivos ajustados com o mesmo número de linhas: " + file1Path + " e " + file2Path);
        } catch (IOException e) {
            System.err.println("Erro ao processar os arquivos: " + file1Path + " e " + file2Path);
            e.printStackTrace();
        }
    }

}
