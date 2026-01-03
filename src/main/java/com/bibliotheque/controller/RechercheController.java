package com.bibliotheque.controller;

import java.util.List;

public class RechercheController {
    private LivreController livreController;
    private List<String> categories;

    public void setLivreController(LivreController livreController) {
        this.livreController = livreController;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}