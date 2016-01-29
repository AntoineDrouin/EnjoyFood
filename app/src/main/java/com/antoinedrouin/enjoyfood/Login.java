package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    Context context;
    static Login instLogin;

    EditText edtPseudo, edtMdp;
    Button btnConnexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();
        instLogin = this;

        edtPseudo = (EditText) findViewById(R.id.edtPseudoLogin);
        edtMdp = (EditText) findViewById(R.id.edtMdp);
        btnConnexion = (Button) findViewById(R.id.btnConnexion);

        // Test sur les champs

        edtPseudo.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                checkFields();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        edtMdp.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                checkFields();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    public void onClickLogin(View v) {
        String script, methode, pseudo, mdp;

        script = getString(R.string.checkIdentifiants);
        methode = getString(R.string.read);
        pseudo = edtPseudo.getText().toString();
        mdp = edtMdp.getText().toString();

        // Test si les identifiants correspondent à un compte
        ServerSide checkIdentifiant = new ServerSide(context);
        checkIdentifiant.execute(script, methode, pseudo, mdp);
    }

    public void onClickRegister(View v) {
        startActivity(new Intent(this, Register.class));
    }

    private void checkFields() {
        if (!edtPseudo.getText().toString().equals("") && !edtMdp.getText().toString().equals(""))
            btnConnexion.setVisibility(View.VISIBLE);
        else
            btnConnexion.setVisibility(View.INVISIBLE);
    }

    public void putInPrefLogin(String pseudo, String mdp, String compte,
                          String nom, String prenom, String ville,
                          String cp, String tel, String adresse) {

        // Mettre dans pref
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(getString(R.string.prefPseudo), pseudo);
        edit.putString(getString(R.string.prefMdp), mdp);
        edit.putString(getString(R.string.prefCompte), compte);
        edit.putString(getString(R.string.prefNom), nom);
        edit.putString(getString(R.string.prefPrenom), prenom);

        if (compte.equals(getString(R.string.varClient))) {
            edit.putString(getString(R.string.prefVille), ville);
            edit.putString(getString(R.string.prefCp), cp);
            edit.putString(getString(R.string.prefTel), tel);
            edit.putString(getString(R.string.prefAdresse), adresse);
        }
//        else if (compte.equals(context.getString(R.string.varGerant))) {
//
//        }

        edit.apply();

        Toast.makeText(context, getString(R.string.connectionSuccess), Toast.LENGTH_SHORT).show();

        startActivity(new Intent(context, Compte.class));
        finish();
    }

    public static Login getInstance(){
        return instLogin;
    }

}

