package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Etablissement extends AppCompatActivity {

    Context context;
    static Etablissement instEtab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etablissement);

        context = getApplicationContext();
        instEtab = this;

        Bundle extras = getIntent().getExtras();
        String nomEtab =  extras.getString(getString(R.string.extraEtabName), getString(R.string.tabEtab));
        ((TextView) findViewById(R.id.lblNomEtab)).setText(nomEtab);

       // Remplis le viewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerEtab);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), context, 1));

        // Donne le viewPage au tabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayoutEtab);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static Etablissement getInstance(){
        return instEtab;
    }

}
