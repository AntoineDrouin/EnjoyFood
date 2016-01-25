package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Login extends AppCompatActivity {

    Context context;
    static Login login;

    EditText edtPseudo, edtMdp;
    Button btnConnexion;

    String user, script;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();
        login = this;

        script = getString(R.string.checkIdentifiants);

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
        String pseudo = edtPseudo.getText().toString();
        String mdp = edtMdp.getText().toString();

        // Test si les identifiants correspondent à un compte
        LoginServerSide checkUtilisateur = new LoginServerSide();
        checkUtilisateur.execute(script, pseudo, mdp);
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

    public static Login getInstance(){
        return login;
    }



    // COTE SERVEUR DE LA CLASSE



    private class LoginServerSide extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {}


        @Override
        protected String doInBackground(String... params) {
            // param[0] = script à utiliser
            // param[1] = indique lecture ou écriture

            script = params[0];
            String json;

            // Préparation de la connexion

            try {
                // Donne l'adresse du script php
                URL url = new URL(script);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter buffW = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));

                // Encodage des données pour méthode POST
                String data = encodeData(script, params);
                buffW.write(data);

                buffW.flush();
                buffW.close();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                BufferedReader buffR = new BufferedReader(new InputStreamReader(is));
                StringBuilder stringB = new StringBuilder();

                while ((json = buffR.readLine()) != null) {
                    stringB.append(json);
                }

                buffR.close();
                is.close();
                httpURLConnection.disconnect();

                return stringB.toString().trim();

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Return par défaut
            return getString(R.string.connectionError);
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


        @Override
        protected void onPostExecute(String result) {
            // Traitements des retours

            // S'il n'y a pas d'erreur de connexion
            if (result.equals(getString(R.string.connectionError))) {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }
            else {
                JSONObject jsonObject, jso;
                JSONArray jsonArray;
                String id, compte, nom, prenom, ville, cp, tel, adresse;

                try {
                    jsonObject = new JSONObject(result);
                    jsonArray = jsonObject.getJSONArray("response");
                    user = id = compte = nom = prenom = ville = cp = tel = adresse = "";

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jso = jsonArray.getJSONObject(i);
                        id = jso.getString("id");
                        user = jso.getString("pseudo");
                        nom = jso.getString("nom");
                        prenom = jso.getString("prenom");
                        compte = jso.getString("compte");

                        if (compte.equals(getString(R.string.varClient))) {
                            ville = jso.getString("ville");
                            cp = jso.getString("cp");
                            tel = jso.getString("tel");
                            adresse = jso.getString("adresse");
                        }
                        else if (compte.equals(getString(R.string.varGerant))) {

                        }
                    }

                    // Si un utilisateur a été trouvé
                    if (!user.equals("")) {
                        Toast.makeText(context, getString(R.string.connectionSuccess), Toast.LENGTH_SHORT).show();

                        // Mettre dans pref
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                        Editor edit = pref.edit();
                        edit.putString(getString(R.string.prefId), id);
                        edit.putString(getString(R.string.prefUser), user);
                        edit.putString(getString(R.string.prefMdp), edtMdp.getText().toString());
                        edit.putString(getString(R.string.prefCompte), compte);
                        edit.putString(getString(R.string.prefNom), nom);
                        edit.putString(getString(R.string.prefPrenom), prenom);

                        if (compte.equals(getString(R.string.varClient))) {
                            edit.putString(getString(R.string.prefVille), ville);
                            edit.putString(getString(R.string.prefCp), cp);
                            edit.putString(getString(R.string.prefTel), tel);
                            edit.putString(getString(R.string.prefAdresse), adresse);
                        }
                        else if (compte.equals(getString(R.string.varGerant))) {

                        }

                        edit.apply();

                        finish();
                        startActivity(new Intent(context, Compte.class));
                    } else {
                        Toast.makeText(context, getString(R.string.connectionFail), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String encodeData(String lien, String... params) {
        String data = null;

        try {
            if (lien.equals(getString(R.string.checkIdentifiants))) {
                data = URLEncoder.encode("pseudo", "utf-8") + "=" + URLEncoder.encode(params[1], "utf-8") + "&" +
                    URLEncoder.encode("mdp", "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return data;
    }

}

