package com.antoinedrouin.enjoyfood;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Notes extends Fragment {

    static Notes instNotes;

    public static Notes newInstance() {
        Notes fragment = new Notes();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instNotes = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        return view;
    }

    public static final Notes getInstance() {
        return instNotes;
    }

}
