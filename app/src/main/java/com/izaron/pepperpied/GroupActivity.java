package com.izaron.pepperpied;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Filter filter;
    private int currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        final List<String> titleFileNameList = new ArrayList<>();
        final List<String> convertedFileNameList = new ArrayList<>();
        final List<String> fileNameList = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        String groupName = extras.getString("currentGroup");

        for (int i = 0; i < MainActivity.fileNameList.size(); i++) {
            String s = MainActivity.fileNameList.get(i);
            String subgroup = "";
            if (MainActivity.subgroupMap.containsKey(s))
                subgroup = MainActivity.subgroupMap.get(s);
            if (subgroup.equals(groupName)) {
                fileNameList.add(s);
                convertedFileNameList.add(MainActivity.convertedFileNameList.get(i));
                titleFileNameList.add(MainActivity.titleMap.get(s));
            }
        }

        ListView listView = (ListView) findViewById(R.id.groupListView);
        SearchableAdapter<String> adapter = new SearchableAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, titleFileNameList.toArray(new String[titleFileNameList.size()]));

        assert listView != null;
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(false);
        filter = adapter.getFilter();

        final ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++)
            list.add(adapter.getItem(i));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), CodeActivity.class);
                String str = (String) parent.getItemAtPosition(position);
                int realPosition = list.indexOf(str);
                intent.putExtra("FILE_NAME", fileNameList.get(realPosition));
                intent.putExtra("CONVERTED_FILE_NAME", convertedFileNameList.get(realPosition));
                startActivity(intent);
            }
        });

        setTitle(groupName);
        displayHome();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_preferences:
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        if (changeTheme())
            recreate();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");

        return true;
    }

    void displayHome() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    boolean changeTheme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int appTheme = Integer.parseInt(preferences.getString("APP_THEME", "0"));
        if (appTheme == 0)
            setTheme(R.style.AppTheme);
        else if (appTheme == 1)
            setTheme(R.style.WhiteTheme);
        else if (appTheme == 2)
            setTheme(R.style.BlackTheme);
        if (appTheme != currentTheme) {
            currentTheme = appTheme;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filter.filter(newText);
        return false;
    }
}
