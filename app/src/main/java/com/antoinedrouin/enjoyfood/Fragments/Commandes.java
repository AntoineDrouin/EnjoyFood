package com.antoinedrouin.enjoyfood.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antoinedrouin.enjoyfood.R;

public class Commandes extends Fragment {

    static Commandes instCommandes;

    public static Commandes newInstance() {
        Commandes fragment = new Commandes();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instCommandes = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_commandes, container, false);
        return view;
    }

    public void searchInLv() {

    }

    public static Commandes getInstance() {
        return instCommandes;
    }
}
