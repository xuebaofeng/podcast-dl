/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package bf.pd;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class DownLjsw {

    public static void main(String[] args) throws Exception {

//        String baseUrl = "https://www.ljsw.io/knowl/column/2/";
//        String baseUrl = "https://www.ljsw.io/knowl/column/5/";
        String baseUrl = "https://www.ljsw.io/knowl/column/3/";
        boolean hasNext = true;
        int pageNum = 1;
        while (hasNext) {
            hasNext = onePage(baseUrl + pageNum + "/");
            pageNum++;
        }
    }

    private static boolean onePage(String pageUrl) throws Exception {

        Document page = JsoupUtil.urlToDoc(pageUrl);

        Elements tracks = page.select("div.article-row > div > a");
        boolean hasNext = false;
        for (Element track : tracks) {
            String href = track.attr("href");
            String trackUrl = "https://www.ljsw.io" + href;

            if (!trackUrl.contains("/article/")) continue;
            Document trackDoc = JsoupUtil.urlToDoc(trackUrl);
            Element audio = trackDoc.select(".audio > audio").get(0);
            String title = trackDoc.select(".post-title").get(0).html();
            String audioUrl = audio.attr("src");

            title = title.replaceAll("\\?", "？");
            title = title.replaceAll("\\|", "");
            title = title.replaceAll("/", "");
            title = title.replaceAll(":", "：");

            String albumTitle = "罗辑思维前4季";
            File folder = new File("C:\\media\\podcast\\" + albumTitle);
            Files.createDirectories(folder.toPath());

            File toDownload = new File(folder + "/" + title + ".m4a");
            if (toDownload.exists()) {
                if (toDownload.length() == 0) {
                    boolean delete = toDownload.delete();
                    System.out.println(toDownload + " size 0, deleted:" + delete);
                }
            }

            if (!toDownload.exists()) {
                try {
                    FileUtils.copyURLToFile(new URL(audioUrl), toDownload);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println(title);
            }
            hasNext = true;
        }
        return hasNext;
    }
}
