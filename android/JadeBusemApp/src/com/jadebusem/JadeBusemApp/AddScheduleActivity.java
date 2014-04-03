package com.jadebusem.JadeBusemApp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by alanhawrot on 19.03.14.
 */
public class AddScheduleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("Tutaj będzie tworzony nowy rozkład (formularz do wypełnienia)");
        setContentView(textView);
    }
}
