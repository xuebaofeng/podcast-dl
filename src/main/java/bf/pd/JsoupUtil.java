package bf.pd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class JsoupUtil {

    static Document urlToDoc(String url) {
        String folder = "cache/";
        try {
            Files.createDirectories(Paths.get(folder));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        URL aUrl = null;
        try {
            aUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        String host = aUrl.getHost();
        folder = "cache/" + host + "/";

        try {
            Files.createDirectories(Paths.get(folder));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            FileUtil.writeToFile(fileName, doc.html());
        }
        return doc;
    }

    static String hash(String url){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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
