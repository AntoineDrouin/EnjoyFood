package com.antoinedrouin.enjoyfood.Classes;

import android.content.Context;

import com.antoinedrouin.enjoyfood.R;

/**
 * Created by cdsm04 on 15/09/2016.
 */
public class Comm {

    private String id;
    private String etat;
    private String remarque;
    private String adresse;
    private String tel;
    private double prix;
    private double prixLivr;
    private int quantite;

    public Comm() {

    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    private double getPrix() {
        return prix;
    }

    public String getPrixStr() {
        return Double.toString(prix);
    }

    public void addPrix(double prix) {
        this.prix = Utilitaire.round(this.prix + prix, 2);
    }

    private double getPrixLivr() {
        return prixLivr;
    }

    public String getPrixLivrStr() {
        return Double.toString(prixLivr);
    }

    public String getPrixLivrDisplay(Context context) {
        return context.getString(R.string.lvPrixLivr) + " " + getPrixLivrStr() + context.getString(R.string.txtCurrency);
    }

    public void setPrixLivr(double prixLivr) {
        this.prixLivr = prixLivr;
    }

    public String getQuantiteStr() {
        return Integer.toString(quantite);
    }

    public void addQuantite(int quantite) {
        this.quantite += quantite;
    }

    public String getTotal() {
        return Double.toString(Utilitaire.round(getPrix() + getPrixLivr(), 2));
    }

    public String getTotalDisplay(Context context) {
        return context.getString(R.string.lvTotal) + " " + getTotal() + context.getString(R.string.txtCurrency);
    }
}
