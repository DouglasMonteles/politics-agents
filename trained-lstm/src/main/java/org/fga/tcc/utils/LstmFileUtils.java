package org.fga.tcc.utils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class LstmFileUtils {

    public static void iterateDirectory(String parentDir, PerformIterateDirectoryAction action) {
        try {
            List<Path> filePairs = new ArrayList<>();

            Files.walkFileTree(Paths.get(parentDir), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    Path sibling = file.getParent();

                    if (Files.exists(sibling)) {
                        filePairs.add(sibling);
                    }

                    return FileVisitResult.SKIP_SIBLINGS;
                }
            });

            for (Path path : filePairs) {
                action.processData(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
