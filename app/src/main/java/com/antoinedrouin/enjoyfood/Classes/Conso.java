package com.antoinedrouin.enjoyfood.Classes;

import android.content.Context;

import com.antoinedrouin.enjoyfood.R;

/**
 * Created by cdsm04 on 15/09/2016.
 */
public class Conso {

    private String nom;
    private String description;
    private int quantite;
    private double prix;

    public Conso(String nom) {
        this.nom = nom;
    }

    public Conso(String nom, double prix, int quantite) {
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantite() {
        return quantite;
    }

    public String getQuantiteStr() {
        return Integer.toString(quantite);
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void addQuantite(int quantite) {
        this.quantite += quantite;
    }

    public double getPrix() {
        return prix;
    }

    public String getPrixStr() {
        return Double.toString(prix);
    }

    public void setPrix(String prix) {
        this.prix = Double.parseDouble(prix);
    }

    public double getTotal() {
        return Utilitaire.round(quantite * prix, 2);
    }

    public String displayForPanier(Context context) {
       return nom + " : " + getQuantiteStr() + " x " + getPrixStr() + context.getString(R.string.txtCurrency) + " = " + Double.toString(getTotal()) + context.getString(R.string.txtCurrency);
    }
}
