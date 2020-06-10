package bf.pd;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class SQLiteJDBC {


    private static Connection connection;
    private static Set<String> allArtist;
    private static Set<String> notTrack = new HashSet<>();
    public static final String[] SPLITTER = new String[]{"_", "-", "》", "《", " ", "&", "相声", "群口",
            "、", "作品", "－", "\\(", "\\)", "—", "（", "^\\d*", "\\{", "\\}", "单口", ":", "：", "完整版"};

    public static void main(String args[]) throws Exception {
        allArtist = allArtist();
        notTrack.addAll(allArtist);
        List<Path> files = Files.walk(Paths.get("c:\\media\\podcast\\"))
                .filter(
                        (p) -> Files.isRegularFile(p)
                                && !p.toString().endsWith(".bat")
                                && !p.toString().endsWith(".txt")
                )
                .collect(Collectors.toCollection(ArrayList::new));
        for (Path path : files) {
            for (String artist : allArtist) {
                if (path.toString().contains(artist)) {
                    String track = extractTrack(path.toFile().getName());
                    if (exist(artist, track)) {
                        continue;
                    }
                    System.out.println(path);
                    insertPodcast(artist, track);
                }
            }
        }
    }

    private static String extractTrack(String path) {
        if (path.endsWith(".m4a"))
            path = path.substring(0, path.lastIndexOf(".m4a"));

        String track = null;
        List<String> words = splitWords(path);

        notTrack:
        for (String word : words) {
            if (word.length() == 0) continue;
            if (allArtist.contains(word)) continue;
            if (!containsHanScript(word)) continue;

            for (String n : notTrack) {
                if (word.contains(n) || n.contains(word)) continue notTrack;
            }
            track = word;
            break;
        }

        validateTrack(track);
        return track;
    }

    private static List<String> splitWords(String fileName) {
        List<String> merged = new ArrayList<>();

        for (String artist : allArtist) {
            merged.add(artist);
        }

        for (String s : SPLITTER) {
            merged.add(s);
        }

        List<String> list = new ArrayList<>();
        list.add(fileName);
        List<String> result = new ArrayList<>();
        for (String c : merged) {
            for (String line : list) {
                if (line.length() == 0) continue;
                String[] split = line.split(c);
                result.addAll(Arrays.asList(split));
            }
            list.clear();
            list.addAll(result);
            result.clear();
        }
        return list;
    }

    private static void insertPodcast(String artist, String track) throws Exception {
        if (!allArtist.contains(artist)) throw new RuntimeException(artist);
        if (track == null || track.length() == 1) return;
        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("INSERT INTO podcast (artist, track) VALUES(?, ?)");
        stmt.setString(1, artist);
        stmt.setString(2, track);
        stmt.execute();
        stmt.close();
    }


    static void insertArtist(String artist) throws Exception {
        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("INSERT INTO artist (artist) VALUES(?)");
        stmt.setString(1, artist);
        stmt.execute();
        stmt.close();
    }

    private static void validateTrack(String track) {
        if (track == null) return;
        if (!containsHanScript(track)) throw new RuntimeException(track);
        if (track.contains("\\") || track.contains("/")) throw new RuntimeException(track);
        if (track.contains("相声")) throw new RuntimeException(track);
        if (allArtist.contains(track)) throw new RuntimeException(track);
        for (String s : allArtist) {
            if (track.contains(s)) throw new RuntimeException(track);
        }
    }

    private static Connection getConnection() throws Exception {
        Class.forName("org.sqlite.JDBC");
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:sqlite:podcast.db");
        }
        return connection;
    }


    static Set<String> allArtist() throws Exception {
        Set<String> artists = new HashSet<>();
        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("SELECT artist FROM artist");
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet.next()) {
            String artist = resultSet.getString(1);
            artists.add(artist);
        }
        stmt.close();
        return artists;
    }

    static HashMap<String, Set<String>> allTracks() throws Exception {
        HashMap<String, Set<String>> ret = new HashMap<>();
        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("SELECT artist, track FROM podcast");
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet.next()) {
            String artist = resultSet.getString(1);
            String track = resultSet.getString(2);
            Set<String> artists = ret.getOrDefault(track, new HashSet<>());
            artists.add(artist);
            ret.put(track, artists);
        }
        stmt.close();
        return ret;
    }


    private static boolean exist(String artist, String track) throws Exception {
        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("SELECT count(*) FROM podcast where artist=? and track=?");
        stmt.setString(1, artist);
        stmt.setString(2, track);
        ResultSet resultSet = stmt.executeQuery();
        int count = resultSet.getInt(1);
        stmt.close();
        return count == 1;
    }

    public static boolean containsHanScript(String s) {
        for (int i = 0; i < s.length(); ) {
            int codepoint = s.codePointAt(i);
            i += Character.charCount(codepoint);
            if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN) {
                return true;
            }
        }
        return false;
    }
}
