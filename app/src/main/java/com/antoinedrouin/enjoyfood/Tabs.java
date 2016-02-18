package com.antoinedrouin.enjoyfood;

import android.Manifest;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Tabs extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Context context;
    static Tabs instTabs;
    SharedPreferences pref;

    String pseudo, compte;
    int currentTab, tabCount, tab;

    DrawerLayout mDrawerLayout;
    TabHost tabHost;
    TabWidget tw;
    TextView lblTitreTab;
    EditText edtSearchEtab, edtSearchVille, edtSearchSpecialite, edtSearchArticle, edtSearchCommande;
    LinearLayout layoutVille;

    GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        context = getApplicationContext();
        instTabs = this;
        currentTab = 0;

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        tabHost = (TabHost) findViewById(R.id.tabHost);
        tw = (TabWidget) tabHost.findViewById(android.R.id.tabs);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        layoutVille = (LinearLayout) findViewById(R.id.layoutVille);
        lblTitreTab = (TextView) findViewById(R.id.lblTitreTab);
        edtSearchEtab = (EditText) findViewById(R.id.edtSearchEtab);
        edtSearchVille = (EditText) findViewById(R.id.edtSearchVille);
        edtSearchArticle = (EditText) findViewById(R.id.edtSearchArticle);
        edtSearchCommande = (EditText) findViewById(R.id.edtSearchCommande);
        edtSearchSpecialite = (EditText) findViewById(R.id.edtSearchSpecialite);

        LocalActivityManager mlam = new LocalActivityManager(this, false);
        mlam.dispatchCreate(savedInstanceState);
        tabHost.setup(mlam);

        // Charge les onglets
        CustomTab customTab = new CustomTab(context, tabHost, tw, getString(R.string.varTabs));
        tabHost = customTab.load();

        checkPref();

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

        // Change le contenus du drawer on fonction de l'onglet chargé
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                currentTab = tabHost.getCurrentTab();
                switch (currentTab) {
                    case 0: loadEtabTabDrawer(); break;
                    case 1: loadPanTabDrawer(); break;
                    case 2: loadComTabDrawer(); break;
                }
            }
        });

        // Crée une instance de GoogleApi
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    // Charge le drawer

    private void loadEtabTabDrawer() {
        lblTitreTab.setText(getString(R.string.lblSearchEtab));
        layoutVille.setVisibility(View.VISIBLE);
        edtSearchEtab.setVisibility(View.VISIBLE);
        edtSearchSpecialite.setVisibility(View.VISIBLE);
        edtSearchArticle.setVisibility(View.GONE);
        edtSearchCommande.setVisibility(View.GONE);
    }

    private void loadPanTabDrawer() {
        lblTitreTab.setText(getString(R.string.lblSearchPanier));
        layoutVille.setVisibility(View.GONE);
        edtSearchEtab.setVisibility(View.GONE);
        edtSearchSpecialite.setVisibility(View.GONE);
        edtSearchArticle.setVisibility(View.VISIBLE);
        edtSearchCommande.setVisibility(View.GONE);
    }

    private void loadComTabDrawer() {
        lblTitreTab.setText(getString(R.string.lblSearchCom));
        layoutVille.setVisibility(View.GONE);
        edtSearchEtab.setVisibility(View.GONE);
        edtSearchSpecialite.setVisibility(View.GONE);
        edtSearchArticle.setVisibility(View.GONE);
        edtSearchCommande.setVisibility(View.VISIBLE);
    }

    // Localisation

    public void onClickLocation(View v) {
        mGoogleApiClient.disconnect();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        double lat, lon;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            try {
                lat = mLastLocation.getLatitude();
                lon = mLastLocation.getLongitude();

                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(lat, lon, 1);

                if (addresses.size() > 0)
                    edtSearchVille.setText(addresses.get(0).getLocality());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(context, getString(R.string.geolocationFailed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(context, getString(R.string.geolocationFailed), Toast.LENGTH_SHORT).show();
    }

        // Affiche une notification
//    public void notif() {
//        Intent intent = new Intent(context, Login.class);
//        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
//
//        Notification notification = new NotificationCompat.Builder(context)
//                .setTicker(getString(R.string.tabEtab))
//                .setSmallIcon(R.drawable.ic_menu)
//                .setContentTitle(getString(R.string.tabEtab))
//                .setContentText(getString(R.string.tabCommandes))
//                .setContentIntent(pIntent)
//                .setAutoCancel(true)
//                .build();
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notification);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPref();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    // MENU

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
                //
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
        pseudo = pref.getString(getString(R.string.prefPseudo), "");
        compte = pref.getString(getString(R.string.prefCompte), "");
    }

    public void onClickSearch(View v) {
        switch (currentTab) {
            case 0 : break;
            case 1 : break;
            case 2 : break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    public static Tabs getInstance(){
        return instTabs;
    }

}