package com.antoinedrouin.enjoyfood;

import android.content.Context;
import android.os.AsyncTask;
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

/**
 * Created by cdsm04 on 26/01/2016.
 */

public class ServerSide extends AsyncTask<String, Void, String> {

    Context context;

    String script, methode, pseudo, mdp, nom, prenom, compte, user, ville, cp, tel, adresse;

    public ServerSide (Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {}


    @Override
    protected String doInBackground(String... params) {
        script = params[0]; // script à utiliser
        methode = params[1]; // indique lecture ou écriture
        String json;

        /** 1. PREPARATION DE LA CONNEXION */

        HttpURLConnection httpURLConnection = null;
        try {
            // Donne l'adresse du script php
            URL url = new URL(script);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream os = httpURLConnection.getOutputStream();
            BufferedWriter buffW = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));

            // Encodage des données
            String data = encodeData(script, params);
            buffW.write(data);

            buffW.flush();
            buffW.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /** 3. ALGORITHME DE LECTURE OU D'ECRITURE */

        // Ecriture
        if (methode.equals(context.getString(R.string.write))) {
            try {
                InputStream is = httpURLConnection.getInputStream();
                is.close();

                return script;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Lecture
        else if (methode.equals(context.getString(R.string.read))) {
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
        return context.getString(R.string.connectionError);
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


    @Override
    protected void onPostExecute(String result) {

        /** 4. TRAITEMENTS DES RETOURS D'ECRITURE ... */

        if (result.equals(context.getString(R.string.connectionError))) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }

        else if (result.equals(context.getString(R.string.insertUtilisateur))) {
            // Mettre dans pref
            Register.getInstance().putInPrefRegister();
        }

        else if (result.equals(context.getString(R.string.eraseCompte))) {
            Compte.getInstance().deco();
            Toast.makeText(context, context.getString(R.string.eraseCompteSuccess), Toast.LENGTH_SHORT).show();
        }

        else if (result.equals(context.getString(R.string.changeMdp))) {
            Compte.getInstance().okMdp();
        }

        else if (result.equals(context.getString(R.string.changeCoordClient))) {
            Compte.getInstance().okCoord();
        }


        /** 4. ... OU LECTURE DES RETOURS JSON */

        else {
            JSONObject jsonObject, jso;
            JSONArray jsonArray;

            try {
                jsonObject = new JSONObject(result);
                jsonArray = jsonObject.getJSONArray(context.getString(R.string.varReponseJson));

                // On utilise le json différement pour chaque script
                if (script.equals(context.getString(R.string.checkUtilisateur))) {
                    user = "";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jso = jsonArray.getJSONObject(i);
                        user = jso.getString(context.getString(R.string.prefPseudo));
                    }

                    // S'il ne trouve aucun pseudo similaire, on lance l'insertion
                    if (user.equals("")) {
                        script = context.getString(R.string.insertUtilisateur);
                        methode = context.getString(R.string.write);

                        compte = ((Spinner) Register.getInstance().findViewById(R.id.spinCompte)).getSelectedItem().toString();
                        pseudo = ((EditText) Register.getInstance().findViewById(R.id.edtPseudo)).getText().toString();
                        mdp = ((EditText) Register.getInstance().findViewById(R.id.edtMdp1)).getText().toString();
                        nom = ((EditText) Register.getInstance().findViewById(R.id.edtNom)).getText().toString();
                        prenom = ((EditText) Register.getInstance().findViewById(R.id.edtPrenom)).getText().toString();

                        ServerSide insertUtilisateur = new ServerSide(context);
                        insertUtilisateur.execute(script, methode, compte, pseudo, mdp, nom, prenom);
                    }

                    else {
                        Toast.makeText(context, context.getString(R.string.insertUtilisateurFail), Toast.LENGTH_SHORT).show();
                    }
                }

                // Cherche un compte avec les identifiants en paramètres
                else if (script.equals(context.getString(R.string.checkIdentifiants))) {
                    user = mdp = compte = nom = prenom = ville = cp = tel = adresse = "";

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jso = jsonArray.getJSONObject(i);
                        user = jso.getString(context.getString(R.string.prefPseudo));
                        mdp = jso.getString(context.getString(R.string.prefMdp));
                        nom = jso.getString(context.getString(R.string.prefNom));
                        prenom = jso.getString(context.getString(R.string.prefPrenom));
                        compte = jso.getString(context.getString(R.string.prefCompte));

                        if (compte.equals(context.getString(R.string.varClient))) {
                            ville = jso.getString(context.getString(R.string.prefVille));
                            cp = jso.getString(context.getString(R.string.prefCp));
                            tel = jso.getString(context.getString(R.string.prefTel));
                            adresse = jso.getString(context.getString(R.string.prefAdresse));
                        }
//                        else if (compte.equals(context.getString(R.string.varGerant))) {
//
//                        }
                    }

                    // Si un utilisateur a été trouvé
                    if (!user.equals("")) {
                        Login.getInstance().putInPrefLogin(user, mdp, compte, nom, prenom, ville, cp, tel, adresse);
                    }
                    else {
                        Toast.makeText(context, context.getString(R.string.connectionFail), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /** 2. FONCTION D'ENCODAGE DES DONNEES EN FONCTION DU SCRIPT A EXECUTER */

    private String encodeData(String lien, String... params) {
        String data = null;

        try {
            if (lien.equals(context.getString(R.string.insertUtilisateur))) {
                data = URLEncoder.encode(context.getString(R.string.prefCompte), "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.prefPseudo), "utf-8") + "=" + URLEncoder.encode(params[3], "utf-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.prefMdp), "utf-8") + "=" + URLEncoder.encode(params[4], "utf-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.prefNom), "utf-8") + "=" + URLEncoder.encode(params[5], "utf-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.prefPrenom), "utf-8") + "=" + URLEncoder.encode(params[6], "utf-8");
            }
            else if (lien.equals(context.getString(R.string.checkUtilisateur))) {
                data = URLEncoder.encode(context.getString(R.string.prefPseudo), "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8");
            }
            else if (lien.equals(context.getString(R.string.checkIdentifiants))) {
                data = URLEncoder.encode(context.getString(R.string.prefPseudo), "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.prefMdp), "utf-8") + "=" + URLEncoder.encode(params[3], "utf-8");
            }
            else if (lien.equals(context.getString(R.string.changeMdp))) {
                data = URLEncoder.encode(context.getString(R.string.prefPseudo), "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.prefMdp), "utf-8") + "=" + URLEncoder.encode(params[3], "utf-8");
            }
            else if (lien.equals(context.getString(R.string.eraseCompte))) {
                data = URLEncoder.encode(context.getString(R.string.prefPseudo), "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8");
            }
            else if (lien.equals(context.getString(R.string.changeCoordClient))) {
                data = URLEncoder.encode(context.getString(R.string.prefPseudo), "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.prefVille), "utf-8") + "=" + URLEncoder.encode(params[3], "utf-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.prefCp), "utf-8") + "=" + URLEncoder.encode(params[4], "utf-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.prefTel), "utf-8") + "=" + URLEncoder.encode(params[5], "utf-8") + "&" +
                        URLEncoder.encode(context.getString(R.string.prefAdresse), "utf-8") + "=" + URLEncoder.encode(params[6], "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return data;
    }

}