/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package bf.xmly;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

public class App {

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("input album url");
      return;
    }
    String albumUrl = args[0];
    System.out.println(albumUrl);
    if (albumUrl == null) {
      System.out.println("input album url");
      return;
    }
    boolean hasNext = true;
    int pageNum = 1;
    while (hasNext) {
      hasNext = onePage(albumUrl + "/p" + pageNum);
      pageNum++;
    }
  }

  private static boolean onePage(String albumUrl) throws Exception {
    String[] split = albumUrl.split("https://www.ximalaya.com/");
    String s = split[1].split("/")[1];
    System.out.println(s);
    int albumNum = Integer.parseInt(s);

    Document page = JsoupUtil.urlToDoc(albumUrl);

    Elements tracks = page.select("li._c2 > div> a");
    boolean hasNext = false;
    for (Element track : tracks) {
      String href = track.attr("href");
      System.out.println(href);
      String[] tried = href.split("/" + albumNum + "/");
      if (tried.length == 1)
        continue;
      String trackNum = tried[1];
      //http://www.ximalaya.com/tracks/238620663.json
      String trackUrl = "http://www.ximalaya.com/tracks/" + trackNum + ".json";
      String json = HttpUtil.url2Body(trackUrl);
      ObjectMapper mapper = new ObjectMapper();
      Map map = mapper.readValue(json, Map.class);
      String audioUrl = (String) map.get("play_path_64");
      String title = (String) map.get("title");

      String albumTitle = (String) map.get("album_title");
      File folder = new File("downloads/" + albumTitle);
      Files.createDirectories(folder.toPath());
      System.out.println(title);

      File toDownload = new File(folder + "/" + title + ".m4a");
      if (!toDownload.exists()) {
        File downloaded = new File(title + ".m4a");
        if (downloaded.exists()) {
          try {
            Files.move(downloaded.toPath(), toDownload.toPath());
          } catch (Exception e) {
            System.out.println(e.getMessage());
          }
        } else {
          try {
            FileUtils.copyURLToFile(new URL(audioUrl), toDownload);
          } catch (IOException e) {
            System.out.println(e.getMessage());
          }
        }
      }
      hasNext = true;
    }
    return hasNext;
  }

}