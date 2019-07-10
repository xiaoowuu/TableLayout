package com.github.xiaoowuu.tableview.app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.xiaoowuu.tableview.TableView;

/**
 * @author xiaoowuu
 */
public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        TableView tableView = findViewById(R.id.table);
        tableView.setAdapter(new TableView.PreviewAdapter());
    }
}
