package bf.pd;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpUtil {
    public static String url2Body(String trackUrl) {
        URL url = null;
        try {
            url = new URL(trackUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        String path = url.getPath();
        path = path.replaceFirst("/", "");
        path = path.replace("/", "_");
        path = "cache/" + path;
        boolean exists = new File(path).exists();
        String body;
        if (exists) {
            body = FileUtil.readToString(path);
        } else {
            HttpResponse<String> response = null;
            try {
                response = HttpClient.newHttpClient()
                        .send(HttpRequest.newBuilder(url.toURI()).build(),
                                HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
            body = response.body();
            FileUtil.writeToFile(path, body);
        }
        return body;
    }
}
