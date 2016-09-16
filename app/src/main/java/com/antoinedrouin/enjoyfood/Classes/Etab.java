package com.antoinedrouin.enjoyfood.Classes;

/**
 * Created by cdsm04 on 14/09/2016.
 */
public class Etab {

    private String id;
    private String nom;
    private String cp;
    private String ville;
    private String adresse;
    private String tel;
    private String description;
    private double prixLivr;
    private boolean conges;

    public Etab(String id, String nom, String cp, String ville, String adresse, String tel, String description, double prixLivr, boolean conges) {
        this.id = id;
        this.nom = nom;
        this.cp = cp;
        this.ville = ville;
        this.adresse = adresse;
        this.tel = tel;
        this.description = description;
        this.prixLivr = prixLivr;
        this.conges = conges;
    }

    public Etab(String id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Etab() {

    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getCp() {
        return cp;
    }

    public String getVille() {
        return ville;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getTel() {
        return tel;
    }

    public String getDescription() {
        return description;
    }

    public String getPrixLivr() {
        return Double.toString(prixLivr);
    }

    public boolean isConges() {
        return conges;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrixLivr(String prixLiv) {
        this.prixLivr = Double.parseDouble(prixLiv);
    }

    public void setConges(boolean conges) {
        this.conges = conges;
    }
}
