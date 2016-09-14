package com.antoinedrouin.enjoyfood.Classes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.antoinedrouin.enjoyfood.Activities.Compte;
import com.antoinedrouin.enjoyfood.Activities.Consommable;
import com.antoinedrouin.enjoyfood.Activities.EtablissementManager;
import com.antoinedrouin.enjoyfood.Activities.EtablissementManagerInfos;
import com.antoinedrouin.enjoyfood.Activities.EtablissementManagerInfosDetails;
import com.antoinedrouin.enjoyfood.Activities.Login;
import com.antoinedrouin.enjoyfood.Activities.Register;
import com.antoinedrouin.enjoyfood.Fragments.Coordonnees;
import com.antoinedrouin.enjoyfood.R;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by cdsm04 on 26/01/2016.
 */

public class ServerSide extends AsyncTask<String, Void, String> {

    Context context;
    String script, methode, nom, prenom, compte, pseudo;
    public String user, mdp;
    long timeStart, timeEnd;
    private static final String encodage = "utf-8";

    public ServerSide (Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        timeStart = System.nanoTime();
    }


    @Override
    protected String doInBackground(String... params) {
        script = params[0]; // script à utiliser
        methode = params[1]; // indique lecture ou écriture
        String json;

        /** 1. PREPARATION DE LA CONNEXION */

        try {
            // Donne l'adresse du script php
            URL url = new URL(script);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream os = httpURLConnection.getOutputStream();
            BufferedWriter buffW = new BufferedWriter(new OutputStreamWriter(os, encodage));

            // 2. Encodage des données
            String data = encodeData(script, params);
            buffW.write(data);

            buffW.flush();
            buffW.close();
            os.close();

            /** 3. ALGORITHME DE LECTURE OU D'ECRITURE */

            // Ecriture
            if (methode.equals(context.getString(R.string.write))) {
                InputStream is = httpURLConnection.getInputStream();
                is.close();
                httpURLConnection.disconnect();

                return script;
            }

            // Lecture
            else if (methode.equals(context.getString(R.string.read))) {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("marquage", "doInBackground : " + e.getMessage());
        }

        // Return par défaut si aucune connexion
        return context.getString(R.string.connectionError);
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


    @Override
    protected void onPostExecute(String result) {
        timeEnd = System.nanoTime();
        Log.i("marquage", "Temps en nanosecondes : " + String.valueOf(timeEnd - timeStart));

        /** 4. TRAITEMENTS DES RETOURS D'ECRITURE ... */

        if (result.equals(context.getString(R.string.connectionError))) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }
        else if (result.equals(context.getString(R.string.insertUtilisateur))) {
            // Mettre dans pref
            Register.getInstance().putInPrefRegister();
        }
        else if (result.equals(context.getString(R.string.eraseCompte))) {
            Compte.getInstance().okErase();
        }
        else if (result.equals(context.getString(R.string.changeMdp))) {
            Compte.getInstance().okMdp();
        }
        else if (result.equals(context.getString(R.string.changeCoordClient))) {
            Compte.getInstance().okCoord();
        }
        else if (result.equals(context.getString(R.string.updateEtab))) {
            EtablissementManager.getInstance().okUpdateEtab();
        }
        else if (result.equals(context.getString(R.string.insertEtab))) {
            EtablissementManager.getInstance().okInsertEtab();
        }
        else if (result.equals(context.getString(R.string.insertHoraire)) || result.equals(context.getString(R.string.updateHoraire)) ||
                result.equals(context.getString(R.string.insertPaiement)) || result.equals(context.getString(R.string.updatePaiement)) ||
                result.equals(context.getString(R.string.insertCateg)) || result.equals(context.getString(R.string.updateCateg)) ||
                result.equals(context.getString(R.string.insertConso)) || result.equals(context.getString(R.string.updateConso)) ||
                result.equals(context.getString(R.string.deleteCateg)) ||  result.equals(context.getString(R.string.deleteConso)) ||
                result.equals(context.getString(R.string.deleteHoraire)) || result.equals(context.getString(R.string.deletePaiement))) {
            EtablissementManagerInfosDetails.getInstance().okUpdateObject();
        }

        /** 4. ... OU LECTURE DES RETOURS JSON */

        else {
            JSONObject jsonObject, jso;
            JSONArray jsonArray;

            try {
                jsonObject = new JSONObject(result);
                jsonArray = jsonObject.getJSONArray(context.getString(R.string.varReponseJson));
                Log.i("marquage", "Réponse json : " + jsonArray);

                // On utilise le json différement pour chaque script
                if (script.equals(context.getString(R.string.checkUtilisateur))) {
                    if (jsonArray.length() > 0) {
                        jso = jsonArray.getJSONObject(0);
                        user = jso.getString(context.getString(R.string.prefPseudo));
                    }

                    // S'il ne trouve aucun pseudo similaire, on lance l'insertion
                    if (user == null) {
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
                       Register.getInstance().insertFail();
                    }
                }

                // Cherche un compte avec les identifiants en paramètres
                else if (script.equals(context.getString(R.string.checkIdentifiants))) {
                    // Si un utilisateur a été trouvé
                    if (jsonArray.length() > 0) {
                        jso = jsonArray.getJSONObject(0);
                        Utilisateur utilisateur;

                        // Si client
                        if (jso.getString(context.getString(R.string.prefCompte)).equals(context.getString(R.string.varClient))) {
                            utilisateur = new Utilisateur(
                                    jso.getString(context.getString(R.string.prefId)),
                                    user, mdp,
                                    jso.getString(context.getString(R.string.prefNom)),
                                    jso.getString(context.getString(R.string.prefPrenom)),
                                    jso.getString(context.getString(R.string.prefCompte)),
                                    jso.getString(context.getString(R.string.prefVille)),
                                    jso.getString(context.getString(R.string.prefCp)),
                                    jso.getString(context.getString(R.string.prefTel)),
                                    jso.getString(context.getString(R.string.prefAdresse)));
                        }
                        // Si manager
                        else {
                            utilisateur = new Utilisateur(
                                    jso.getString(context.getString(R.string.prefId)),
                                    user, mdp,
                                    jso.getString(context.getString(R.string.prefNom)),
                                    jso.getString(context.getString(R.string.prefPrenom)),
                                    jso.getString(context.getString(R.string.prefCompte)));
                        }

                        Login.getInstance().putInPrefLogin(utilisateur);
                    }
                    // Sinon
                    else {
                        Login.getInstance().noAccount();
                    }
                }

                // Informations d'un établissement pour le consulter
                else if (script.equals(context.getString(R.string.getEtabById))) {
                    Etab etab = new Etab();
                    if (jsonArray.length() > 0) {
                        jso = jsonArray.getJSONObject(0);
                        etab = getEtabData(jso);
                    }

                    Coordonnees.getInstance().getInfos(etab);
                }

                // Informations d'un établissement pour le modifier
                else if (script.equals(context.getString(R.string.getEtabByManager))) {
                    Etab etab = new Etab();
                    if (jsonArray.length() > 0) {
                        jso = jsonArray.getJSONObject(0);
                        etab = getEtabData(jso);
                    }

                    EtablissementManager.getInstance().getInfos(etab);
                }

                // Horaires d'un établissement
                else if (script.equals(context.getString(R.string.getHoraires))) {
                    ArrayList<String> horaires = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jso = jsonArray.getJSONObject(i);
                        horaires.add(jso.getString(context.getString(R.string.prefJour)) + " : " +
                            jso.getString(context.getString(R.string.prefHeureDebut1)) + " - " +
                            jso.getString(context.getString(R.string.prefHeureFin1)) + " / " +
                            jso.getString(context.getString(R.string.prefHeureDebut2)) + " - " +
                            jso.getString(context.getString(R.string.prefHeureFin2)));
                    }

                    Coordonnees.getInstance().getHor(horaires);
                }

                else if (script.equals(context.getString(R.string.getHorairesInfos))) {
                    String[][] horaires = new String[jsonArray.length()][6];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jso = jsonArray.getJSONObject(i);
                        horaires[i][0] = jso.getString(context.getString(R.string.prefIdHor));
                        horaires[i][1] = jso.getString(context.getString(R.string.prefJour));
                        horaires[i][2] = jso.getString(context.getString(R.string.prefHeureDebut1));
                        horaires[i][3] = jso.getString(context.getString(R.string.prefHeureFin1));
                        horaires[i][4] = jso.getString(context.getString(R.string.prefHeureDebut2));
                        horaires[i][5] = jso.getString(context.getString(R.string.prefHeureFin2));
                    }

                    EtablissementManagerInfos.getInstance().fillLvInfos(horaires);
                }

                else if (script.equals(context.getString(R.string.getPaiements))) {
                    ArrayList<String> paiements = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jso = jsonArray.getJSONObject(i);
                        paiements.add(jso.getString(context.getString(R.string.prefNomPa)));
                    }

                    Coordonnees.getInstance().getPai(paiements);
                }

                else if (script.equals(context.getString(R.string.getPaiementsInfos))) {
                    String[][] paiements = new String[jsonArray.length()][2];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jso = jsonArray.getJSONObject(i);
                        paiements[i][0] = jso.getString(context.getString(R.string.prefIdPa));
                        paiements[i][1] = jso.getString(context.getString(R.string.prefNomPa));
                    }

                    EtablissementManagerInfos.getInstance().fillLvInfos(paiements);
                }

                else if (script.equals(context.getString(R.string.getCategInfos)) || script.equals(context.getString(R.string.getCategs))) {
                    String[][] categories = new String[jsonArray.length()][2];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jso = jsonArray.getJSONObject(i);
                        categories[i][0] = jso.getString(context.getString(R.string.prefIdCa));
                        categories[i][1] = jso.getString(context.getString(R.string.prefNomCa));
                    }
                    if (script.equals(context.getString(R.string.getCategInfos)))
                        EtablissementManagerInfos.getInstance().fillLvInfos(categories);
                    else if (script.equals(context.getString(R.string.getCategs)))
                        EtablissementManagerInfosDetails.getInstance().setComposConso(categories);
                }

                else if (script.equals(context.getString(R.string.getConsosInfos))) {
                    String[][] consos = new String[jsonArray.length()][5];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jso = jsonArray.getJSONObject(i);
                        consos[i][0] = jso.getString(context.getString(R.string.prefIdConso));
                        consos[i][1] = jso.getString(context.getString(R.string.prefNomConso));
                        consos[i][2] = jso.getString(context.getString(R.string.prefDescriptionConso));
                        consos[i][3] = jso.getString(context.getString(R.string.prefPrixConso));
                        consos[i][4] = jso.getString(context.getString(R.string.prefIdCa));
                    }

                    EtablissementManagerInfos.getInstance().fillLvInfos(consos);
                }

                else if (script.equals(context.getString(R.string.getMenu))) {
                    String[][] menu = new String[jsonArray.length()][2];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jso = jsonArray.getJSONObject(i);
                        menu[i][0] = jso.getString(context.getString(R.string.prefNomConso));
                        menu[i][1] = jso.getString(context.getString(R.string.prefNomCa));
                    }

                    ExpandableListData.assembleData(menu, context);
                }

                else if (script.equals(context.getString(R.string.getConso))) {
                    String[][] conso = new String[jsonArray.length()][2];

                    for (int i = 0; i <jsonArray.length(); i++) {
                        jso = jsonArray.getJSONObject(i);
                        conso[i][0] = jso.getString(context.getString(R.string.prefDescriptionConso));
                        conso[i][1] = jso.getString(context.getString(R.string.prefPrixConso));
                    }

                    Consommable.getInstance().getConso(conso);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("marquage", "Erreur de lecture du json ServerSide : " + e.getMessage());
            }
        }
    }

    private Etab getEtabData(JSONObject jso) throws JSONException {
        return new Etab(
                jso.getString(context.getString(R.string.prefIdEt)),
                jso.getString(context.getString(R.string.prefNomEt)),
                jso.getString(context.getString(R.string.prefCp)),
                jso.getString(context.getString(R.string.prefVille)),
                jso.getString(context.getString(R.string.prefAdresse)),
                jso.getString(context.getString(R.string.prefTel)),
                jso.getString(context.getString(R.string.prefDesc)),
                jso.getDouble(context.getString(R.string.prefPrixLivr)),
                Utilitaire.returnBoolFromString(jso.getString(context.getString(R.string.prefConges))));
    }


    /** 2. FONCTION D'ENCODAGE DES DONNEES EN FONCTION DU SCRIPT A EXECUTER */

    private String encodeData(String lien, String... params) {
        String key, value, data = "";
        String[] values = params;
        int i;
        int[] keys = new int[0];
        Log.i("marquage", "Script utilisé : " + lien);

        // On n'a plus besoin des 2 premiers paramètres
        for (i = 0; i < params.length - 2; i++) {
            values[i] = values[i+2];
        }

        try {
            if (lien.equals(context.getString(R.string.insertUtilisateur))) {
                keys = new int[]{R.string.prefCompte, R.string.prefPseudo, R.string.prefMdp, R.string.prefNom, R.string.prefPrenom};
            }
            else if (lien.equals(context.getString(R.string.checkUtilisateur)) || lien.equals(context.getString(R.string.eraseCompte))) {
                keys = new int[]{R.string.prefPseudo};
            }
            else if (lien.equals(context.getString(R.string.checkIdentifiants)) || lien.equals(context.getString(R.string.changeMdp))) {
                keys = new int[]{R.string.prefPseudo, R.string.prefMdp};
            }
            else if (lien.equals(context.getString(R.string.changeCoordClient))) {
                keys = new int[]{R.string.prefId, R.string.prefVille, R.string.prefCp, R.string.prefTel, R.string.prefAdresse};
            }
            else if (lien.equals(context.getString(R.string.getEtabById)) || lien.equals(context.getString(R.string.getHoraires)) ||
                    lien.equals(context.getString(R.string.getHorairesInfos)) || lien.equals(context.getString(R.string.getPaiements)) ||
                    lien.equals(context.getString(R.string.getPaiementsInfos)) || lien.equals(context.getString(R.string.getCategInfos)) ||
                    lien.equals(context.getString(R.string.getConsosInfos)) || lien.equals(context.getString(R.string.getCategs)) ||
                    lien.equals(context.getString(R.string.getMenu))) {
                keys = new int[]{R.string.prefIdEt};
            }
            else if (lien.equals(context.getString(R.string.getEtabByManager))) {
                keys = new int[]{R.string.prefId};
            }
            else if (lien.equals(context.getString(R.string.updateEtab))) {
                keys = new int[]{R.string.prefIdEt, R.string.prefId, R.string.prefDesc, R.string.prefPrixLivr, R.string.prefTel, R.string.prefConges};
            }
            else if (lien.equals(context.getString(R.string.insertEtab))) {
                keys = new int[]{R.string.prefIdEt, R.string.prefNomEt, R.string.prefId, R.string.prefDesc, R.string.prefPrixLivr, R.string.prefTel,
                    R.string.prefConges, R.string.prefCp, R.string.prefVille, R.string.prefAdresse};
            }
            else if (lien.equals(context.getString(R.string.updateHoraire))) {
                keys = new int[]{R.string.prefIdHor, R.string.prefJour, R.string.prefHeureDebut1, R.string.prefHeureFin1,
                        R.string.prefHeureDebut2, R.string.prefHeureFin2};
            }
            else if (lien.equals(context.getString(R.string.insertHoraire))) {
                keys = new int[]{R.string.prefIdEt, R.string.prefJour, R.string.prefHeureDebut1, R.string.prefHeureFin1,
                        R.string.prefHeureDebut2, R.string.prefHeureFin2};
            }
            else if (lien.equals(context.getString(R.string.deleteHoraire))) {
                keys = new int[]{R.string.prefIdHor};
            }
            else if (lien.equals(context.getString(R.string.insertPaiement))) {
                keys = new int[]{R.string.prefIdEt, R.string.prefNomPa};
            }
            else if (lien.equals(context.getString(R.string.updatePaiement))) {
                keys = new int[]{R.string.prefIdPa, R.string.prefNomPa};
            }
            else if (lien.equals(context.getString(R.string.deletePaiement))) {
                keys = new int[]{R.string.prefIdPa};
            }
            else if (lien.equals(context.getString(R.string.insertCateg))) {
                keys = new int[]{R.string.prefIdEt, R.string.prefNomCa};
            }
            else if (lien.equals(context.getString(R.string.updateCateg))) {
                keys = new int[]{R.string.prefIdCa, R.string.prefNomCa};
            }
            else if (lien.equals(context.getString(R.string.deleteCateg))) {
                keys = new int[]{R.string.prefIdCa};
            }
            else if (lien.equals(context.getString(R.string.insertConso))) {
                keys = new int[]{R.string.prefIdEt, R.string.prefNomConso, R.string.prefDescriptionConso, R.string.prefPrixConso, R.string.prefIdCa};
            }
            else if (lien.equals(context.getString(R.string.updateConso))) {
                keys = new int[]{R.string.prefIdConso, R.string.prefNomConso, R.string.prefDescriptionConso, R.string.prefPrixConso, R.string.prefIdCa};
            }
            else if (lien.equals(context.getString(R.string.deleteConso))) {
                keys = new int[]{R.string.prefIdConso};
            }
            else if (lien.equals(context.getString(R.string.getConso))) {
                keys = new int[]{R.string.prefIdEt, R.string.prefNomConso};
            }

            int lengArray = keys.length;

            // Création de la chaine de paramètres à envoyer
            for (i = 0; i < lengArray; i++) {
                key = context.getString(keys[i]);
                value = values[i];

                // Si le paramètre est un pseudo ou un mot de passe, il est haché
                if (key.equals(context.getString(R.string.prefPseudo)) || key.equals(context.getString(R.string.prefMdp))) {
                    value = hash(value);
                }

                data = data + URLEncoder.encode(key, encodage) + "=" + URLEncoder.encode(value, encodage);

                // Si ce n'est pas le dernier paramètre, on rajoute un caractère de liaison
                if (i < lengArray - 1) {
                    data = data + "&";
                }
            }

            Log.i("marquage", "Data : " + data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.i("marquage", "Erreur de formatage de la chaine de paramètres : " + e.getMessage());
        }

        return data;
    }

    // Permet de hacher une chaine grâce à SHA-512
    private String hash(String chaine) {
        String encryptedString = "";

        try {
            byte[] bytesOfMessage = chaine.getBytes(encodage);
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(bytesOfMessage);

            for (int i = 0; i < digest.length; i++)
                encryptedString += String.format("%02x", digest[i]);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return encryptedString;
    }
}