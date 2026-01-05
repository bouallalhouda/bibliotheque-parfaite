package com.bibliotheque.model;

import java.util.Date;

public class Emprunt {
    private int id;
    private Date dateEmprunt;          
    private Date dateRetourPrevue;     
    private Date dateRetourReelle;
    private Livre livre;               
    private Membre membre;            
    private boolean retourne;
    
    // Constructeur
    public Emprunt() {}
    
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Date getDateEmprunt() { return dateEmprunt; }
    public void setDateEmprunt(Date dateEmprunt) { this.dateEmprunt = dateEmprunt; }
    
    public Date getDateRetourPrevue() { return dateRetourPrevue; }
    public void setDateRetourPrevue(Date dateRetourPrevue) { this.dateRetourPrevue = dateRetourPrevue; }
    
    public Date getDateRetourReelle() { return dateRetourReelle; }
    public void setDateRetourReelle(Date dateRetourReelle) { this.dateRetourReelle = dateRetourReelle; }
    
    public Livre getLivre() { return livre; }
    public void setLivre(Livre livre) { this.livre = livre; }
    
    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }
    
    public boolean isRetourne() { return retourne; }
    public void setRetourne(boolean retourne) { this.retourne = retourne; }
}
