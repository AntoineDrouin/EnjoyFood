package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    Context context;
    static Register instRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = getApplicationContext();
        instRegister = this;
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

    public void putInPrefRegister() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = pref.edit();

        edit.putString(getString(R.string.prefPseudo), ((EditText) findViewById(R.id.edtPseudo)).getText().toString());
        edit.putString(getString(R.string.prefMdp), ((EditText) Register.getInstance().findViewById(R.id.edtMdp1)).getText().toString());
        edit.putString(getString(R.string.prefCompte), ((Spinner) findViewById(R.id.spinCompte)).getSelectedItem().toString());
        edit.putString(getString(R.string.prefNom), ((EditText) Register.getInstance().findViewById(R.id.edtNom)).getText().toString());
        edit.putString(getString(R.string.prefPrenom), ((EditText) Register.getInstance().findViewById(R.id.edtPrenom)).getText().toString());
        edit.apply();

        Toast.makeText(context, getString(R.string.insertUtilisateurSuccess), Toast.LENGTH_SHORT).show();

        Login.getInstance().finish();
        Tabs.getInstance().recreate();
        startActivity(new Intent(context, Compte.class));
        finish();
    }

    public static Register getInstance(){
        return instRegister;
    }

}
