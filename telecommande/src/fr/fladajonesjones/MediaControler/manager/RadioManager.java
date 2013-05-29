package fr.fladajonesjones.MediaControler.manager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = httpclient.execute(new HttpGet("http://192.168.0.32/radioList.json"));

            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                String responseString = out.toString();
                Gson gson = new GsonBuilder().create();

                details = Arrays.asList(gson.fromJson(responseString, Radio[].class));

            } else {
                // Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return details;
    }
}
