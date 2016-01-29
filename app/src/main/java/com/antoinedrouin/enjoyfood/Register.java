package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {

    Context context;
    static Register instRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = getApplicationContext();
        instRegister = this;

        List comptes = new ArrayList<>();
        comptes.add(getString(R.string.varClient));
        comptes.add(getString(R.string.varGerant));

        ((Spinner) findViewById(R.id.spinCompte)).setAdapter(new ArrayAdapter<>(this, android.R.layout.select_dialog_item, comptes));
    }


    public void onClickCreation(View v) {
        String script, methode, pseudo, mdp, mdp2, nom, prenom;

        pseudo = ((EditText) findViewById(R.id.edtPseudo)).getText().toString();
        mdp = ((EditText) findViewById(R.id.edtMdp1)).getText().toString();
        mdp2 = ((EditText) findViewById(R.id.edtMdp2)).getText().toString();
        nom = ((EditText) findViewById(R.id.edtNom)).getText().toString();
        prenom = ((EditText) findViewById(R.id.edtPrenom)).getText().toString();

        // Chaine qui va retourner toutes les erreurs de saisies
        String error = "";

        // Test des champs
        if (pseudo.length() < 3)
            error = getString(R.string.errorPseudoLength);
        else if (mdp.length() < 3 || mdp2.length() < 3)
            error = getString(R.string.errorMdpLength);
        else if (!mdp.equals(mdp2))
            error = getString(R.string.errorMdpNotSame);
        else if (nom.length() == 0)
            error = getString(R.string.errorNom);
        else if (prenom.length() == 0)
            error = getString(R.string.errorPrenom);

        if (!error.equals(""))
            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
        else {
            // Vérifie si le pseudo est disponible
            script = getString(R.string.checkUtilisateur);
            methode = getString(R.string.read);

            ServerSide checkUtilisateur = new ServerSide(context);
            checkUtilisateur.execute(script, methode, pseudo);
        }
    }

    public void putInPrefRegister(String pseudo, String mdp, String compte,
                                   String nom, String prenom) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = pref.edit();

        edit.putString(getString(R.string.prefPseudo), pseudo);
        edit.putString(getString(R.string.prefMdp), mdp);
        edit.putString(getString(R.string.prefCompte), compte);
        edit.putString(getString(R.string.prefNom), nom);
        edit.putString(getString(R.string.prefPrenom), prenom);
        edit.apply();

        Toast.makeText(context, getString(R.string.insertUtilisateurSuccess), Toast.LENGTH_SHORT).show();

        Login.getInstance().finish();
        startActivity(new Intent(context, Compte.class));
        finish();
    }

    public static Register getInstance(){
        return instRegister;
    }

}
