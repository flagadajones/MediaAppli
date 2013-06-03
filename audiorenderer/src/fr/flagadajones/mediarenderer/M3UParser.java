package fr.flagadajones.mediarenderer;



import android.net.http.AndroidHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import java.io.*;
import java.util.logging.Logger;

public class M3UParser {
    private static final Logger log = Logger.getLogger(M3UParser.class.getName());
    public M3UParser() throws Exception {

    }

    public String convertStreamToString(java.io.InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }

    public M3UHolder parseFile(File f) throws FileNotFoundException {
        if (f.exists()) {


            String stream = convertStreamToString(new FileInputStream(f));


            stream = stream.replaceAll("#EXTM3U", "").trim();
            String[] arr = stream.split("#EXTINF.*,");
            String urls = "", data = "";
            // clean
            {
                for (int n = 0; n < arr.length; n++) {
                    if (arr[n].contains("http")) {
                        String nu = arr[n].substring(arr[n].indexOf("http://"),
                                arr[n].indexOf(".mp3") + 4);

                        urls = urls.concat(nu);
                        data = data.concat(arr[n].replaceAll(nu, "").trim())
                                .concat("&&&&");
                        urls = urls.concat("####");
                    }
                }
            }
            return new M3UHolder(data.split("&&&&"), urls.split("####"));
        }
        return null;
    }
    public M3UHolder parseURL(String url) throws FileNotFoundException {
            AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android");
            HttpResponse response;
            StringBuilder responseString = new StringBuilder();
            try {
                response = httpClient.execute(new HttpGet(url));

                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseString.append(line);
                    }
                    reader.close();
                }else if(statusLine.getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY){

                    return parseURL(response.getLastHeader("Location").getValue());
                }
                } catch (ClientProtocolException e) {
                    log.warning(e.toString());
                } catch (IOException e) {
                    log.warning(e.toString());
                }
                finally {
                    httpClient.close();
                }
                    String stream = responseString.toString();
            stream = stream.replaceAll("#EXTM3U", "").trim();
            String[] arr = stream.split("#EXTINF.*,");
            String urls = "", data = "";
            // clean
            {
                for (int n = 0; n < arr.length; n++) {
                    if (arr[n].contains("http")) {
                        String nu = arr[n].substring(arr[n].indexOf("http://"),
                                arr[n].indexOf(".mp3") + 4);

                        urls = urls.concat(nu);
                        data = data.concat(arr[n].replaceAll(nu, "").trim())
                                .concat("&&&&");
                        urls = urls.concat("####");
                    }
                }
            }
            return new M3UHolder(data.split("&&&&"), urls.split("####"));

    }

    public class M3UHolder {
        private String[] data, url;

        public M3UHolder(String[] names, String[] urls) {
            this.data = names;
            this.url = urls;
        }

        public int getSize() {
            if (url != null)
                return url.length;
            return 0;
        }

        public String getName(int n) {
            return data[n];
        }

        public String getUrl(int n) {
            return url[n];
        }
    }
}