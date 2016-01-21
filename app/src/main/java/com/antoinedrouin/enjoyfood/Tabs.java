package com.antoinedrouin.enjoyfood;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class Tabs extends AppCompatActivity {

    String utilisateur;
    int currentTab, tab, tabCount;
    Context context;

    DrawerLayout mDrawerLayout;
    TabHost tabHost;
    TabWidget tw;
    TextView lblTitreTab;
    EditText edtSearchEtab, edtSearchVille, edtSearchArticle, edtSearchCommande;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        context = getApplicationContext();
        currentTab = 0;

        tabHost = (TabHost) findViewById(R.id.tabHost);
        tw = (TabWidget)tabHost.findViewById(android.R.id.tabs);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        lblTitreTab = (TextView) findViewById(R.id.lblTitreTab);
        edtSearchEtab = (EditText) findViewById(R.id.edtSearchEtab);
        edtSearchVille = (EditText) findViewById(R.id.edtSearchVille);
        edtSearchArticle = (EditText) findViewById(R.id.edtSearchArticle);
        edtSearchCommande = (EditText) findViewById(R.id.edtSearchCommande);


        LocalActivityManager mlam = new LocalActivityManager(this, false);
        mlam.dispatchCreate(savedInstanceState);
        tabHost.setup(mlam);

        // Réduit la hauteur et la police des onglets
        TabSpec tabSpec = null;
        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 0 :
                    tabSpec = tabHost
                        .newTabSpec("Etab")
                        .setIndicator(getString(R.string.tabEtab))
                        .setContent(new Intent(this, Etablissements.class));
                    break;
                case 1 :
                    tabSpec = tabHost
                        .newTabSpec("Pan")
                        .setIndicator(getString(R.string.tabPanier))
                        .setContent(new Intent(this, Panier.class));
                    break;
                case 2 :
                    tabSpec = tabHost
                        .newTabSpec("Com")
                        .setIndicator(getString(R.string.tabCommandes))
                        .setContent(new Intent(this, Commandes.class));
                    break;
            }

            tabHost.addTab(tabSpec);
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().height /= 1.5;

            View tabView = tw.getChildTabViewAt(i);
            TextView tv = (TextView)tabView.findViewById(android.R.id.title);
            tv.setSingleLine();
            tv.setTextSize(12);
        }

        checkPref();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                currentTab = tabHost.getCurrentTab();
                switch (currentTab) {
                    case 0 :
                        loadEtabTabDrawer();
                        break;
                    case 1 :
                        loadPanTabDrawer();
                        break;
                    case 2 :
                        loadComTabDrawer();
                        break;
                }
            }
        });

        // Détecte les mouvements de glissement pour changer d'onglet

//        tabHost.setOnTouchListener(new OnSwipeListener(context) {
//            public void onSwipeLeft() {
//                tabCount = tabHost.getTabWidget().getTabCount() - 1;
//                tab = tabHost.getCurrentTab();
//                tab++;
//
//                if (tab > tabCount)
//                    tabHost.setCurrentTab(0);
//                else
//                    tabHost.setCurrentTab(tab);
//            }
//            public void onSwipeRight() {
//                tabCount = tabHost.getTabWidget().getTabCount() - 1;
//                tab = tabHost.getCurrentTab();
//                tab--;
//
//                if (tab < 0)
//                    tabHost.setCurrentTab(tabCount);
//                else
//                    tabHost.setCurrentTab(tab);
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPref();
    }

    // MENU

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        checkPref();

        // Prend l'id du menu sélectionné
        switch (item.getItemId()) {
            case R.string.menuIdCompte :
                if (utilisateur.equals(""))
                    startActivity(new Intent(this, Login.class));
                else
                    startActivity(new Intent(this, Compte.class));
                return true;
            case R.string.menuIdInfo :
                startActivity(new Intent(this, Informations.class));
                return true;
            case R.string.menuIdReglages :
                startActivity(new Intent(this, Reglages.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkPref() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        utilisateur = pref.getString(getString(R.string.prefUser), "");
    }

    public void onClickSearch(View v) {
        switch (currentTab) {
            case 0 : break;
            case 1 : break;
            case 2 : break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void loadEtabTabDrawer() {
        lblTitreTab.setText(getString(R.string.lblSearchEtab));
        edtSearchEtab.setVisibility(View.VISIBLE);
        edtSearchVille.setVisibility(View.VISIBLE);
        edtSearchArticle.setVisibility(View.GONE);
        edtSearchCommande.setVisibility(View.GONE);
    }

    private void loadPanTabDrawer() {
        lblTitreTab.setText(getString(R.string.lblSearchPanier));
        edtSearchEtab.setVisibility(View.GONE);
        edtSearchVille.setVisibility(View.GONE);
        edtSearchArticle.setVisibility(View.VISIBLE);
        edtSearchCommande.setVisibility(View.GONE);
    }

    private void loadComTabDrawer() {
        lblTitreTab.setText(getString(R.string.lblSearchCom));
        edtSearchEtab.setVisibility(View.GONE);
        edtSearchVille.setVisibility(View.GONE);
        edtSearchArticle.setVisibility(View.GONE);
        edtSearchCommande.setVisibility(View.VISIBLE);
    }
}
