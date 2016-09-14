package com.antoinedrouin.enjoyfood.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.antoinedrouin.enjoyfood.Classes.ServerSide;
import com.antoinedrouin.enjoyfood.Classes.Utilisateur;
import com.antoinedrouin.enjoyfood.Classes.Utilitaire;
import com.antoinedrouin.enjoyfood.R;

public class Login extends AppCompatActivity {

    Context context;
    static Login instLogin;

    EditText edtPseudo, edtMdp;
    Button btnConnexion;
    RelativeLayout layoutLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_login);

        instLogin = this;

        edtPseudo = (EditText) findViewById(R.id.edtPseudoLogin);
        edtMdp = (EditText) findViewById(R.id.edtMdp);
        btnConnexion = (Button) findViewById(R.id.btnConnexion);
        layoutLoading = (RelativeLayout) findViewById(R.id.loadingPanel);

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
        try {
            layoutLoading.setVisibility(View.VISIBLE);
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
        finally {
            layoutLoading.setVisibility(View.GONE);
        }
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

    public void putInPrefLogin(Utilisateur utilisateur) {
        // Mettre dans les préférences
        Utilitaire prefHelp = new Utilitaire(context);
        prefHelp.putUtilisateur(utilisateur);

        Toast.makeText(context, getString(R.string.connectionSuccess), Toast.LENGTH_SHORT).show();

        Tabs.getInstance().recreate(); // On recrée l'instance pour prendre en compte l'utilisateur nouvellement connecté
        startActivity(new Intent(context, Compte.class));
        finish();
    }

    public void noAccount() {
        Toast.makeText(context, context.getString(R.string.connectionFail), Toast.LENGTH_SHORT).show();
    }

    public static Login getInstance(){
        return instLogin;
    }

}

