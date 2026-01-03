-- Drop database if it exists
DROP DATABASE IF EXISTS bibliotheque_db;

-- Create database
CREATE DATABASE bibliotheque_db;

-- Use the database
USE bibliotheque_db;

-- Table livres
CREATE TABLE livres (
    isbn VARCHAR(20) PRIMARY KEY,
    titre VARCHAR(100) NOT NULL,
    auteur VARCHAR(100) NOT NULL,      -- Fixed: auteur (not antenv)
    annee_publication INT,
    disponible BOOLEAN DEFAULT true    -- Fixed: BOOLEAN (not BOOLEM)
);

-- Table membres
CREATE TABLE membres (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    actif BOOLEAN DEFAULT true         -- Fixed: BOOLEAN (not BOOLEM)
);

-- Table emprunts
CREATE TABLE emprunts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    livre_isbn VARCHAR(20),
    membre_id INT,
    date_emprunt DATE,
    date_retour_prevue DATE,
    date_retour_reelle DATE,
    FOREIGN KEY (livre_isbn) REFERENCES livres(isbn),
    FOREIGN KEY (membre_id) REFERENCES membres(id)
);

-- Insert sample data
INSERT INTO livres (isbn, titre, auteur, annee_publication, disponible) VALUES
('978-123456', 'Le Petit Prince', 'Antoine de Saint-Exupéry', 1943, true),
('978-234567', '1984', 'George Orwell', 1949, true),
('978-345678', 'Harry Potter à l\'école des sorciers', 'J.K. Rowling', 1997, true);

INSERT INTO membres (nom, prenom, email, actif) VALUES
('Dupont', 'Jean', 'jean.dupont@email.com', true),
('Martin', 'Marie', 'marie.martin@email.com', true),
('Bernard', 'Pierre', 'pierre.bernard@email.com', true)
