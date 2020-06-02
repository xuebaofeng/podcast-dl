/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package bf.pd;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DownXmly {

    static List<String> blackList;
    private static HashMap<String, Set<String>> allTracks = new HashMap<>();
    private static Set<String> allArtist;

    public static void main(String[] args) throws Exception {
        blackList = Files.readAllLines(Path.of("blacklist.txt"));


        if (args.length != 1) {
            System.out.println("input album url");
            return;
        }
        String albumUrl = args[0];

        if (albumUrl.contains("xiangsheng")) {
            if (allTracks.isEmpty())
                allTracks = SQLiteJDBC.allTracks();

            if (allArtist == null || allArtist.isEmpty())
                allArtist = SQLiteJDBC.allArtist();
        }

        boolean hasNext = true;
        int pageNum = 1;
        while (hasNext) {
            hasNext = onePage(albumUrl + "p" + pageNum + "/", pageNum);
            pageNum++;
        }
    }

    private static boolean onePage(String albumUrl, int pageNum) throws Exception {
        System.out.println(albumUrl);
        String[] split = albumUrl.split("https://www.ximalaya.com/");
        String s = split[1].split("/")[1];
        int albumNum = Integer.parseInt(s);

        Document page = JsoupUtil.urlToDoc(albumUrl);

        Elements tracks = page.select("#anchor_sound_list > div.sound-list._Qp > ul > li> div.text._Vc > a");
        String albumName = page.select("head > title").html();
        System.out.println(albumName);
        if (tracks.size() == 0) {
            if (pageNum == 1)
                throw new Exception("empty list:" + albumUrl);
            else
                return false;
        }

        tracks:
        for (Element track : tracks) {
            String href = track.attr("href");
            String[] tried = href.split("/" + albumNum + "/");
            if (tried.length == 1)
                continue;
            String trackNum = tried[1];
            String trackUrl = "http://www.ximalaya.com/tracks/" + trackNum + ".json";
            String json = HttpUtil.url2Body(trackUrl);
            ObjectMapper mapper = new ObjectMapper();
            Map map = mapper.readValue(json, Map.class);
            String audioUrl = (String) map.get("play_path_64");
            String title = (String) map.get("title");
            if (title.contains("直播回听")) continue;
            if (title.contains("问题征集活动")) continue;
            title = title.replaceAll("\"", "");
            title = title.replaceAll("\\?", "");
            title = title.replaceAll("\\|", "");
            title = title.replaceAll("/", "");


            for (String item : blackList) {
                if (title.contains(item)) {
                    System.out.println(title + " is blacklisted, skip");
                    continue tracks;
                }
            }


            for (String aTrack : allTracks.keySet()) {
                if (!foundArtist(title, albumName)) {
                    System.out.println(title + " has no artist, skip");
                    continue tracks;
                }
                if (title.contains(aTrack)) {
                    Set<String> artists = allTracks.get(aTrack);
                    for (String artist : artists) {
                        if (title.contains(artist) || albumName.contains(artist)) {
                            System.out.println(title + " is downloaded, skip");
                            continue tracks;
                        }
                    }
                }
            }


            String albumTitle = (String) map.get("album_title");
            albumTitle = albumTitle.replaceAll("\\|", "");
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
                    System.out.println(title);
                    FileUtils.copyURLToFile(new URL(audioUrl), toDownload);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return true;
    }

    private static boolean foundArtist(String title, String albumName) {
        for (String artist : allArtist) {
            if (title.contains(artist) || albumName.contains(artist)) return true;
        }
        return false;
    }
}
