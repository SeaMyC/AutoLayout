package com.odpautolayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AutoLayout autoLayout;
    private ArrayList<String> objects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        autoLayout = (AutoLayout) findViewById(R.id.autoLayout);
        autoLayout.addItemListener(new AutoLayout.ItemListener() {
            @Override
            public void registerListener(int position, TextView textView) {
                Toast.makeText(MainActivity.this, "position:" + position + "->" + textView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void click(View view) {
        objects = new ArrayList<>();
        objects.add("大苹果");
        objects.add("金丝肉松饼");
        objects.add("美的冰箱");
        objects.add("男装短袖");
        objects.add("格力空调洗衣机热水器");
        autoLayout.loadView(objects);
    }
    public void oneClick(View view) {
//        autoLayout.clearChildView();
        objects.clear();
        objects.add("one");
        autoLayout.loadView(objects);
    }
}