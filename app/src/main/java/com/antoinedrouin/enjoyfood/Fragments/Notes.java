package com.antoinedrouin.enjoyfood.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.antoinedrouin.enjoyfood.R;

public class Notes extends Fragment {

    private Context context;
    private static Notes instNotes;
    private SharedPreferences pref;

    private Button btnAddNote;
    private LinearLayout layoutNote;

    public static Notes newInstance() {
        return new Notes();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instNotes = this;
        context = getContext();
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        btnAddNote = (Button) view.findViewById(R.id.btnAddNote);
        layoutNote = (LinearLayout) view.findViewById(R.id.layoutNote);

        if (!pref.getString(getString(R.string.prefCompte), "").equals(getString(R.string.varClient))) {
            btnAddNote.setVisibility(View.GONE);
        }

        return view;
    }

    public void addNote() {
        if (layoutNote.getVisibility() == View.GONE) {
            layoutNote.setVisibility(View.VISIBLE);
        } else {
            layoutNote.setVisibility(View.GONE);
        }
    }

    public void sendNote() {
        // Envois de la note

        addNote();
    }

    public static final Notes getInstance() {
        return instNotes;
    }

}
