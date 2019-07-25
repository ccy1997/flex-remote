package fc.flexremote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

/**
 * This activity represents the screen for application settings
 *
 * @author ccy
 * @version 2019.0723
 * @since 1.0
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set the toolbar attributes
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

}
