package com.rokasjankunas.ticktock.activities.custom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.rokasjankunas.ticktock.R;

public class HourlyBeepActivity extends Activity {

    private Integer volume; //player uses 50+input*5
    private Integer duration; //player uses 50+input*100
    private SharedPreferences sharedPreferences;
    private SeekBar volumeBar;
    private SeekBar durationBar;
    private Button play;
    private Switch switcher;
    private Boolean hourlyBeepEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTheme(com.rokasjankunas.ticktock.R.style.MainStyle);
        setContentView(R.layout.hourly_beep_activity);

        sharedPreferences = getSharedPreferences(getString(R.string.sharedPreferences),Context.MODE_PRIVATE);
        volume = sharedPreferences.getInt(getString(R.string.beep_volume_preference),5);
        duration = sharedPreferences.getInt(getString(R.string.beep_duration_preference),1);
        hourlyBeepEnabled = sharedPreferences.getBoolean(getString(R.string.hourly_beep_preference),false);

        //volume:
        //real volume:  50 55 60 65 70 [75] 80 85 90 95 100
        //progress bar: 0  1  2  3  4  [5]  6  7  8  9  10

        //duration:
        //real duration: 50 [150] 250 350 450
        //progress bar:  0  [1]   2   3   4

        volumeBar = findViewById(R.id.seekBar);
        durationBar = findViewById(R.id.seekBar2);
        play = findViewById(R.id.battery_submit);
        switcher = findViewById(R.id.miscoptionsListSwitch);

        volumeBar.setProgress(volume);
        durationBar.setProgress(duration);
        switcher.setChecked(hourlyBeepEnabled);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                sharedPreferences.edit().putInt(getString(R.string.beep_volume_preference),progress).apply();
                volume = progress;
            }
        });

        durationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                sharedPreferences.edit().putInt(getString(R.string.beep_duration_preference),progress).apply();
                duration = progress;
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final ToneGenerator[] toneGenerator = {new ToneGenerator(AudioManager.STREAM_MUSIC, 50 + volume * 5)};
                    toneGenerator[0].startTone(ToneGenerator.TONE_CDMA_ABBR_INTERCEPT, 50 + duration * 100);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (toneGenerator[0] != null) {

                                toneGenerator[0].release();
                                toneGenerator[0] = null;
                            }
                        }

                    }, 100 + duration * 100);
                } catch (Exception ignored) {
                }
            }
        });

        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().putBoolean(getString(R.string.hourly_beep_preference),!hourlyBeepEnabled).apply();
                switcher.setChecked(!hourlyBeepEnabled);
                hourlyBeepEnabled = !hourlyBeepEnabled;
            }
        });

    }
}
