package bf.xmly;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FileUtil {

  static String readToString(String fileName) throws Exception {
    return new Scanner(new File(fileName),StandardCharsets.UTF_8).useDelimiter("\\Z").next();
  }

  static void writeToFile(String fileName, String content) throws Exception {
    BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8));
    writer.write(content);
    writer.close();
  }
}
