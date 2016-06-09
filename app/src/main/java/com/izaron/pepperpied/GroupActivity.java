package com.izaron.pepperpied;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
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
            if (subgroup.equals(groupName) || groupName.equals("All algorithms")) {
                fileNameList.add(s);
                convertedFileNameList.add(MainActivity.convertedFileNameList.get(i));
                //titleFileNameList.add(MainActivity.titleMap.get(s));
                titleFileNameList.add(s);
            }
        }


        ArrayList<HashMap<String, String>> myArrList = new ArrayList<>();
        HashMap<String, String> map;

        for (int i = 0; i < titleFileNameList.size(); i++) {
            map = new HashMap<>();
            map.put("Name", convertedFileNameList.get(i));
            String info = "<null>";
            if (MainActivity.titleMap.containsKey(fileNameList.get(i)))
                info = MainActivity.titleMap.get(fileNameList.get(i));
            info = info.replace("{n}", "{N}");
            info = info.replace("{m}", "{M}");
            info = info.replace("N * \\\\log{N}", "N\\\\log{N}");
            while (info.indexOf('$') != -1) {
                info = info.replaceFirst("\\$", "<b><i>");
                info = info.replaceFirst("\\$", "</i></b>");
            }
            info = info.replace("\\log", "</i> log<i>");
            info = info.replace("{", "").replace("}", "");
            info = info.replace("^2", "</i> \u00B2<i>").replace("^3", "</i> \u00B3<i>").replace("^4", "</i> \u2074<i>").replace("^n", "</i> \u207F<i>").replace("^N", "</i> \u207F<i>");
            info = info.replace("\\sqrt", "\u221A");
            info = info.replace("*", "</i> *<i>");
            map.put("Tel", info);
            myArrList.add(map);
        }


        //SimpleAdapter adapter = new SimpleAdapter(this, myArrList, android.R.layout.simple_list_item_2,
        //        new String[] {"Name", "Tel"},
        //        new int[] {android.R.id.text1, android.R.id.text2});

        SearchableSimpleAdapter adapter = new SearchableSimpleAdapter(this, myArrList, android.R.layout.simple_list_item_2,
                new String[] {"Name", "Tel"},
                new int[] {R.id.customTextView, android.R.id.text2});

        ListView listView = (ListView) findViewById(R.id.groupListView);
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, myArrList,
        //        android.R.layout.simple_list_item_2, titleFileNameList.toArray(new String[titleFileNameList.size()]));

        assert listView != null;
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(false);
        filter = adapter.getFilter();

        final ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++)
            list.add(((HashMap<String, String>)adapter.getItem(i)).get("Name"));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), CodeActivity.class);
                //String str = (String) parent.getItemAtPosition(position);
                String str = ((HashMap<String, String>)parent.getItemAtPosition(position)).get("Name");
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
