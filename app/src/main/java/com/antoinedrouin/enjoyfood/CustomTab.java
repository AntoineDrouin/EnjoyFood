package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

/**
 * Created by cdsm04 on 16/02/2016.
 */
public class CustomTab {

    Context ctContext;
    TabHost ctTabHost;
    TabWidget ctTw;
    String ctType;

    TabHost.TabSpec tabSpec = null;
    View tabView;
    TextView tv;
    ImageView tabImageView;

    public CustomTab(Context context, TabHost tabHost, TabWidget tabWidget, String type) {
        ctContext = context;
        ctTabHost = tabHost;
        ctTw = tabWidget;
        ctType = type;
    }

    public TabHost load() {
        for (int i = 0; i < 3; i++) {

            if (ctType.equals(ctContext.getString(R.string.varTabs))) {
                switch (i) {
                    case 0 :
                        tabSpec = ctTabHost
                                .newTabSpec("Etab")
                                .setIndicator(ctContext.getString(R.string.tabEtab))
                                .setContent(new Intent(ctContext, Etablissements.class));
                        break;
                    case 1 :
                        tabSpec = ctTabHost
                                .newTabSpec("Pan")
                                .setIndicator(ctContext.getString(R.string.tabPanier))
                                .setContent(new Intent(ctContext, Panier.class));
                        break;
                    case 2 :
                        tabSpec = ctTabHost
                                .newTabSpec("Com")
                                .setIndicator(ctContext.getString(R.string.tabCommandes))
                                .setContent(new Intent(ctContext, Commandes.class));
                        break;
                }
            }

            else if (ctType.equals(ctContext.getString(R.string.varEtab))) {
                switch (i) {
                    case 0:
                        tabSpec = ctTabHost
                                .newTabSpec("Coord")
                                .setIndicator(ctContext.getString(R.string.tabCoord))
                                .setContent(new Intent(ctContext, Coordonnees.class));
                        break;
                    case 1:
                        tabSpec = ctTabHost
                                .newTabSpec("Menu")
                                .setIndicator(ctContext.getString(R.string.tabMenu))
                                .setContent(new Intent(ctContext, Menu.class));
                        break;
                    case 2:
                        tabSpec = ctTabHost
                                .newTabSpec("Notes")
                                .setIndicator(ctContext.getString(R.string.tabNotes))
                                .setContent(new Intent(ctContext, Notes.class));
                        break;
                }
            }

            // Réduit la hauteur et la police des onglets
            ctTabHost.addTab(tabSpec);
            ctTabHost.getTabWidget().getChildAt(i).getLayoutParams().height /= 1.5;

            tabView = ctTw.getChildTabViewAt(i);
            tv = (TextView) tabView.findViewById(android.R.id.title);
            tv.setSingleLine();
            tv.setTextSize(12);

            // Charge les icônes
            tabImageView = (ImageView) ctTabHost.getTabWidget().getChildTabViewAt(i).findViewById(android.R.id.icon);
            tabImageView.setVisibility(View.VISIBLE);

            if (ctType.equals(ctContext.getString(R.string.varTabs))) {
                switch (i) {
                    case 0 : tabImageView.setImageResource(R.drawable.ic_eta); break;
                    case 1 : tabImageView.setImageResource(R.drawable.ic_pan); break;
                    case 2 : tabImageView.setImageResource(R.drawable.ic_com); break;
                }
            }
            else if (ctType.equals(ctContext.getString(R.string.varEtab))) {
                switch (i) {
                    case 0: tabImageView.setImageResource(R.drawable.ic_coo); break;
                    case 1: tabImageView.setImageResource(R.drawable.ic_menu); break;
                    case 2: tabImageView.setImageResource(R.drawable.ic_not); break;
                }
            }
        }

        return ctTabHost;
    }
}


