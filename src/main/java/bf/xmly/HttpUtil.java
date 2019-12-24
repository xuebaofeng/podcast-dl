package bf.xmly;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpUtil {
  public static String url2Body(String trackUrl) throws Exception {
    //http://www.ximalaya.com/tracks/238620663.json
    URL url = new URL(trackUrl);
    String path = url.getPath();
    path=path.replaceFirst("/","");
    path = path.replace("/", "_");
    path="cache/"+path;
    boolean exists = new File(path).exists();
    String body;
    if (exists) {
      body = FileUtil.readToString(path);
    } else {
      HttpResponse<String> response = HttpClient.newHttpClient()
          .send(HttpRequest.newBuilder(new URI(trackUrl)).build(),
              HttpResponse.BodyHandlers.ofString());
      body = response.body();
      FileUtil.writeToFile(path, body);
    }
    return body;
  }
}
