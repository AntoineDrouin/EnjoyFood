package com.antoinedrouin.enjoyfood;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ExpandableListView;

public class Menu extends Activity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        context = getApplicationContext();

        ExpandableListView explvMenu = (ExpandableListView) findViewById(R.id.explvMenu);
    }
}
