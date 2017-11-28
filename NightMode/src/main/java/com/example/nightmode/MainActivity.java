package com.example.nightmode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void switchMode(View view) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES) {
            sp.edit().putBoolean(SettingsUtil.KEY_NIGHT_MODE, false).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            sp.edit().putBoolean(SettingsUtil.KEY_NIGHT_MODE, true).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
        recreate();
    }

    public void jump(View view) {
        startActivity(new Intent(this,Main2Activity.class));
    }
}
