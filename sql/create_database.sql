DROP DATABASE IF EXISTS bibliotheque;
CREATE DATABASE bibliotheque;
USE bibliotheque;

-- Table livres
CREATE TABLE livre (
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
    FOREIGN KEY (livre_isbn) REFERENCES livre(isbn) ON DELETE CASCADE,  --  
    FOREIGN KEY (membre_id) REFERENCES membres(id) ON DELETE CASCADE
);

-- Données de test
INSERT INTO livre (isbn, titre, auteur, annee_publication) VALUES
('978-123456', 'javafxe', 'someone', 1943),
('978-654321', '1984', 'personl', 1949),
('978-789012', 'ESI', 'J.K. maybeg', 1997);

INSERT INTO membres (nom, prenom, email) VALUES
('Aya', 'ait allal', 'aya.esi@email.com'),
('chaimae', 'Zeroual', 'chaim.esi@email.com'),
('Aya', 'elghlimi', 'aya.esigh@email.com');

-- Un emprunt de test
INSERT INTO emprunts (livre_isbn, membre_id, date_emprunt, date_retour_prevue) VALUES
('978-123456', 1, '2024-01-15', '2024-02-15');
SHOW TABLES;
SHOW FULL TABLES;
