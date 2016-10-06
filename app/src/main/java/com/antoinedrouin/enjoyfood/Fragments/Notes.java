package com.antoinedrouin.enjoyfood.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.antoinedrouin.enjoyfood.Activities.Etablissement;
import com.antoinedrouin.enjoyfood.Classes.ServerSide;
import com.antoinedrouin.enjoyfood.R;

import java.util.ArrayList;

public class Notes extends Fragment {

    private Context context;
    private static Notes instNotes;
    private SharedPreferences pref;

    private Button btnAddNote;
    private LinearLayout layoutNote;
    private TextView txtNote;
    private EditText edtRemarque;
    private SeekBar skNote;
    private ListView lvNotes;

    private String idEt, idUt;
    private boolean charged = false;
    private ArrayList<String> notes;

    public static Notes newInstance() {
        return new Notes();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instNotes = this;
        context = getContext();

        pref = PreferenceManager.getDefaultSharedPreferences(context);
        idUt = pref.getString(getString(R.string.prefId), "");

        Bundle extras = Etablissement.getInstance().getIntent().getExtras();
        idEt = extras.getString(getString(R.string.extraEtabId), "");

        searchNotes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        btnAddNote = (Button) view.findViewById(R.id.btnAddNote);
        layoutNote = (LinearLayout) view.findViewById(R.id.layoutNote);
        txtNote = (TextView) view.findViewById(R.id.txtNote);
        edtRemarque = (EditText) view.findViewById(R.id.edtRemarqueNo);
        skNote = (SeekBar) view.findViewById(R.id.skNote);
        lvNotes = (ListView) view.findViewById(R.id.lvNotes);

        if (!pref.getString(getString(R.string.prefCompte), "").equals(getString(R.string.varClient))) {
            btnAddNote.setVisibility(View.GONE);
        }

        txtNote.setText(Integer.toString(skNote.getProgress()) + getString(R.string.varNoteMax));

        skNote.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtNote.setText(Integer.toString(progress) + getString(R.string.varNoteMax));
            }
        });

        // Recharge les composants parce que la vue est détruite quand on va au premier onglet
        if (charged)
            setCompo();

        return view;
    }

    private void searchNotes() {
        // Requête pour trouver les données de l'établissement
        ServerSide getNotes = new ServerSide(context);
        getNotes.execute(getString(R.string.getNotes), getString(R.string.read), idEt);
    }

    public void addNote() {
        if (layoutNote.getVisibility() == View.GONE)
            layoutNote.setVisibility(View.VISIBLE);
        else
            layoutNote.setVisibility(View.GONE);
    }

    public void sendNote() {
        addNote();

        // Envois de la note
        ServerSide insertNote = new ServerSide(context);
        insertNote.execute(getString(R.string.insertNote), getString(R.string.write), idUt, idEt, txtNote.getText().toString(), edtRemarque.getText().toString());
    }

    public void noteWellSend() {
        searchNotes();
        Toast.makeText(context, getString(R.string.noteSend),Toast.LENGTH_SHORT).show();
    }

    public void displayNotes(ArrayList<String> n) {
        charged = true;
        notes = new ArrayList<>();
        notes = n;
        setCompo();
    }

    private void setCompo() {
        ArrayAdapter<String> arrayNo = new ArrayAdapter<>(
                context,
                R.layout.listitem,
                notes);

        lvNotes.setAdapter(arrayNo);
    }

    public static final Notes getInstance() {
        return instNotes;
    }

}
