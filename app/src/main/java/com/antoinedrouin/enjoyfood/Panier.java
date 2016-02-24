package com.antoinedrouin.enjoyfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Panier extends Fragment {

    static Panier instPanier;

    public static Panier newInstance() {
        Panier fragment = new Panier();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instPanier = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_panier, container, false);
        return view;
    }

    public void onClickOpenMaps(View v) {
        startActivity(new Intent(getContext(), MapsActivity.class));
    }

    public void searchInLv() {

    }

    public static Panier getInstance() {
        return instPanier;
    }
}
