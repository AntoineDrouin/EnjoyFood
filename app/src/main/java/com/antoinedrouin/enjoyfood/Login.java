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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class Login extends AppCompatActivity {

    Context context;
    static Login instLogin;

    EditText edtPseudo, edtMdp;
    Button btnConnexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        FacebookSdk.sdkInitialize(context);
        setContentView(R.layout.activity_login);

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

        // Bouton Facebook

        CallbackManager callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id. login_button);
        loginButton.setReadPermissions("user_friends");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(context, "404", Toast.LENGTH_SHORT).show();
            }
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
        checkIdentifiant.user = pseudo;
        checkIdentifiant.mdp = mdp;
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

    public void putInPrefLogin(String id, String pseudo, String mdp, String compte,
                          String nom, String prenom, String ville,
                          String cp, String tel, String adresse) {

        // Mettre dans les préférences
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(getString(R.string.prefId), id);
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

        edit.apply();
        Toast.makeText(context, getString(R.string.connectionSuccess), Toast.LENGTH_SHORT).show();

        Tabs.getInstance().recreate(); // On recrée l'instance pour prendre en compte l'utilisateur nouvellement connecté
        startActivity(new Intent(context, Compte.class));
        finish();
    }

    public static Login getInstance(){
        return instLogin;
    }

}

