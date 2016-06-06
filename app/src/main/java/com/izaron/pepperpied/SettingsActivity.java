package com.izaron.pepperpied;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class SettingsActivity extends AppCompatActivity {

    private int currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeTheme();
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        setSettingsFragment();
        displayHome();
    }

    boolean changeTheme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int appTheme = Integer.parseInt(preferences.getString("APP_THEME", "1"));
        if (appTheme == 1)
            setTheme(com.izaron.pepperpied.R.style.WhiteTheme);
        else if (appTheme == 2)
            setTheme(com.izaron.pepperpied.R.style.BlackTheme);
        if (appTheme != currentTheme) {
            currentTheme = appTheme;
            return true;
        } else {
            return false;
        }
    }

    void setSettingsFragment() {
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    void displayHome() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
