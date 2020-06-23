package ru.netology.lists;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ListViewActivity extends AppCompatActivity {

    private static final String KEY_TITLE = "key_title";
    private static final String KEY_COUNT = "key_count";
    private static String STORAGE_FILE = "storage_file.txt";

    private BaseAdapter listContentAdapter;
    private List<Map<String,String>> values;
    private List<Map<String,String>> result;
    private FloatingActionButton addBtn;
    private File file;
    private ImageView delImV;

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        initViews();

        values = prepareContent();

        listContentAdapter = createAdapter(values);

        list.setAdapter(listContentAdapter);
    }

    private File getValuesFile(){
        return new File(getExternalFilesDir(null),STORAGE_FILE);
    }

    private void saveFile(List<Map<String,String>> values, File file){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Map<String,String> map : values){
                String text = map.get(KEY_TITLE);
                String count = map.get(KEY_COUNT);
                writer.write(text + ";" + count);
                writer.write("\n\n");
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private List<Map<String,String>> loadFromFile(File file){
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int symbol;
            while ((symbol = reader.read()) != -1) {
                sb.append((char) symbol);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fullFile = sb.toString();
        String[] lines = fullFile.split("\n\n");
        for (String line : lines) {
            String[] title = line.split(";");
            if (title.length != 2) {
                return result;
            } else {
                Map<String, String> map = new HashMap<>();
                map.put(KEY_TITLE, title[0]);
                map.put(KEY_COUNT, title[1] + "");
                result.add(map);
            }
        }
        return result;
    }

    private void initViews() {
        list = findViewById(R.id.listView);
        addBtn = findViewById(R.id.fab);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] newStr = {"New text. New text. New text."};
                addToResult(newStr);
                saveFile(result,file);
                listContentAdapter.notifyDataSetChanged();
            }
        });
    }

    private View.OnClickListener removeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            values.remove(position);
            saveFile(values, getValuesFile());
            listContentAdapter.notifyDataSetChanged();
        }
    };

    @NonNull
    private BaseAdapter createAdapter(List<Map<String,String>> values) {
        return new SimpleAdapter(this,values,R.layout.item_list, new String[]{KEY_TITLE,KEY_COUNT},
                new int[]{R.id.textView,R.id.symbCountTv}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View root = super.getView(position, convertView, parent);
                View removeButton = root.findViewById(R.id.imageView);
                removeButton.setTag(position);
                removeButton.setOnClickListener(removeClickListener);
                return root;
            }
        };

    }

    @NonNull
    private List<Map<String,String>> prepareContent() {
        result = new ArrayList<>();
        file = getValuesFile();
        if (file.exists()){
            result = loadFromFile(file);
        }else {
            String[] titles = getString(R.string.large_text).split("\n\n");
            addToResult(titles);
            saveFile(result,file);
        }

        return result;
    }

    public void addToResult(String[] titles) {
        for (String title : titles) {
            Map<String, String> map = new HashMap<>();
            map.put(KEY_TITLE, title);
            map.put(KEY_COUNT, title.length() + "");
            result.add(map);
        }
    }

}

