package com.bibliotheque.controller;

import com.bibliotheque.model.Livre;

public class LivreFormController {
    private LivreController livreController;
    private Livre livreToEdit;

    public void setLivreController(LivreController livreController) {
        this.livreController = livreController;
    }

    public void setLivreToEdit(Livre livre) {
        this.livreToEdit = livre;
    }

    public void setModeAjout() {
        // TODO: implement
    }

    public void setModeModification(Livre livre) {
        // TODO: implement
    }
}