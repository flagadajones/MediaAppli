package fr.fladajonesjones.MediaControler.manager;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import android.net.http.AndroidHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Xml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.media.model.Radio;

public class RadioManager {
    Context context;
    private static final Logger log = Logger.getLogger(RadioManager.class.getName());
    RadioManager instance;

    private List<Radio> radios = null;

    public RadioManager(Context context) {
        this.context = context;
    }

    static final String ITEM = "radio";
    static final String NAME = "name";
    static final String IMAGE = "image";
    static final String FLUX = "flux";

    public List<Radio> parse() {
        if (radios == null)
            // radios=parseXml();
            radios = parseJson();

        return radios;
    }

    public List<Radio> parseXml() {
        List<Radio> messages = null;
        XmlPullParser parser = Xml.newPullParser();
        try {
            // auto-detect the encoding from the stream
            parser.setInput(context.getResources().openRawResource(R.raw.radios), null);
            int eventType = parser.getEventType();
            Radio currentRadio = null;
            boolean done = false;
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                String name = null;
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    messages = new ArrayList<Radio>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase(ITEM)) {
                        currentRadio = new Radio();
                    } else if (currentRadio != null) {
                        if (name.equalsIgnoreCase(NAME)) {
                            currentRadio.titre = parser.nextText();
                        } else if (name.equalsIgnoreCase(IMAGE)) {
                            currentRadio.albumArt = parser.nextText();
                        } else if (name.equalsIgnoreCase(FLUX)) {
                            currentRadio.url = parser.nextText();

                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase(ITEM) && currentRadio != null) {
                        messages.add(currentRadio);
                    }
                    break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    public List<Radio> parseJson() {
        List<Radio> details = null;
        AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android");
        HttpResponse response;
        StringBuilder responseString = new StringBuilder();
        try {
            response = httpClient.execute(new HttpGet("http://gruault.free.fr/RadioList.json"));

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

                Gson gson = new GsonBuilder().create();

                details = Arrays.asList((Radio[])gson.fromJson(responseString.toString(), Radio[].class));





            } else {
                // Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
           log.warning(e.toString());
        } catch (IOException e) {
            log.warning(e.toString());
        }
        finally {
            httpClient.close();
        }


        return details;
    }
}
