package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {

    Context context;
    SharedPreferences pref;
    SharedPreferences.Editor edit;

    Spinner spinCompte;
    EditText edtPseudo;

    String script, methode, user, pseudo, mdp, compte, mdp2, nom, prenom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        edit = pref.edit();

        context = getApplicationContext();

        spinCompte = (Spinner) findViewById(R.id.spinCompte);
        edtPseudo = (EditText) findViewById(R.id.edtPseudo);

        List comptes = new ArrayList<>();
        comptes.add(getString(R.string.varClient));
        comptes.add(getString(R.string.varGerant));

        spinCompte.setAdapter(new ArrayAdapter<>(this, android.R.layout.select_dialog_item, comptes));
    }


    public void onClickCreation(View v) {
        compte = spinCompte.getSelectedItem().toString();
        pseudo = edtPseudo.getText().toString();
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
            RegisterServerSide checkUtilisateur = new RegisterServerSide();
            checkUtilisateur.execute(script, methode, edtPseudo.getText().toString());
        }
    }



    // COTE SERVEUR DE LA CLASSE



    private class RegisterServerSide extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {}


        @Override
        protected String doInBackground(String... params) {
            // param[0] = script à utiliser
            // param[1] = indique lecture ou écriture

            script = params[0];
            methode = params[1];
            String json;

            // Préparation de la connexion

            HttpURLConnection httpURLConnection = null;
            try {
                // Donne l'adresse du script php
                URL url = new URL(script);
                httpURLConnection = (HttpURLConnection) url.openConnection();
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
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Algorithme d'écriture ou de lecture

            // Ecriture
            if (methode.equals(getString(R.string.write))) {
                try {
                    InputStream is = httpURLConnection.getInputStream();
                    is.close();

                    return script;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Lecture
            else if (methode.equals(getString(R.string.read))) {
                try {
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

            if (result.equals(getString(R.string.insertUtilisateur))) {
                // Mettre dans pref
                edit.putString(getString(R.string.prefUser), pseudo);
                edit.putString(getString(R.string.prefMdp), mdp);
                edit.putString(getString(R.string.prefCompte), compte);
                edit.putString(getString(R.string.prefNom), nom);
                edit.putString(getString(R.string.prefPrenom), prenom);
                edit.apply();

                Toast.makeText(context, getString(R.string.insertUtilisateurSuccess), Toast.LENGTH_SHORT).show();
                Login.getInstance().finish();
                finish();
                startActivity(new Intent(context, Compte.class));
            }
            else if (result.equals(getString(R.string.connectionError))) {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }
            else {
                JSONObject jsonObject, jso;
                JSONArray jsonArray;

                try {
                    jsonObject = new JSONObject(result);
                    jsonArray = jsonObject.getJSONArray("response");

                    // On utilise le json différement pour chaque script
                    if (script.equals(getString(R.string.checkUtilisateur))) {
                        user = "";
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jso = jsonArray.getJSONObject(i);
                            user = jso.getString("pseudo");
                        }

                        // S'il ne trouve aucun pseudo similaire, on lance l'insertion
                        if (user.equals("")) {
                            script = getString(R.string.insertUtilisateur);
                            methode = getString(R.string.write);
                            RegisterServerSide insertUtilisateur = new RegisterServerSide();
                            insertUtilisateur.execute(script, methode, compte, pseudo, mdp, nom, prenom);
                        }
                        else {
                            Toast.makeText(context, getString(R.string.insertUtilisateurFail), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private String encodeData(String lien, String... params) {
            String data = null;

            try {
                if (lien.equals(getString(R.string.insertUtilisateur))) {
                    data = URLEncoder.encode("compte", "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8") + "&" +
                            URLEncoder.encode("pseudo", "UTF-8") + "=" + URLEncoder.encode(params[3], "utf-8") + "&" +
                            URLEncoder.encode("mdp", "utf-8") + "=" + URLEncoder.encode(params[4], "utf-8") + "&" +
                            URLEncoder.encode("nom", "utf-8") + "=" + URLEncoder.encode(params[5], "utf-8") + "&" +
                            URLEncoder.encode("prenom", "utf-8") + "=" + URLEncoder.encode(params[6], "utf-8");
                }
                else if (lien.equals(getString(R.string.checkUtilisateur))) {
                    data = URLEncoder.encode("pseudo", "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return data;
        }

    }

}
