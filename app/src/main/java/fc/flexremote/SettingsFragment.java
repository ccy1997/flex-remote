package fc.flexremote;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SeekBarPreference;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.preferences);
        SeekBarPreference sp = (SeekBarPreference) findPreference("set_touch_pad_sensitivity");
        sp.setMin(1);
        sp.setSelectable(false);
    }

}