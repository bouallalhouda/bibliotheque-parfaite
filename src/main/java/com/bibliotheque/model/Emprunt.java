package model;

import java.util.Date;

public class Emprunt {

    private int id;
    private Date dateEmprunt;
    private Date dateRetourPrevue;
    private Date dateRetourEffective;
    private Livre livre;
    private Membre membre;

    // Constructor
    public Emprunt(int id, Date dateEmprunt, Date dateRetourPrevue, Livre livre, Membre membre) {
        this.id = id;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.livre = livre;
        this.membre = membre;
    }

    // Empty constructor (useful for DAO)
    public Emprunt() {}

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(Date dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public Date getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    public void setDateRetourPrevue(Date dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }

    public Date getDateRetourEffective() {
        return dateRetourEffective;
    }

    public void setDateRetourEffective(Date dateRetourEffective) {
        this.dateRetourEffective = dateRetourEffective;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    public Membre getMembre() {
        return membre;
    }

    public void setMembre(Membre membre) {
        this.membre = membre;
    }
}
