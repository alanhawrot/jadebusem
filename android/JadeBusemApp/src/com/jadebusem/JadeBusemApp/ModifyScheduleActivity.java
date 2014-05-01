package com.jadebusem.JadeBusemApp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by alanhawrot on 08.04.14.
 */
public class ModifyScheduleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("Tutaj będzie modyfikowany rozkład (formularz do wypełnienia)");
        setContentView(textView);
    }

}
