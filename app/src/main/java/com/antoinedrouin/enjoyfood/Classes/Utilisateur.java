package com.antoinedrouin.enjoyfood.Classes;

/**
 * Created by cdsm04 on 13/09/2016.
 */
public class Utilisateur {

    private String id;
    private String pseudo;
    private String mdp;
    private String nom;
    private String prenom;
    private String compte;
    private String ville;
    private String cp;
    private String tel;
    private String adresse;

    public Utilisateur(String id, String ps, String md, String no, String pr, String co, String vi, String cp, String te, String ad) {
        this.id = id;
        this.pseudo = ps;
        this.mdp = md;
        this.nom = no;
        this.prenom = pr;
        this.compte = co;
        this.ville = vi;
        this.cp = cp;
        this.tel = te;
        this.adresse = ad;
    }

    public Utilisateur(String id, String ps, String md, String no, String pr, String co) {
        this.id = id;
        this.pseudo = ps;
        this.mdp = md;
        this.nom = no;
        this.prenom = pr;
        this.compte = co;
    }

    public String getId() {
        return id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getMdp() {
        return mdp;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getCompte() {
        return compte;
    }

    public String getVille() {
        return ville;
    }

    public String getCp() {
        return cp;
    }

    public String getTel() {
        return tel;
    }

    public String getAdresse() {
        return adresse;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id='" + id + '\'' +
                ", pseudo='" + pseudo + '\'' +
                ", mdp='" + mdp + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", compte='" + compte + '\'' +
                ", ville='" + ville + '\'' +
                ", cp='" + cp + '\'' +
                ", tel='" + tel + '\'' +
                ", adresse='" + adresse + '\'' +
                '}';
    }
}
