package com.example.kangzhe.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.kangzhe.sample.R;


public class MainActivity extends AppCompatActivity {

    private Button recyclerList;

    private Button recyclerGrid;

    private Button recyclerStaggered;

    private Button simplePtr;

    private Button brand5Ptr;

    private Button horizontal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerList = (Button)findViewById(R.id.recycler_list);
        recyclerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RecyclerListViewActivity.class);
                startActivity(intent);
            }
        });

        recyclerGrid = (Button)findViewById(R.id.recycler_grid);
        recyclerGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RecyclerGridViewActivity.class);
                startActivity(intent);
            }
        });

        recyclerStaggered = (Button)findViewById(R.id.recycler_staggered);
        recyclerStaggered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RecyclerStaggeredViewActivity.class);
                startActivity(intent);
            }
        });

        simplePtr = (Button)findViewById(R.id.simple_ptr);
        simplePtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SimplePtrActivity.class);
                startActivity(intent);
            }
        });

        horizontal = (Button)findViewById(R.id.horizontal_mode);
        horizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,HorizontalActivity.class);
                startActivity(intent);
            }
        });
    }
}