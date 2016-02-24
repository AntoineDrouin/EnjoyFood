package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

/**
 * Created by cdsm04 on 24/02/2016.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private Context context;
    final int PAGE_COUNT = 3;
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

    @Override
    public CharSequence getPageTitle(int position) {
        int[][] imageId = new int[][]{
                {R.drawable.ic_eta, R.drawable.ic_pan, R.drawable.ic_com,} ,
                {R.drawable.ic_coo, R.drawable.ic_menu, R.drawable.ic_not}
        };

        int[][] titleId = new int[][] {
                {R.string.tabEtab, R.string.tabPanier, R.string.tabCommandes,} ,
                {R.string.tabCoord, R.string.tabMenu, R.string.tabNotes}
        };

        Drawable image = ContextCompat.getDrawable(context, imageId[tab][position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(context.getString(titleId[tab][position]));
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);

        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }
}
