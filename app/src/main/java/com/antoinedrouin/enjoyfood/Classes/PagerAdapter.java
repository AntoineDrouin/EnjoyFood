package com.antoinedrouin.enjoyfood.Classes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.antoinedrouin.enjoyfood.Fragments.Commandes;
import com.antoinedrouin.enjoyfood.Fragments.Coordonnees;
import com.antoinedrouin.enjoyfood.Fragments.Etablissements;
import com.antoinedrouin.enjoyfood.Fragments.Menu;
import com.antoinedrouin.enjoyfood.Fragments.Notes;
import com.antoinedrouin.enjoyfood.Fragments.Panier;
import com.antoinedrouin.enjoyfood.R;

/**
 * Created by cdsm04 on 24/02/2016.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private final int PAGE_COUNT = 3;
    private int tab;

    public PagerAdapter(FragmentManager fm, Context pContext, int pTab ){
        super(fm);
        context = pContext;
        tab = pTab;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    // Retourne l'instance du Fragment par rapport à l'index passé en paramètre
    @Override
    public Fragment getItem(int position) {
        switch (tab) {
            case 0 :
                switch (position) {
                    case 0 : return Etablissements.newInstance();
                    case 1 : return Panier.newInstance();
                    case 2 : return Commandes.newInstance();
                }

            case 1 :
                switch (position) {
                    case 0 : return Coordonnees.newInstance();
                    case 1 : return Menu.newInstance();
                    case 2 : return Notes.newInstance();
                }
        }
        return null;
    }

    // Retourne l'apparence de l'onglet par rapport à l'index passé en paramètre
    @Override
    public CharSequence getPageTitle(int position) {
        int[][] imageId = new int[][]{
                {R.drawable.ic_eta, R.drawable.ic_pan, R.drawable.ic_com,} ,
                {R.drawable.ic_coo, R.drawable.ic_menu, R.drawable.ic_not}
        };

        Drawable image = ContextCompat.getDrawable(context, imageId[tab][position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }
}
