-- Si la base existe déjà, on la supprime et recrée
DROP DATABASE IF EXISTS bibliotheque_db;
CREATE DATABASE bibliotheque_db;
USE bibliotheque_db;

-- Table livres
CREATE TABLE livres (
    isbn VARCHAR(20) PRIMARY KEY,
    titre VARCHAR(100) NOT NULL,
    auteur VARCHAR(100) NOT NULL,
    annee_publication INT,
    disponible BOOLEAN DEFAULT true
);

-- Table membres
CREATE TABLE membres (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    actif BOOLEAN DEFAULT true
);

-- Table emprunts
CREATE TABLE emprunts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    livre_isbn VARCHAR(20),
    membre_id INT,
    date_emprunt DATE NOT NULL,
    date_retour_prevue DATE NOT NULL,
    date_retour_reelle DATE,
    FOREIGN KEY (livre_isbn) REFERENCES livres(isbn) ON DELETE CASCADE,
    FOREIGN KEY (membre_id) REFERENCES membres(id) ON DELETE CASCADE
);

-- Données de test
INSERT INTO livres (isbn, titre, auteur, annee_publication) VALUES
('978-123456', 'Le Petit Prince', 'Antoine de Saint-Exupéry', 1943),
('978-654321', '1984', 'George Orwell', 1949),
('978-789012', 'Harry Potter à l école des sorciers', 'J.K. Rowling', 1997);

INSERT INTO membres (nom, prenom, email) VALUES
('Dupont', 'Jean', 'jean.dupont@email.com'),
('Martin', 'Marie', 'marie.martin@email.com'),
('Durand', 'Pierre', 'pierre.durand@email.com');

-- Un emprunt de test
INSERT INTO emprunts (livre_isbn, membre_id, date_emprunt, date_retour_prevue) VALUES
('978-123456', 1, '2024-01-15', '2024-02-15');