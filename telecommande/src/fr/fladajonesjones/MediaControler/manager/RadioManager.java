package fr.fladajonesjones.MediaControler.manager;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Xml;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.media.model.Radio;

public class RadioManager {
	Context context;

	RadioManager instance;

	public RadioManager(Context context) {
		this.context=context;
	}

	static final String ITEM = "radio";
	static final String NAME = "name";
	static final String IMAGE = "image";
	static final String FLUX = "flux";

	public List<Radio> parse() {
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
}
