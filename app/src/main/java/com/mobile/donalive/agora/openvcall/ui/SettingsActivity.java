package com.mobile.donalive.agora.openvcall.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.donalive.R;
import com.mobile.donalive.agora.openvcall.model.ConstantApp;
import com.mobile.donalive.agora.openvcall.ui.layout.SettingsButtonDecoration;
import com.mobile.donalive.agora.openvcall.ui.layout.VideoEncResolutionAdapter;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupUI();
    }

    private void setupUI() {

        RecyclerView videoResolutionList = (RecyclerView) findViewById(R.id.settings_video_resolution);
        videoResolutionList.setHasFixedSize(true);
        videoResolutionList.addItemDecoration(new SettingsButtonDecoration());

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int resolutionIdx = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_RESOLUTION, ConstantApp.DEFAULT_VIDEO_ENC_RESOLUTION_IDX);
        int fpsIdx = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_FPS, ConstantApp.DEFAULT_VIDEO_ENC_FPS_IDX);

        VideoEncResolutionAdapter videoResolutionAdapter = new VideoEncResolutionAdapter(this, resolutionIdx);
        videoResolutionAdapter.setHasStableIds(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        videoResolutionList.setLayoutManager(layoutManager);

        videoResolutionList.setAdapter(videoResolutionAdapter);

        Spinner videoFpsSpinner = (Spinner) findViewById(R.id.settings_video_frame_rate);

        ArrayAdapter<CharSequence> videoFpsAdapter = ArrayAdapter.createFromResource(this,
                R.array.string_array_frame_rate, R.layout.simple_spinner_item_light);
        videoFpsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        videoFpsSpinner.setAdapter(videoFpsAdapter);

        videoFpsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_FPS, position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        videoFpsSpinner.setSelection(fpsIdx);
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
