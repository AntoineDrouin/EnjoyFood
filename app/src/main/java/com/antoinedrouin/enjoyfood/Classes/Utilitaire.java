package com.antoinedrouin.enjoyfood.Classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.antoinedrouin.enjoyfood.R;

/**
 * Created by cdsm04 on 14/09/2016.
 *
 * Classe contenant quelques méthodes utiles
 */

public class Utilitaire {

    private Context context;

    public Utilitaire(Context co) {
        this.context = co;
    }

    public void putUtilisateur(Utilisateur ut) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = pref.edit();

        edit.putString(context.getString(R.string.prefId), ut.getId());
        edit.putString(context.getString(R.string.prefPseudo), ut.getPseudo());
        edit.putString(context.getString(R.string.prefMdp), ut.getMdp());
        edit.putString(context.getString(R.string.prefCompte), ut.getCompte());
        edit.putString(context.getString(R.string.prefNom), ut.getNom());
        edit.putString(context.getString(R.string.prefPrenom), ut.getPrenom());

        if (ut.getCompte().equals(context.getString(R.string.varClient))) {
            edit.putString(context.getString(R.string.prefVille), ut.getVille());
            edit.putString(context.getString(R.string.prefCp), ut.getCp());
            edit.putString(context.getString(R.string.prefTel), ut.getTel());
            edit.putString(context.getString(R.string.prefAdresse), ut.getAdresse());
        }

        edit.apply();
    }

    public static String returnStringFromBool(Boolean bo) {
        if (bo)
            return "1";
        else
            return "0";
    }

    public static Boolean returnBoolFromString(String st) {
        return st.equals("1");
    }
}
