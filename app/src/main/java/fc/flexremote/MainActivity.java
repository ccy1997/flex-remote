package fc.flexremote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * This activity is the entry point of the application
 *
 * @author ccy
 * @version 2019.0723
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity {
    private ArrayList<RemoteControlConfig> remoteControlConfigs;
    private RemoteControlListAdapter remoteControlListAdapter;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupRemoteControlListView();
        Preload.initializeKeyActionList();
    }

    /**
     * Setup the toolbar of the main page
     */
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton addRemote = findViewById(R.id.option_add_remote_control);
        ImageButton help = findViewById(R.id.option_help);
        ImageButton settings = findViewById(R.id.option_settings);

        addRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClickTime < Parameters.RECLICK_COOLDOWN_MILLI)
                    return;

                lastClickTime = SystemClock.elapsedRealtime();
                showOrientationDialog();
            }
        });

        help.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClickTime < Parameters.RECLICK_COOLDOWN_MILLI)
                    return;

                lastClickTime = SystemClock.elapsedRealtime();
                Intent startHelpActivity = new Intent(MainActivity.this, HelpActivity.class);
                MainActivity.this.startActivity(startHelpActivity);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClickTime < Parameters.RECLICK_COOLDOWN_MILLI)
                    return;

                lastClickTime = SystemClock.elapsedRealtime();
                Intent startSettingActivity = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(startSettingActivity);
            }
        });
    }

    /**
     * Setup the list view that shows all created remote controls
     */
    private void setupRemoteControlListView() {
        ListView listView = (ListView) findViewById(R.id.lv_remote_controls);
        listView.setEmptyView(findViewById(R.id.empty_list_view_text));
        remoteControlConfigs = new ArrayList<>();
        populateRemoteControlConfigs(remoteControlConfigs);

        remoteControlListAdapter = new RemoteControlListAdapter(this, remoteControlConfigs);
        listView.setAdapter(remoteControlListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SystemClock.elapsedRealtime() - lastClickTime < Parameters.RECLICK_COOLDOWN_MILLI)
                    return;

                lastClickTime = SystemClock.elapsedRealtime();

                Toast.makeText(MainActivity.this, "Connecting...", Toast.LENGTH_SHORT).show();
                RemoteControlConfig remoteControlConfig = remoteControlConfigs.get(position);
                connectServer(remoteControlConfig);
            }

        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String intentSource = intent.getStringExtra("intent_source");

        if (intentSource.equals("ConfigureRemoteControlActivity")) {
            String purpose = intent.getStringExtra("purpose");

            if (purpose.equals("create")) {
                RemoteControlConfig remoteControlConfig = (RemoteControlConfig) intent.getSerializableExtra("remote_control_config");
                remoteControlConfigs.add(remoteControlConfig);
                remoteControlListAdapter.notifyDataSetChanged();
            }

        }
    }

    /**
     * Create a dialog for choosing remote control's orientation
     * @return A dialog in linear layout
     */
    private LinearLayout createOrientationDialogLinearLayout() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Button portrait = new Button(this);
        Button landscape = new Button(this);
        portrait.setPadding(20, 0, 0, 0);
        landscape.setPadding(20, 0, 0, 0);
        portrait.setStateListAnimator(null);
        landscape.setStateListAnimator(null);
        portrait.setAllCaps(false);
        landscape.setAllCaps(false);
        portrait.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        landscape.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        portrait.setTextSize(24);
        landscape.setTextSize(24);
        portrait.setText("Portrait");
        landscape.setText("Landscape");
        portrait.setBackgroundResource(R.drawable.orientation_options_button);
        landscape.setBackgroundResource(R.drawable.orientation_options_button);
        portrait.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stay_primary_portrait_black_24dp,0,0,0);
        landscape.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stay_primary_landscape_black_24dp,0,0,0);
        linearLayout.addView(portrait);
        linearLayout.addView(landscape);
        return linearLayout;
    }


    /**
     * Show the dialog for choosing remote control's orientation
     */
    private void showOrientationDialog() {
        LinearLayout linearLayout = createOrientationDialogLinearLayout();
        Button portrait = (Button) linearLayout.getChildAt(0);
        Button landscape = (Button) linearLayout.getChildAt(1);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle("Select Orientation");
        builder.setView(linearLayout);
        final AlertDialog dialog = builder.create();

        portrait.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent startConfigureKeyboardActivity = new Intent(MainActivity.this, ConfigureRemoteControlActivity.class);
                startConfigureKeyboardActivity.putExtra("purpose", "create");
                startConfigureKeyboardActivity.putExtra("orientation", "portrait");
                MainActivity.this.startActivity(startConfigureKeyboardActivity);
            }
        });

        landscape.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent startConfigureKeyboardActivity = new Intent(MainActivity.this, ConfigureRemoteControlActivity.class);
                startConfigureKeyboardActivity.putExtra("purpose", "create");
                startConfigureKeyboardActivity.putExtra("orientation", "landscape");
                MainActivity.this.startActivity(startConfigureKeyboardActivity);
            }
        });

        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = Utils.dpToPx(Parameters.ORIENTATION_DIALOG_WIDTH_DP, this);
        lp.height = Utils.dpToPx(Parameters.ORIENTATION_DIALOG_HEIGHT_DP, this);
        window.setAttributes(lp);
    }

    /**
     * Populate the saved remote control configuration arraylist from files
     *
     * @param remoteControlConfigs The remote control configuration arraylist
     */
    private void populateRemoteControlConfigs(ArrayList<RemoteControlConfig> remoteControlConfigs) {
        File[] fileList = getFilesDir().listFiles();

        for (File f : fileList) {
            try {
                FileInputStream fis = openFileInput(f.getName());
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                String remoteControlName = f.getName();
                int orientation = Integer.parseInt(br.readLine());
                remoteControlConfigs.add(new RemoteControlConfig(remoteControlName, orientation));
                br.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Connect a phone's client to a PC server
     *
     * @param remoteControlConfig The configuration of the selected remote control
     */
    private void connectServer(RemoteControlConfig remoteControlConfig) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String serverIPString = sharedPref.getString("set_server_ip", "");
        Client client = new Client(this, serverIPString, remoteControlConfig);
        new Thread(client).start();
    }

    /**
     * Get the last time any button is being clicked
     * @return The last time any button is being clicked
     */
    public long getLastClickTime() {
        return lastClickTime;
    }


    /**
     * Set the last time any button is being clicked
     * @param lastClickTime The last time any button is being clicked
     */
    public void setLastClickTime(long lastClickTime) {
        this.lastClickTime = lastClickTime;
    }

}
