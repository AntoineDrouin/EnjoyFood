package com.antoinedrouin.enjoyfood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Compte extends AppCompatActivity {

    Context context;
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    String pseudo, script;

    LinearLayout layoutMdp;
    EditText edtOldMdp, edtNewMdp1, edtNewMdp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compte);

        context = getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        pseudo = pref.getString(getString(R.string.prefUser), "");

        layoutMdp = (LinearLayout) findViewById(R.id.layoutMdp);
        edtOldMdp = (EditText) findViewById(R.id.edtOldMdp);
        edtNewMdp1 = (EditText) findViewById(R.id.edtNewMdp1);
        edtNewMdp2 = (EditText) findViewById(R.id.edtNewMdp2);
    }

    public void onClickDeco (View v) {
        deco();
        Toast.makeText(context, getString(R.string.disconnectionSuccess), Toast.LENGTH_SHORT).show();
    }

    public void onClickMdpChange(View v) {
        LinearLayout layoutMdp = (LinearLayout) findViewById(R.id.layoutMdp);

        if (layoutMdp.getVisibility() == View.VISIBLE)
            layoutMdp.setVisibility(View.GONE);
        else
            layoutMdp.setVisibility(View.VISIBLE);
    }

    public void onClickMdpChangeValider(View v) {
        String oldMdp, newMdp1, newMdp2, error;

        error = "";
        oldMdp = edtOldMdp.getText().toString();
        newMdp1 = edtNewMdp1.getText().toString();
        newMdp2 = edtNewMdp2.getText().toString();

        if (oldMdp.length() < 3 || newMdp1.length() < 3 || newMdp2.length() < 3 )
            error = getString(R.string.errorMdpLength);
        else if (!newMdp1.equals(newMdp2))
            error = getString(R.string.errorMdpNotSame);
        else if (oldMdp.equals(newMdp1))
            error = getString(R.string.errorMdpSame);

        if (!error.equals(""))
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
        else {
            if (!pref.getString(getString(R.string.prefMdp), "").equals(oldMdp))
                Toast.makeText(context, getString(R.string.errorOldMdp), Toast.LENGTH_SHORT).show();
            else {
                CompteServerSide changePassword = new CompteServerSide();
                changePassword.execute(getString(R.string.changePassword), pseudo, newMdp1);
            }
        }
    }

    public void onClickSupprCompte(View v) {
        new AlertDialog.Builder(this)
            .setMessage(getString(R.string.eraseCompteMessage))
            .setCancelable(false)
            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(context, getString(R.string.eraseCompteFail), Toast.LENGTH_SHORT).show();
                }
            })
            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    CompteServerSide eraseCompte = new CompteServerSide();
                    eraseCompte.execute(getString(R.string.eraseCompte), pseudo);
                }
            })
            .show();
    }

    private void deco() {
        edit = pref.edit();
        edit.putString(getString(R.string.prefUser), "");
        edit.putString(getString(R.string.prefMdp), "");
        edit.apply();
        finish();
    }



    // COTE SERVEUR DE LA CLASSE



    private class CompteServerSide extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {}


        @Override
        protected String doInBackground(String... params) {
            // param[0] = script à utiliser
            // param[1] = indique lecture ou écriture

            script = params[0];

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

                InputStream is = httpURLConnection.getInputStream();
                is.close();

                if (script.equals(getString(R.string.changePassword)))
                    return getString(R.string.changeMdpSuccess);
                else if (script.equals(getString(R.string.eraseCompte)))
                    return getString(R.string.eraseCompteSuccess);

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
            if (result.equals(getString(R.string.eraseCompteSuccess))) {
                deco();
                Toast.makeText(context, getString(R.string.eraseCompteSuccess), Toast.LENGTH_SHORT).show();
            }
            else if (result.equals(getString(R.string.changeMdpSuccess))) {
                layoutMdp.setVisibility(View.GONE);
                edtOldMdp.setText("");
                edtNewMdp1.setText("");
                edtNewMdp2.setText("");
                Toast.makeText(context, getString(R.string.changeMdpSuccess), Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }

        private String encodeData(String lien, String... params) {
            String data = null;

            try {
                if (lien.equals(getString(R.string.changePassword)))
                    data = URLEncoder.encode("pseudo", "utf-8") + "=" + URLEncoder.encode(params[1], "utf-8") + "&" +
                            URLEncoder.encode("mdp", "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8");
                else if (lien.equals(getString(R.string.eraseCompte)))
                    data = URLEncoder.encode("pseudo", "utf-8") + "=" + URLEncoder.encode(params[1], "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return data;
        }

    }
}
