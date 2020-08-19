package com.example.ht_612;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ht_612.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TITLE = "TITLE";
    private static final String SUBTITLE = "SUBTITLE";
    private static final String SHARED_PREFS_NAME = "SHARED_PREFS_NAME";
    private static final String SHARED_PREFS_KEY = "SHARED_PREFS_KEY";
    private static final String BUNDLE_KEY = "BUNDLE";
    List<Map<String, String>> lines = new ArrayList<>();
    ArrayList<Integer> integersForBundle = new ArrayList<>();
    BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        adapter = createAdapter(lines);
        if (savedInstanceState != null)
            integersForBundle.addAll(Objects.requireNonNull(savedInstanceState.getIntegerArrayList(BUNDLE_KEY)));
        for (Integer i :
                integersForBundle) {
            lines.remove((int) i);
        }
        adapter.notifyDataSetChanged();

        ListView list = findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lines.remove(position);
                integersForBundle.add(position);
                adapter.notifyDataSetChanged();
            }
        });
        list.setAdapter(adapter);

        final SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lines.clear();
                init();
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });

    }

    public void init() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String savedString = sharedPreferences.getString(SHARED_PREFS_KEY, "");
        lines.addAll(prepareContent());
        if (savedString == null) {
            sharedPreferences.edit().putString(SHARED_PREFS_KEY, String.valueOf(lines)).apply();
        }
        integersForBundle = new ArrayList<>();
    }

    public List<Map<String, String>> prepareContent() {
        String[] lines = getString(R.string.large_text).split(";");
        List<Map<String, String>> list = new ArrayList<>();
        for (String string :
                lines) {
            Map<String, String> map = new HashMap<>();
            map.put(TITLE, string);
            map.put(SUBTITLE, string.length() + "");
            list.add(map);
        }
        return list;
    }

    public BaseAdapter createAdapter(List<Map<String, String>> list) {
        String[] from = {TITLE, SUBTITLE};
        int[] to = {R.id.text1, R.id.text2};
        return new SimpleAdapter(this, list, R.layout.textviews, from, to);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(BUNDLE_KEY, integersForBundle);
    }
}