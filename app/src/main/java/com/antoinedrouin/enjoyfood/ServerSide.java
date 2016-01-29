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

        // Return par défaut s'il y a une erreur
        return context.getString(R.string.connectionError);
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


    @Override
    protected void onPostExecute(String result) {
        /** TRAITEMENTS DES RETOURS EN ECRITURZE */

        if (result.equals(context.getString(R.string.connectionError))) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }

        else if (result.equals(context.getString(R.string.insertUtilisateur))) {
            Register.getInstance().putInPrefRegister(pseudo, mdp, compte, nom, prenom);
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

        /** LECTURE DES RETOURS JSON */

        else {
            JSONObject jsonObject, jso;
            JSONArray jsonArray;

            try {
                jsonObject = new JSONObject(result);
                jsonArray = jsonObject.getJSONArray("response");

                // On utilise le json différement pour chaque script
                if (script.equals(context.getString(R.string.checkUtilisateur))) {
                    user = "";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jso = jsonArray.getJSONObject(i);
                        user = jso.getString("pseudo");
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
                        user = jso.getString("pseudo");
                        mdp = jso.getString("mdp");
                        nom = jso.getString("nom");
                        prenom = jso.getString("prenom");
                        compte = jso.getString("compte");

                        if (compte.equals(context.getString(R.string.varClient))) {
                            ville = jso.getString("ville");
                            cp = jso.getString("cp");
                            tel = jso.getString("tel");
                            adresse = jso.getString("adresse");
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

    /** Fonction d'encodage des données en fonction du script à executer */

    private String encodeData(String lien, String... params) {
        String data = null;

        try {
            if (lien.equals(context.getString(R.string.insertUtilisateur))) {
                data = URLEncoder.encode("compte", "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8") + "&" +
                        URLEncoder.encode("pseudo", "utf-8") + "=" + URLEncoder.encode(params[3], "utf-8") + "&" +
                        URLEncoder.encode("mdp", "utf-8") + "=" + URLEncoder.encode(params[4], "utf-8") + "&" +
                        URLEncoder.encode("nom", "utf-8") + "=" + URLEncoder.encode(params[5], "utf-8") + "&" +
                        URLEncoder.encode("prenom", "utf-8") + "=" + URLEncoder.encode(params[6], "utf-8");
            }
            else if (lien.equals(context.getString(R.string.checkUtilisateur))) {
                data = URLEncoder.encode("pseudo", "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8");
            }
            else if (lien.equals(context.getString(R.string.checkIdentifiants))) {
                data = URLEncoder.encode("pseudo", "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8") + "&" +
                        URLEncoder.encode("mdp", "utf-8") + "=" + URLEncoder.encode(params[3], "utf-8");
            }
            else if (lien.equals(context.getString(R.string.changeMdp))) {
                data = URLEncoder.encode("pseudo", "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8") + "&" +
                        URLEncoder.encode("mdp", "utf-8") + "=" + URLEncoder.encode(params[3], "utf-8");
            }
            else if (lien.equals(context.getString(R.string.eraseCompte))) {
                data = URLEncoder.encode("pseudo", "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8");
            }
            else if (lien.equals(context.getString(R.string.changeCoordClient))) {
                data = URLEncoder.encode("pseudo", "utf-8") + "=" + URLEncoder.encode(params[2], "utf-8") + "&" +
                        URLEncoder.encode("ville", "utf-8") + "=" + URLEncoder.encode(params[3], "utf-8") + "&" +
                        URLEncoder.encode("cp", "utf-8") + "=" + URLEncoder.encode(params[4], "utf-8") + "&" +
                        URLEncoder.encode("tel", "utf-8") + "=" + URLEncoder.encode(params[5], "utf-8") + "&" +
                        URLEncoder.encode("add", "utf-8") + "=" + URLEncoder.encode(params[6], "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return data;
    }

}