package com.bibliotheque.model;

public abstract class Personne {
    protected String nom;
    protected String prenom;
    protected String email;

    public Personne(String nom, String prenom, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
}


