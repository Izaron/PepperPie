package com.izaron.pepperpied;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Filter filter;
    private int currentTheme;
    private Set<String> fileSet;
    public static Set<String> uriSet;
    public static Set<String> subgroupSet;
    public static Map<String, String> titleMap;
    public static Map<String, String> subgroupMap;
    public static List<String> convertedFileNameList;
    public static List<String> fileNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        convertedFileNameList = new ArrayList<>();
        fileNameList = new ArrayList<>();

        fileSet = new HashSet<>();
        readFileNames(fileNameList, convertedFileNameList);

        uriSet = new HashSet<>();
        subgroupSet = new HashSet<>();
        titleMap = new HashMap<>();
        subgroupMap = new HashMap<>();
        parseJson();

        final List<String> subgroupList = new ArrayList<>();
        subgroupList.add("All algorithms");
        for (String s : subgroupSet)
            subgroupList.add(s);

        ListView listView = (ListView) findViewById(R.id.listView);
        SearchableAdapter<String> adapter = new SearchableAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, subgroupList.toArray(new String[subgroupList.size()]));

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
                //Intent intent = new Intent(getBaseContext(), CodeActivity.class);
                //String str = (String) parent.getItemAtPosition(position);
                //int realPosition = list.indexOf(str);
                //intent.putExtra("FILE_NAME", fileNameList.get(realPosition));
                //intent.putExtra("CONVERTED_FILE_NAME", convertedFileNameList.get(realPosition));
                //startActivity(intent);

                Intent intent = new Intent(getBaseContext(), GroupActivity.class);
                intent.putExtra("currentGroup", subgroupList.get(position));
                startActivity(intent);
            }
        });
    }

    void parseJson() {
        String jsonFile = readFileFromAsset("algo_group.json");
        TreeSet<String> set = new TreeSet<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonFile);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String uri = jsonObject.getString("uri");
                uri = uri.replace('-', '_');
                uri = getRealClassName(uri);
                String title = jsonObject.getString("title");
                String group = jsonObject.getString("group");
                String subgroup = jsonObject.getString("subgroup");

                boolean kek = false;
                if (uri.equals("treap_bst"))
                    kek = true;
                set.add(uri);

                if (!group.equals("Algorithms and Data Structures"))
                    continue;

                JSONArray implementations = jsonObject.getJSONArray("implementations");
                boolean hasJava = false;
                for (int z = 0; z < implementations.length(); z++) {
                    if (implementations.getJSONObject(z).getString("language").equals("java"))
                        hasJava = true;
                }
                if (!hasJava)
                    continue;

                subgroupSet.add(subgroup);

                uri = getRealClassName(uri);
                if (!fileSet.contains(uri)) {
                    Log.i("CONT", uri);
                    continue;
                }

                fileSet.remove(uri);
                uriSet.add(uri);
                titleMap.put(uri, title);
                subgroupMap.put(uri, subgroup);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (String s : fileSet)
            Log.i("CONT2", s);

        for (String s : subgroupSet)
            Log.i("GROUPS", s);
    }

    String getRealClassName(String uri) {
        if (uri.equals("graph_coloring")) return "coloring";
        if (uri.equals("combinatorial_enumeration")) return "combinatorial_enumerations";
        if (uri.equals("random_tree")) return "random_graph";
        if (uri.equals("binary_heap_increase_prio")) return "binary_heap_with_increase_priority";
        if (uri.equals("fenwick_tree_2d")) return "fenwick_tree2_d";
        if (uri.equals("fenwick_tree_on_map")) return "n_u_l_l";
        if (uri.equals("hashmap")) return "chaining_hash_map";
        if (uri.equals("kdtree")) return "kd_tree_point_query";
        if (uri.equals("kdtree_rect")) return "kd_tree_rect_query";
        if (uri.equals("sparse_table_rmq")) return "rmq_sparse_table";
        if (uri.equals("rtree")) return "r_tree";
        if (uri.equals("segment_tree_fast_add_max_2d")) return "segment_tree2_d_fast_add_max";
        if (uri.equals("segment_tree_add_max")) return "segment_tree_interval_add_max";
        if (uri.equals("segment_tree_sum_lower_boud")) return "segment_tree_sum_lower_bound";
        if (uri.equals("treap_bst")) return "treap_simple";
        if (uri.equals("treap")) return "treap_implicit_key";
        if (uri.equals("biconnected_components")) return "biconnectivity";
        if (uri.equals("strongly_connected_components")) return "s_c_c_kosaraju";
        if (uri.equals("scc_tarjan")) return "s_c_c_tarjan";
        if (uri.equals("topological_sorting")) return "topological_sort";
        if (uri.equals("edit_distance")) return "edit_distance";
        if (uri.equals("lcs")) return "lcs";
        if (uri.equals("longest_palindrome")) return "max_palindrome";
        if (uri.equals("perfect_matches_count")) return "perfect_matching_count";
        if (uri.equals("linear_equality")) return "linear_eqaulity";
        if (uri.equals("matrix_multiply")) return "matrix_chain_multiply";
        if (uri.equals("dynamic_tsp")) return "shortest_hamiltonian_cycle";
        if (uri.equals("assignment_hungary")) return "hungarian";
        if (uri.equals("min_cost_flow_bf")) return "min_cost_flow_b_f";
        if (uri.equals("min_cost_flow_dense_pot")) return "min_cost_flow_dense";
        if (uri.equals("min_cost_flow_pot")) return "min_cost_flow";
        if (uri.equals("dinic_flow")) return "max_flow_dinic";
        if (uri.equals("edmonds_karp")) return "max_flow_edmond_karp";
        if (uri.equals("ford_fulkerson")) return "max_flow_ford_fulkerson";
        if (uri.equals("preflow")) return "max_flow_preflow";
        if (uri.equals("closest_pair")) return "closest2_points";
        if (uri.equals("diametr")) return "n_u_l_l";
        if (uri.equals("force_based_graph_drawing")) return "force_based_graph_drawer";
        if (uri.equals("find_segments_intersection")) return "segments_intersection_scanline";
        if (uri.equals("polygon_polygon_intersection")) return "polygons_intersection";
        if (uri.equals("rectangular_union_area")) return "rectangle_union";
        if (uri.equals("segments_union_length")) return "segments_union";
        if (uri.equals("tree_isomorphism")) return "n_u_l_l";
        if (uri.equals("gauss_elimination")) return "gauss";
        if (uri.equals("sparse_table_lca")) return "lca_sparse_table";
        if (uri.equals("classification")) return "n_u_l_l";
        if (uri.equals("convolutional_neural_network")) return "n_u_l_l";
        if (uri.equals("hopcroft_karp")) return "n_u_l_l";
        if (uri.equals("kuhn_matching2")) return "max_matching2";
        if (uri.equals("kuhn_matching")) return "max_matching";
        if (uri.equals("edmonds_matching")) return "max_matching_edmonds";
        if (uri.equals("2_sat")) return "two_sat";
        if (uri.equals("bigint")) return "n_u_l_l";
        if (uri.equals("array_rotation")) return "array_rotate";
        if (uri.equals("cycle_finding")) return "cycle_detection";
        if (uri.equals("lis_nlogn")) return "lis2";
        if (uri.equals("max_rectangle")) return "max_rectangle";
        if (uri.equals("prime_numbers")) return "primes_and_divisors";
        if (uri.equals("fft")) return "f_f_t";
        if (uri.equals("polynom_roots")) return "n_u_l_l";
        if (uri.equals("simpson_integrating")) return "simpson_integration";
        if (uri.equals("simplex_algorithm")) return "simplex";
        if (uri.equals("kth_order_statistics")) return "nth_element";
        if (uri.equals("quick_sort")) return "quicksort";
        if (uri.equals("mst_prim")) return "prim_heap";
        if (uri.equals("mst_prim_simple")) return "prim";
        if (uri.equals("string_hashing")) return "hashing";
        if (uri.equals("prefix_function")) return "kmp";
        if (uri.equals("suffix_array_lcp")) return "n_u_l_l";

        //if (uri.equals("treap_bst")) return "treap_b_s_t";

        return uri;
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

    void readFileNames(List<String> fileNameList, List<String> convertedFileNameList) {
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            if (field.getType() == int.class) {
                fileSet.add(field.getName());
                fileNameList.add(field.getName());
                convertedFileNameList.add(convertName(field.getName()));
            }
        }
    }

    String readFileFromAsset(String fileName) {
        StringBuffer buffer = new StringBuffer();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(fileName)));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                buffer.append(mLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return buffer.toString();
    }

    String convertName(String fileName) {
        String[] strings = fileName.split("_");
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
