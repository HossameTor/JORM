package org.jorm.helpers;



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class InputReader {
    public void writeToFile(String filePath, String content){
        try {
            // Check if the file already exists, create if not
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Files.createFile(path);
                System.out.println("File created: " + filePath);
            }

            // Create a BufferedWriter with FileWriter
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false));

            // Write content to the file
            writer.write(content);

            // Close the writer to release resources
            writer.close();

            System.out.println("Content has been written to the file.");

        } catch (FileAlreadyExistsException e) {
            System.out.println("File already exists: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
