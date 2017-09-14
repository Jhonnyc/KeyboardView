package com.yonikal.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.yonikal.R;
import com.yonikal.keyboardview.KeyboardView;


public class MainActivity extends AppCompatActivity {

    // Views
    KeyboardView keyboardView;
    TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview = (TextView) findViewById(R.id.textview);
        keyboardView = (KeyboardView) findViewById(R.id.keyboard);
        keyboardView.bindTo(textview);
    }
}
