package bf.pd;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

@Deprecated
public class XiangShengArtist {
    public static void main(String[] args) throws Exception {
        Document document = JsoupUtil.urlToDoc("https://zh.wikipedia.org/zh-hans/%E4%B8%AD%E5%9B%BD%E5%A7%93%E6%B0%8F%E6%8E%92%E5%90%8D");
        Elements links = document.select("table:nth-child(68) a,table:nth-child(69) a, table:nth-child(59) a");
        Set<String> lastNames = new HashSet<>();
        for (Element link : links) {
            String name = link.html();
            if (name.length() > 1) continue;
            lastNames.add(name);
        }

        document = JsoupUtil.urlToDoc("https://zh.wikipedia.org/zh-hans/%E7%9B%B8%E5%A3%B0%E6%BC%94%E5%91%98%E5%B8%88%E6%89%BF%E5%85%B3%E7%B3%BB");
        links = document.select(".mw-parser-output a");

        Set<String> artists = new HashSet<>();
        for (Element link : links) {
            String name = link.html();

            if (name.length() > 3) continue;
            for (String lastName : lastNames) {
                if (name.startsWith(lastName)) {
                    if (name.equals("相声")) continue;
                    if (name.equals("白玉")) continue;
                    artists.add(name);
                    break;
                }
            }

        }

        Set<String> existingArtists = XiangShengLib.allArtist();
        artists.removeAll(existingArtists);

        for (String artist : artists) {
            XiangShengLib.insertArtist(artist);
        }
    }
}
