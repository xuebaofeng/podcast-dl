package bf.xmly;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsoupUtil {

  static Document urlToDoc(String url) throws Exception {
    String folder = "cache/";
    Files.createDirectories(Paths.get(folder));
    String fileName = folder + url.split("https://www.ximalaya.com/")[1].replace("/", "_");
    if (!fileName.contains(".")) {
      fileName += ".html";
    }

    Document doc;
    if (new File(fileName).exists()) {
      System.out.println(fileName+" exists");
      String content = FileUtil.readToString(fileName);
      if (content == null)
        return null;
      doc = Jsoup.parse(content);
    } else {
      doc = Jsoup.connect(url).get();
      FileUtil.writeToFile(fileName, doc.html());
    }
    return doc;
  }
}
