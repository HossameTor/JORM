package org.jorm.helpers;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileReader {
    public String readFile(String filePath) {
        InputStream inputStream = getClass().getResourceAsStream(filePath);

        if (inputStream == null) {
            throw new RuntimeException("Resource not found: " + filePath);
        }
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            String content = scanner.useDelimiter("\\A").next();
            // Process the content as needed
            return content;
        }

    }
}
