package fc.flexremote;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SeekBarPreference;

/**
 * This class is a fragment containing a set of preference items in the application's setting page
 *
 * @author ccy
 * @version 2019.0723
 * @since 1.0
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.preferences);

        // Set touch pad sensitivity seekbar's attributes
        SeekBarPreference sp = (SeekBarPreference) findPreference("set_touch_pad_sensitivity");
        sp.setMin(1);
        sp.setSelectable(false);
    }

}