package org.fga.tcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PythonExecutor {

    public static void main(String[] args) {
        processPlainData("");
    }

    public static void processPlainData(String pdfUrl) {
        try {
            String pathProjectPython = System.getProperty("user.dir") + File.separator +
                    "conversor-pdf-to-plain-text" + File.separator;

            String command = "source env/bin/activate && "
                    + "pip install -r " + pathProjectPython + "requirements.txt && "
                    + "python3 " + pathProjectPython + "main.py && "
                    + "echo 'Activated python environment'";

            Process process = new ProcessBuilder()
                    .command("bash", "-c", command)
                    .redirectErrorStream(true)
                    .start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Processo finalizado com código: " + exitCode);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
