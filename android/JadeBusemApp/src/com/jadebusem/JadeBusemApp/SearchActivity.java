package com.jadebusem.JadeBusemApp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by alanhawrot on 19.03.14.
 */
public class SearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("Tutaj będą wyniki wyszukiwania, zawężona lista");
        setContentView(textView);
    }

}
