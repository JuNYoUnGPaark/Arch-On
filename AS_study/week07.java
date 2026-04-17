package com.example.week01;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    View view1;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view1 = findViewById(R.id.layout);
        text = (TextView) findViewById(R.id.TextView01);
        registerForContextMenu(text);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("컨텍스트메뉴");
        menu.add(0, 1, 0, "배경색: RED");
        menu.add(0, 2, 0, "배경색: GREEN");
        menu.add(0, 3, 0, "배경색: BLUE");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == 1) {
            view1.setBackgroundColor(Color.RED);
            return true;
        } else if (id == 2) {
            view1.setBackgroundColor(Color.GREEN);
            return true;
        } else if (id == 3) {
            view1.setBackgroundColor(Color.BLUE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.blue) {
            view1.setBackgroundColor(Color.BLUE);
            return true;
        } else if (id == R.id.green) {
            view1.setBackgroundColor(Color.GREEN);
            return true;
        } else if (id == R.id.red) {
            view1.setBackgroundColor(Color.RED);
            return true;
        } else if (id == R.id.reset) {
            view1.setBackgroundColor(Color.WHITE);
            return true;
        } else if (id == R.id.random) {
            int r = (int)(Math.random() * 256);
            int g = (int)(Math.random() * 256);
            int b = (int)(Math.random() * 256);
            view1.setBackgroundColor(Color.rgb(r, g, b));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}