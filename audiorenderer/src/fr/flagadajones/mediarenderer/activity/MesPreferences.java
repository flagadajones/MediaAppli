package fr.flagadajones.mediarenderer.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import fr.flagadajones.mediarenderer.R;

public class MesPreferences extends PreferenceActivity {
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.preferences);

}
}
