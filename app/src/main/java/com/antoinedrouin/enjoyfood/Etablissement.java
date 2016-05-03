package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Etablissement extends AppCompatActivity {

    Context context;
    static Etablissement instEtab;

    TabLayout tabLayout;
    ViewPager viewPager;

    int currentTab = 0;
    int[] titleId = new int[] {R.string.tabCoord, R.string.tabMenu, R.string.tabNotes};
    int[] imageId = new int[] {R.drawable.ic_coo, R.drawable.ic_menu, R.drawable.ic_not};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etablissement);

        context = getApplicationContext();
        instEtab = this;

        tabLayout = (TabLayout) findViewById(R.id.tabLayoutEtab);
        viewPager = (ViewPager) findViewById(R.id.viewPagerEtab);

       // Remplis le viewPager
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), context, 1));

        // Donne le viewPage au tabLayout
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(currentTab).setText(titleId[currentTab]);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();

                tab.setText(titleId[currentTab]);
                tab.setIcon(null);

                // Pour être sûr d'être synchro
                if (currentTab != viewPager.getCurrentItem())
                    viewPager.setCurrentItem(currentTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(ContextCompat.getDrawable(context, imageId[currentTab]));
                tab.setText(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void onClickCall(View v) {
        Coordonnees.getInstance().call();
    }

    public void onClickItinerary(View v) {
        Coordonnees.getInstance().openMaps();
    }

    public static Etablissement getInstance(){
        return instEtab;
    }
}
