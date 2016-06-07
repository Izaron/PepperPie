package com.izaron.pepperpied;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Filter filter;
    private int currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final List<String> convertedFileNameList = new ArrayList<>();
        final List<String> fileNameList = new ArrayList<>();

        readFileNames(fileNameList, convertedFileNameList);

        ListView listView = (ListView) findViewById(R.id.listView);
        SearchableAdapter<String> adapter = new SearchableAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, convertedFileNameList.toArray(new String[convertedFileNameList.size()]));

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
    }

    boolean changeTheme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int appTheme = Integer.parseInt(preferences.getString("APP_THEME", "1"));
        if (appTheme == 1)
            setTheme(R.style.AppTheme);
        else if (appTheme == 2)
            setTheme(R.style.WhiteTheme);
        else if (appTheme == 3)
            setTheme(R.style.BlackTheme);
        if (appTheme != currentTheme) {
            currentTheme = appTheme;
            return true;
        } else {
            return false;
        }
    }

    void readFileNames(List<String> fileNameList, List<String> convertedFileNameList) {
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            if (field.getType() == int.class) {
                fileNameList.add(field.getName());
                convertedFileNameList.add(convertName(field.getName()));
            }
        }
    }

    String convertName(String fileName) {
        String[] strings = fileName.split("\\_");
        StringBuilder buffer = new StringBuilder();
        for (String string : strings)
            buffer.append(string.substring(0, 1).toUpperCase()).append(string.substring(1));
        buffer.append(".java");
        return buffer.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");

        return true;
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
