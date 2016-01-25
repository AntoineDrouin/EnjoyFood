package com.antoinedrouin.enjoyfood;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class Etablissements extends Activity {

    int tab, tabCount;
    Context context;

    TabHost tabHost;
    TabWidget tw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etablissements);

        context = getApplicationContext();

        tabHost = (TabHost) findViewById(R.id.tabHostEtab);
        tw = (TabWidget) tabHost.findViewById(android.R.id.tabs);

        LocalActivityManager mlam = new LocalActivityManager(this, false);
        mlam.dispatchCreate(savedInstanceState);
        tabHost.setup(mlam);

        // Charge les onglets
        TabHost.TabSpec tabSpec = null;
        View tabView;
        TextView tv;
        ImageView tabImageView;

        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 0:
                    tabSpec = tabHost
                            .newTabSpec("Coord")
                            .setIndicator(getString(R.string.tabCoord))
                            .setContent(new Intent(this, Coordonnees.class));
                    break;
                case 1:
                    tabSpec = tabHost
                            .newTabSpec("Menu")
                            .setIndicator(getString(R.string.tabMenu))
                            .setContent(new Intent(this, Menu.class));
                    break;
                case 2:
                    tabSpec = tabHost
                            .newTabSpec("Notes")
                            .setIndicator(getString(R.string.tabNotes))
                            .setContent(new Intent(this, Notes.class));
                    break;
            }

            // Réduit la hauteur et la police des onglets
            tabHost.addTab(tabSpec);
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().height /= 1.5;

            tabView = tw.getChildTabViewAt(i);
            tv = (TextView) tabView.findViewById(android.R.id.title);
            tv.setSingleLine();
            tv.setTextSize(12);

            // Charge les icônes
            tabImageView = (ImageView) tabHost.getTabWidget().getChildTabViewAt(i).findViewById(android.R.id.icon);
            tabImageView.setVisibility(View.VISIBLE);

            switch (i) {
                case 0:
                    tabImageView.setImageResource(R.drawable.ic_coo);
                    break;
                case 1:
                    tabImageView.setImageResource(R.drawable.ic_menu);
                    break;
                case 2:
                    tabImageView.setImageResource(R.drawable.ic_not);
                    break;
            }
        }

        // Détecte les mouvements de glissement pour changer d'onglet

        tabHost.setOnTouchListener(new OnSwipeListener(context) {
            public void onSwipeLeft() {
                tabCount = tabHost.getTabWidget().getTabCount() - 1;
                tab = tabHost.getCurrentTab() + 1;

                if (tab > tabCount)
                    tabHost.setCurrentTab(0);
                else
                    tabHost.setCurrentTab(tab);
            }
            public void onSwipeRight() {
                tabCount = tabHost.getTabWidget().getTabCount() - 1;
                tab = tabHost.getCurrentTab() - 1;

                if (tab < 0)
                    tabHost.setCurrentTab(tabCount);
                else
                    tabHost.setCurrentTab(tab);
            }
        });
    }

}
