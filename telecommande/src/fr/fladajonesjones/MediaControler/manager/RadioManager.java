package fr.fladajonesjones.MediaControler.manager;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.fladajonesjones.MediaControler.database.RadioDAO;
import fr.fladajonesjones.MediaControler.database.VersionDAO;
import fr.fladajonesjones.media.model.Radio;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RadioManager {
    Context context;
    private static final Logger log = Logger.getLogger(RadioManager.class.getName());
    RadioManager instance;
    VersionDAO versionDao = new VersionDAO();
    RadioDAO radioDao = RadioDAO.getInstance();

    private List<Radio> radios = null;
    String version;

    public RadioManager(Context context) {
        this.context = context;
    }

    public List<Radio> getFav(int number) {

        return radioDao.getAllRadioFav(number);
    }

    public List<Radio> parse() {
        if (!isUpdated()) {
            radios = parseJson();
            radioDao.insertRadios(radios);
            versionDao.updateVersionRadio(version);

        } else {
            radios = radioDao.getAllRadio();


        }

        return radios;
    }

    public boolean isUpdated() {
        boolean result = true;
        AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android");
        HttpResponse response;
        StringBuilder responseString = new StringBuilder();
        try {
            response = httpClient.execute(new HttpGet("http://gruault.free.fr/RadioVersion.json"));

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

                String versionBase = versionDao.getRadioVersion();
                this.version = responseString.toString();
                if (versionBase.equals(responseString.toString())) {
                    return true;
                } else {
                    return false;
                }

            } else {
                // Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            log.log(Level.SEVERE, "Error", e);

        } catch (IOException e) {
            log.log(Level.SEVERE, "Error", e);
        } finally {
            httpClient.close();
        }

        return result;
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

                details = Arrays.asList((Radio[]) gson.fromJson(responseString.toString(), Radio[].class));

            } else {
                // Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            log.warning(e.toString());
        } catch (IOException e) {
            log.warning(e.toString());
        } finally {
            httpClient.close();
        }

        return details;
    }
}
