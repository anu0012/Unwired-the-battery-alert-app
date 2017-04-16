package com.darkguyy.chargealert;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    public static final String PREFERENCES_NAME = "com.pandamonium.chargealert";
    public static final String PREFERENCE_KEY_ENABLED = "enabled";
    public static final String PREFERENCE_KEY_VIBRATE = "vibrate";
    public static final String PREFERENCE_KEY_SOUND = "sound";
    public static final String PREFERENCE_KEY_ALERT_LEVEL = "level";

    private SharedPreferences mPreferences;
    private Switch mEnabledSwitch;
    IntentFilter ifilter;
    Intent batteryStatus;
    Button levelButton;
    //MediaPlayer mediaPlayer=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mPreferences = getSharedPreferences(PREFERENCES_NAME, 0);
        setContentView(R.layout.activity_main);

        levelButton = (Button) findViewById(R.id.button_level);

        levelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(mediaPlayer!=null){
//                    mPreferences.edit().remove(PREFERENCE_KEY_ALERT_LEVEL).apply();
//                    mediaPlayer.release();
//                    mediaPlayer = null;
//                }
                if(MainService.mediaPlayer !=null && MainService.mediaPlayer.isPlaying()){
                    MainService.mediaPlayer.stop();
                    //MainService.mediaPlayer = null;
                }
            }
        });

        batteryLevel();

        ((TextView) findViewById(R.id.version_text)).setText(BuildConfig.VERSION_NAME);

        boolean isEnabled = mPreferences.getBoolean(PREFERENCE_KEY_ENABLED, false);
        mEnabledSwitch = (Switch) findViewById(R.id.enabled_switch);
        mEnabledSwitch.setChecked(isEnabled);
        mEnabledSwitch.setOnCheckedChangeListener(this);

        Switch vibrateSwitch = (Switch) findViewById(R.id.vibrate_switch);
        vibrateSwitch.setChecked(mPreferences.getBoolean(PREFERENCE_KEY_VIBRATE, false));
        vibrateSwitch.setOnCheckedChangeListener(this);

        Switch soundSwitch = (Switch) findViewById(R.id.sound_switch);
        soundSwitch.setChecked(mPreferences.getBoolean(PREFERENCE_KEY_SOUND, false));
        soundSwitch.setOnCheckedChangeListener(this);


        //Spinner
        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems("5","10", "20", "30", "40", "50","60","70","80","90","100");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                mPreferences.edit().putInt(PREFERENCE_KEY_ALERT_LEVEL, Integer.parseInt(item)).apply();
                Toast.makeText(view.getContext(), "Selected level is " + item, LENGTH_LONG).show();
            }
        });

        MainService.startIfEnabled(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        batteryLevel();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.feedback) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse(String.format("mailto:%s?subject=%s", getString(R.string.email), getString(R.string.app_name))));
//            intent.setType("message/rfc822");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.no_email, Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId() == R.id.How_to_use){
            Intent intent = new Intent(MainActivity.this,HowToUse.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();

        switch (id) {
            case R.id.enabled_switch:
                mPreferences.edit().putBoolean(PREFERENCE_KEY_ENABLED, isChecked).apply();
                MainService.startIfEnabled(this);
                break;
            case R.id.vibrate_switch:
                mPreferences.edit().putBoolean(PREFERENCE_KEY_VIBRATE, isChecked).apply();
                break;
            case R.id.sound_switch:
                mPreferences.edit().putBoolean(PREFERENCE_KEY_SOUND, isChecked).apply();
                break;
        }
    }

    private BroadcastReceiver mBatteryLevelReciver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = context.registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;

        levelButton.setText("BATTERY LEVEL\n" + (int)(batteryPct*100) + "%");

        if(!isConnected(context)){
            if(MainService.mediaPlayer !=null){
                MainService.mediaPlayer.release();
                MainService.mediaPlayer = null;
            }
        }


//            SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, 0);
//            if(((int)(batteryPct*100) == preferences.getInt(MainActivity.PREFERENCE_KEY_ALERT_LEVEL,0) || (int)(batteryPct*100) == 100) && isConnected(context)){
//                Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
//                vibrator.vibrate(3000);
//                Toast.makeText(context,"Battery reached the level " + (int)(batteryPct*100) + "% \nRemove from charging !!!",Toast.LENGTH_LONG).show();
//                if(mediaPlayer == null)
//                {
//                    mediaPlayer = MediaPlayer.create(context, R.raw.ringtone);
//                    mediaPlayer.start();
//                }
//
//            }

        }
    };

    private void batteryLevel() {
        IntentFilter batteryLevelFliter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryLevelReciver, batteryLevelFliter);
    }

    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

}
