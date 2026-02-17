-- 1. Table Utilisateur
CREATE TABLE utilisateur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(180) NOT NULL,
    roles LONGTEXT NOT NULL,
    password VARCHAR(255) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone VARCHAR(50),
    adresse VARCHAR(255),
    ville VARCHAR(100),
    code_postal VARCHAR(20)
);

-- 2. Table Categorie (pour classer les prestations)
CREATE TABLE categorie (
    id INT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

-- 3. Table Prestataire (Tes prestations catalogue)
CREATE TABLE prestataire (
    id INT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(200) NOT NULL,
    description LONGTEXT NOT NULL,
    prix_base DECIMAL(10,2) NOT NULL,
    categorie_id INT,
    CONSTRAINT fk_prestataire_categorie FOREIGN KEY (categorie_id) REFERENCES categorie(id)
);

-- 4. Table Inspecteur
CREATE TABLE inspecteur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telephone VARCHAR(255) NOT NULL,
    adresse VARCHAR(255) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    code_postal VARCHAR(20) NOT NULL
);

-- 5. Table Bien
CREATE TABLE bien (
    id INT AUTO_INCREMENT PRIMARY KEY,
    adresse VARCHAR(255) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    code_postal VARCHAR(20) NOT NULL,
    surface DECIMAL(10,2) NOT NULL,
    utilisateur_id INT NOT NULL,
    CONSTRAINT fk_bien_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id)
);

-- 6. Table Chantier
CREATE TABLE chantier (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date_creation DATETIME NOT NULL,
    date_validation DATETIME,
    statut VARCHAR(50) NOT NULL,
    description LONGTEXT NOT NULL,
    bien_id INT NOT NULL,
    inspecteur_id INT NOT NULL,
    CONSTRAINT fk_chantier_bien FOREIGN KEY (bien_id) REFERENCES bien(id),
    CONSTRAINT fk_chantier_inspecteur FOREIGN KEY (inspecteur_id) REFERENCES inspecteur(id)
);

-- 7. Table Devis Type
CREATE TABLE devis_type (
    id INT AUTO_INCREMENT PRIMARY KEY,
    intitule VARCHAR(200) NOT NULL,
    date_creation DATETIME NOT NULL,
    chantier_id INT,
    CONSTRAINT fk_devis_type_chantier FOREIGN KEY (chantier_id) REFERENCES chantier(id)
);

-- 8. Table de liaison Devis_Type_Prestation (avec Quantité)
CREATE TABLE devis_type_prestation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quantite INT NOT NULL,
    devis_type_id INT NOT NULL,
    prestataire_id INT NOT NULL,
    CONSTRAINT fk_dtp_devis FOREIGN KEY (devis_type_id) REFERENCES devis_type(id),
    CONSTRAINT fk_dtp_prestataire FOREIGN KEY (prestataire_id) REFERENCES prestataire(id)
);
-- 1. Génération de 5 Utilisateurs

INSERT INTO utilisateur (email, roles, password, nom, prenom, telephone, adresse, ville, code_postal) VALUES

('admin@admin.com', '["ADMIN"]', 'admin', 'Dupont', 'Jean', '0601020304', '12 rue de la Paix', 'Paris', '75001'),

('marie.leclerc@email.com', '["ROLE_USER", "ROLE_ADMIN"]', '$2y$10$eImiTXuWV.zhp0AFfG52xe2v9W.bX.XW', 'Leclerc', 'Marie', '0611223344', '45 avenue des Champs', 'Lyon', '69002'),

('lucas.bernard@email.com', '["ROLE_USER"]', '$2y$10$eImiTXuWV.zhp0AFfG52xe2v9W.bX.XW', 'Bernard', 'Lucas', '0788991122', '8 impasses des Lilas', 'Marseille', '13008'),

('sophie.petit@email.com', '["ROLE_USER"]', '$2y$10$eImiTXuWV.zhp0AFfG52xe2v9W.bX.XW', 'Petit', 'Sophie', '0655443322', '22 rue du Commerce', 'Bordeaux', '33000'),

('thomas.durand@email.com', '["ROLE_USER"]', '$2y$10$eImiTXuWV.zhp0AFfG52xe2v9W.bX.XW', 'Durand', 'Thomas', '0699887766', '5 place de la Mairie', 'Nantes', '44000');
 
-- 2. Génération de 5 Catégories

INSERT INTO categorie (libelle) VALUES

('Plomberie'),

('Électricité'),

('Peinture'),

('Maçonnerie'),

('Menuiserie');
 
-- 3. Génération de 5 Prestataires (liés aux catégories ci-dessus)

INSERT INTO prestataire (libelle, description, prix_base, categorie_id) VALUES

('Installation Robinetterie', 'Remplacement et installation de mitigeurs haute qualité.', 150.00, 1),

('Mise aux normes tableau', 'Vérification et mise en conformité électrique du tableau principal.', 450.00, 2),

('Peinture Murale (m2)', 'Peinture acrylique blanche, deux couches incluses.', 25.50, 3),

('Réfection de muret', 'Réparation de murets extérieurs en briques ou parpaings.', 600.00, 4),

('Pose de fenêtre PVC', 'Installation de fenêtre double vitrage standard.', 320.00, 5);
 
-- 4. Génération de 5 Inspecteurs

INSERT INTO inspecteur (nom, prenom, email, telephone, adresse, ville, code_postal) VALUES

('Robert', 'Alain', 'a.robert@immosync.fr', '0102030405', '10 rue du Contrôle', 'Paris', '75015'),

('Moreau', 'Claire', 'c.moreau@immosync.fr', '0203040506', '5 bis boulevard Technique', 'Lyon', '69003'),

('Fournier', 'David', 'd.fournier@immosync.fr', '0304050607', '18 rue de la Vigie', 'Toulouse', '31000'),

('Girard', 'Isabelle', 'i.girard@immosync.fr', '0405060708', '2 avenue de l''Audit', 'Nice', '06000'),

('Bonnet', 'Pierre', 'p.bonnet@immosync.fr', '0506070809', '7 chemin de l''Expert', 'Lille', '59000');
 
-- 5. Génération de 5 Biens (liés aux utilisateurs 1 à 5)

INSERT INTO bien (adresse, ville, code_postal, surface, utilisateur_id) VALUES

('102 rue de Rivoli', 'Paris', '75001', 45.50, 1),

('15 rue Victor Hugo', 'Lyon', '69002', 72.00, 2),

('24 avenue du Prado', 'Marseille', '13008', 35.00, 3),

('8 cours de l''Intendance', 'Bordeaux', '33000', 110.20, 4),

('12 quai de la Fosse', 'Nantes', '44000', 58.00, 5);
 