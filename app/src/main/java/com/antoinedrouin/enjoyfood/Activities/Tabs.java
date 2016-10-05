package com.antoinedrouin.enjoyfood.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antoinedrouin.enjoyfood.Classes.GoogleLocation;
import com.antoinedrouin.enjoyfood.Classes.PagerAdapter;
import com.antoinedrouin.enjoyfood.Fragments.Commandes;
import com.antoinedrouin.enjoyfood.Fragments.Etablissements;
import com.antoinedrouin.enjoyfood.Fragments.Panier;
import com.antoinedrouin.enjoyfood.R;

public class Tabs extends AppCompatActivity {

    private Context context;
    private static Tabs instTabs;
    private SharedPreferences pref;

    private String pseudo, compte;
    private int currentTab = 0;
    private int[] titleId = new int[] {R.string.tabEtab, R.string.tabPanier, R.string.tabCommandes};
    private int[] imageId = new int[] {R.drawable.ic_eta, R.drawable.ic_pan, R.drawable.ic_com};

    private DrawerLayout mDrawerLayout;
    ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView txtTitreTab;
    private EditText edtSearchEtab, edtSearchVille, edtSearchArticle, edtSearchCommande;
    private LinearLayout layoutVille;
    private RelativeLayout layoutLoading;

    private GoogleLocation googleLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        context = getApplicationContext();
        instTabs = this;

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        viewPager = (ViewPager) findViewById(R.id.viewPagerTabs);
        layoutVille = (LinearLayout) findViewById(R.id.layoutVille);
        layoutLoading = (RelativeLayout) findViewById(R.id.loadingPanel);
        txtTitreTab = (TextView) findViewById(R.id.txtTitreTab);
        edtSearchEtab = (EditText) findViewById(R.id.edtSearchEtab);
        edtSearchVille = (EditText) findViewById(R.id.edtSearchVille);
        edtSearchArticle = (EditText) findViewById(R.id.edtSearchPanier);
        edtSearchCommande = (EditText) findViewById(R.id.edtSearchCommande);

        // Remplis le viewPager
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), context, 0));

        // Donne le viewPager au tabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutTabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(currentTab).setText(titleId[currentTab]);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                switch (currentTab) {
                    case 0 : loadEtabTabDrawer(); break;
                    case 1 : loadPanTabDrawer(); break;
                    case 2 : loadComTabDrawer(); break;
                }

                // L'onglet actuel affiche le texte et non l'icône
                tab.setText(titleId[currentTab]);
                tab.setIcon(null);

                // Pour être sûr d'être synchro
                if (currentTab != viewPager.getCurrentItem())
                    viewPager.setCurrentItem(currentTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // L'onglet déselectionné reprend son icône et non plus son texte
                tab.setIcon(ContextCompat.getDrawable(context, imageId[currentTab]));
                tab.setText(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Ajoute un listener pour capter les changements d'état du drawer
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            // Permet de fermer le clavier en même temps que le drawer
            public void onDrawerClosed(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            @Override
            public void onDrawerSlide(View view, float v) {}
            @Override
            public void onDrawerOpened(View drawerView) {}
            @Override
            public void onDrawerStateChanged(int i) {}
        });
    }

    private void checkPref() {
        pseudo = pref.getString(getString(R.string.prefPseudo), "");
        compte = pref.getString(getString(R.string.prefCompte), "");
    }

    /** DRAWER **/

    // Affiche des champs du drawer en fonction de l'onglet actuel

    private void loadEtabTabDrawer() {
        txtTitreTab.setText(getString(R.string.txtSearchEtab));
        layoutVille.setVisibility(View.VISIBLE);
        edtSearchEtab.setVisibility(View.VISIBLE);
        edtSearchArticle.setVisibility(View.GONE);
        edtSearchCommande.setVisibility(View.GONE);
    }

    private void loadPanTabDrawer() {
        txtTitreTab.setText(getString(R.string.txtSearchPanier));
        layoutVille.setVisibility(View.GONE);
        edtSearchEtab.setVisibility(View.GONE);
        edtSearchArticle.setVisibility(View.VISIBLE);
        edtSearchCommande.setVisibility(View.GONE);
    }

    private void loadComTabDrawer() {
        txtTitreTab.setText(getString(R.string.txtSearchCom));
        layoutVille.setVisibility(View.GONE);
        edtSearchEtab.setVisibility(View.GONE);
        edtSearchArticle.setVisibility(View.GONE);
        edtSearchCommande.setVisibility(View.VISIBLE);
    }

    public void openDrawerEtab(View v) {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    // Localisation

    public void onClickLocation(View v) {
        if (layoutLoading.getVisibility() == View.GONE) {
            layoutLoading.setVisibility(View.VISIBLE);
            googleLocation = new GoogleLocation(context, instTabs, 0);
        }
    }

    public void goodReturnLocation() {
        edtSearchVille.setText(googleLocation.getCity());
        layoutLoading.setVisibility(View.GONE);
    }

    public void badReturnLocation(String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
        layoutLoading.setVisibility(View.GONE);
    }

    // Recherche d'un établissement
    public void onClickSearch(View v) {
        switch (currentTab) {
            case 0 : Etablissements.getInstance().searchInLv(); break;
            case 1 : Panier.getInstance().searchInLv(); break;
            case 2 : Commandes.getInstance().searchInLv(); break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    /** FIN DRAWER **/

    /** BOUTONS **/

    public void openPlacePicker(View v) {
        Intent intent = new Intent(this, MapPlacePicker.class);
        intent.putExtra(getString(R.string.useType), getString(R.string.useTypeCons));
        startActivity(intent);
    }

    public void emptyLvEtab(View v) {
        Etablissements.getInstance().emptyLvEtab();
    }

    /** FIN BOUTONS **/

    /** MENU **/

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!compte.equals(getString(R.string.varGerant)))
            menu.getItem(1).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        checkPref();

        // Prend l'id du menu sélectionné
        switch (item.getItemId()) {
            case R.string.menuIdCompte :
                if (pseudo.equals(""))
                    startActivity(new Intent(this, Login.class));
                else
                    startActivity(new Intent(this, Compte.class));
                return true;
            case R.string.menuIdEtab :
                startActivity(new Intent(this, EtablissementManager.class));
                return true;
            case R.string.menuIdSupp :
                startActivity(new Intent(this, Support.class));
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    /** FIN MENU **/

    /** ETAT DE L'ACTIVITY **/

    @Override
    protected void onResume() {
        checkPref();
        super.onResume();
    }

    public static Tabs getInstance(){
        return instTabs;
    }

    /** FIN ETAT DE L'ACTIVITY **/
}