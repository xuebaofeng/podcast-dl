package bf.pd;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FileUtil {

    static String readToString(String fileName){
        try {
            return new Scanner(new File(fileName), StandardCharsets.UTF_8).useDelimiter("\\Z").next();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void writeToFile(String fileName, String content) {
        try {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
