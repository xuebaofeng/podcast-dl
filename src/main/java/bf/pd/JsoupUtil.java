package bf.pd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class JsoupUtil {

    static Document urlToDoc(String url) throws Exception {
        String folder = "cache/";
        Files.createDirectories(Paths.get(folder));

        URL aUrl = new URL(url);

        String host = aUrl.getHost();
        folder = "cache/" + host + "/";

        Files.createDirectories(Paths.get(folder));

        String fileName = aUrl.getPath();

        fileName = hash(fileName);
        fileName = folder + fileName;
        fileName += ".html";

        Document doc;
        if (new File(fileName).exists()) {
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

    static String hash(String url) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(url.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digest.length; ++i) {
            sb.append(Integer.toHexString((digest[i] & 0xFF) | 0x100), 1, 3);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(urlToDoc("https://www.ljsw.io/knowl/article/AA.html"));
        System.out.println(hash("https://www.ljsw.io/knowl/article/AA.html"));
    }
}
