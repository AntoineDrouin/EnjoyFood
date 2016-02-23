package com.antoinedrouin.enjoyfood;

import android.app.LocalActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class Etablissement extends AppCompatActivity {

    int tab, tabCount;
    Context context;
    static Etablissement instEtab;

    TabHost tabHost;
    TabWidget tw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etablissement);

        context = getApplicationContext();
        instEtab = this;

        Bundle extras = getIntent().getExtras();
        String nomEtab =  extras.getString(getString(R.string.extraEtabName), getString(R.string.tabEtab));
        ((TextView)  findViewById(R.id.lblNomEtab)).setText(nomEtab);

        tabHost = (TabHost) findViewById(R.id.tabHostEtab);
        tw = (TabWidget) tabHost.findViewById(android.R.id.tabs);

        LocalActivityManager mlam = new LocalActivityManager(this, false);
        mlam.dispatchCreate(savedInstanceState);
        tabHost.setup(mlam);

        // Charge les onglets
        CustomTab customTab = new CustomTab(context, tabHost, tw, getString(R.string.varEtab));
        tabHost = customTab.load();

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

    public static Etablissement getInstance(){
        return instEtab;
    }
}
