package com.immosync.immosyncapp.controller;

import com.immosync.immosyncapp.entities.*;
import com.immosync.immosyncapp.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class HelloController implements Initializable{
    public AnchorPane paneConnexion;
    public AnchorPane paneAcceuil;
    public AnchorPane paneListBien;
    public AnchorPane paneCreaBien;
    public AnchorPane paneCreaChantier;
    public AnchorPane paneCreaDevisType;
    public TableView bienTable;
    public TableColumn colId;
    public TableColumn colCp;
    public TableColumn colAdresse;
    public TextField adresseField;
    public TextField villeField;
    public ComboBox<Utilisateur> proprietaireCombo;
    public ComboBox<Bien> bienCombo;
    public ComboBox<Inspecteur> inspecteurCombo;
    public ComboBox<Categorie> categorieCombo;
    public ComboBox<Prestataire> prestataireCombo;
    public Spinner quantitySpinner;
    public TableView prestationTable;
    public TableColumn colPrestation;
//    public TableColumn colCategorie;
    public TableColumn colQuantite;
    public TableColumn colPrixUnitaire;
    public TableColumn colTotal;
    public Label labelTotalFinal;
    public AnchorPane paneModifierBien;
    public TextField txtAdresse;
    public TextField txtVSurface;
    public Label tctVille;
    public TextField txtVille;
    public TextField txtCodePostal;
    public ComboBox<Utilisateur> modifProprietaireCombo;
    public ComboBox<Bien> cboBienAModif;
    public TextField txtSurface;
    public TextField txtCp;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField mdpField;

    private Integer idBienActuel;
    private Map<Prestataire, Integer> prestationsSelectionnees = new HashMap<>();
    private final ChantierService chantierService;
    private final UtilisateurService userService;
    private final BienService bienService;
    private final InspecteurService inspecteurService;
    private final CategorieService categorieService;
    private final PrestataireService prestataireService;


    public HelloController(ChantierService chantierService, UtilisateurService userService, BienService bienService, InspecteurService inspecteurService, CategorieService categorieService, PrestataireService prestataireService) {
        this.chantierService = chantierService;
        this.userService = userService;
        this.bienService = bienService;
        this.inspecteurService = inspecteurService;
        this.categorieService = categorieService;
        this.prestataireService = prestataireService;
    }
    public void changeImageViewImg(ImageView imgView, String linkImage){
        imgView.setImage(
                new Image(
                        getClass().getResource(
                                "/images/"+linkImage
                        ).toExternalForm()
                )
        );
    }

    public void invisible(AnchorPane apCourante){apCourante.setVisible(false);return;}
    public void visible(AnchorPane apCourante){apCourante.setVisible(true);return;}

    public void clearAll()
    {
        invisible(paneAcceuil);
        invisible(paneConnexion);
        invisible(paneCreaBien);
        invisible(paneCreaChantier);
        invisible(paneCreaDevisType);
        invisible(paneListBien);
        invisible(paneModifierBien);
    }
    private <T> void setComboConverter(ComboBox<T> combo, java.util.function.Function<T, String> mapper) {
        combo.setConverter(new javafx.util.StringConverter<T>() {
            @Override
            public String toString(T object) {
                return (object == null) ? "" : mapper.apply(object);
            }
            @Override
            public T fromString(String string) {
                return null;
            }
        });
    }

    public boolean handleLogin(String email,String mdp) {
        Utilisateur user = userService.existeUser(email, mdp);
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    public void chargerDonnees(Bien bien) {
        this.idBienActuel = bien.getId();
        txtAdresse.setText(bien.getAdresse());
        txtVille.setText(bien.getVille());
        txtCodePostal.setText(bien.getCodePostal());
        txtVSurface.setText(bien.getSurface().toString()!= null ? bien.getSurface().toString() : "");
        if (bien.getUtilisateur() != null) {
            modifProprietaireCombo.setValue(bien.getUtilisateur());
        } else {
            modifProprietaireCombo.getSelectionModel().clearSelection();
        }
    }

    private void handleValiderModif(String adresse,String ville, String cp, BigDecimal surface,Integer proprietaire) {
        try {
            bienService.modifierBien(
                    idBienActuel,
                    adresse,
                    ville,
                    cp,
                    surface,
                    proprietaire
            );
            System.out.println("Modification réussie !");
        } catch (Exception e) {
            System.err.println("Erreur lors de la modif : " + e.getMessage());
        }
    }

    @FXML
    private DevisType handleValiderChantier(String description, Bien bien, Inspecteur inspec) {
        try {
            // On récupère le retour du service
            DevisType devis = chantierService.creerChantierComplet(
                    description,
                    bien,
                    inspec,
                    prestationsSelectionnees
            );
            System.out.println("Tout a été créé en base !");
            return devis;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void initPageDevis() {
        categorieCombo.setItems(FXCollections.observableArrayList(categorieService.getAllCategories()));
        prestataireCombo.setDisable(true);
        prestataireCombo.getItems().clear();
        categorieCombo.setOnAction(e -> {
            Categorie cat = categorieCombo.getValue();
            if (cat != null) {
                prestataireCombo.setDisable(false);
                List<Prestataire> filtres = prestataireService.getPrestatairesByCategorie(cat);
                prestataireCombo.setItems(FXCollections.observableArrayList(filtres));
                prestataireCombo.getSelectionModel().selectFirst();
            }
        });
        prestationsSelectionnees.clear();
        prestationTable.getItems().clear();
        labelTotalFinal.setText("0.00 €");
    }

    private void calculerTotalGlobal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Object item : prestationTable.getItems()) {
            LigneDevis ligne = (LigneDevis) item;
            total = total.add(ligne.getTotal());
        }
        labelTotalFinal.setText(String.format("%.2f €", total));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCp.setCellValueFactory(new PropertyValueFactory<>("codePostal"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        colPrestation.setCellValueFactory(new PropertyValueFactory<>("nomPrestation"));
        //colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colPrixUnitaire.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        setComboConverter(proprietaireCombo, u -> u.getNom() + " " + u.getPrenom());
        setComboConverter(modifProprietaireCombo, u -> u.getNom() + " " + u.getPrenom());

        setComboConverter(bienCombo, b -> b.getAdresse() + " (" + b.getVille() + ")");
        setComboConverter(cboBienAModif, b -> b.getAdresse());

        setComboConverter(inspecteurCombo, i -> i.getNom() + " " + i.getPrenom());

        setComboConverter(categorieCombo, c -> c.getLibelle());
        setComboConverter(prestataireCombo, p -> p.getLibelle() + " (" + p.getPrixBase() + "€)");

        clearAll();
        visible(paneConnexion);

    }

    public void clicConnection(MouseEvent mouseEvent) {
        if(handleLogin(emailField.getText(),mdpField.getText())){
            clearAll();
            visible(paneAcceuil);
        }
    }

    public void goToBiens(MouseEvent mouseEvent) {
        clearAll();
        visible(paneListBien);
        List<Bien> listeBiens = bienService.getAllBiens();
        ObservableList<Bien> observableListe = FXCollections.observableArrayList(listeBiens);
        bienTable.setItems(observableListe);
        bienTable.refresh();
    }

    public void goToChantier(MouseEvent mouseEvent) {
        clearAll();
        visible(paneCreaChantier);

        List<Bien> tousLesBiens = bienService.getAllBiens();
        bienCombo.setItems(FXCollections.observableArrayList(tousLesBiens));

        List<Inspecteur> tousLesInspecteurs = inspecteurService.getAllInspecteurs();
        inspecteurCombo.setItems(FXCollections.observableArrayList(tousLesInspecteurs));

        bienCombo.getSelectionModel().clearSelection();
        inspecteurCombo.getSelectionModel().clearSelection();
    }

    public void clicDeconnexion(MouseEvent mouseEvent) {
        clearAll();
        visible(paneConnexion);
    }

    public void retourAccueil(MouseEvent actionEvent) {
        clearAll();
        visible(paneAcceuil);
    }

    public void ouvrirCreationBien(MouseEvent actionEvent) {
        clearAll();
        visible(paneCreaBien);
        List<Utilisateur> tousLesUsers = userService.getAllUsers();
        proprietaireCombo.setItems(FXCollections.observableArrayList(tousLesUsers));
        adresseField.clear();
        villeField.clear();
        txtSurface.clear();
        txtCp.clear();
        proprietaireCombo.getSelectionModel().clearSelection();
    }

    public void modifierBien(MouseEvent actionEvent) {
        clearAll();
        visible(paneModifierBien);

        cboBienAModif.setItems(FXCollections.observableArrayList(bienService.getAllBiens()));

        List<Utilisateur> tousLesUsers = userService.getAllUsers();
        modifProprietaireCombo.setItems(FXCollections.observableArrayList(tousLesUsers));

        cboBienAModif.setOnAction(e -> {
            Bien selectionne = (Bien) cboBienAModif.getValue();
            if (selectionne != null) {
                chargerDonnees(selectionne);
            }
        });
    }

    public void handleEnregistrer(MouseEvent actionEvent) {
        try {
            String adresse = adresseField.getText();
            String ville = villeField.getText();
            String cp = txtCp.getText();

            String surfaceRaw = txtSurface.getText().replace(",", ".");
            if (surfaceRaw.isEmpty()) {
                System.err.println("La surface est obligatoire");
                return;
            }
            BigDecimal surface = new BigDecimal(surfaceRaw);

            Utilisateur proprioSelectionne = (Utilisateur) proprietaireCombo.getValue();
            bienService.creerBien(adresse, ville, cp, surface, proprioSelectionne);

            System.out.println("Bien créé avec succès !");
            ListeBiens(actionEvent);

        } catch (NumberFormatException e) {
            System.err.println("Erreur : La surface doit être un nombre (ex: 75.5)");
        } catch (Exception e) {
            System.err.println("Erreur lors de la création : " + e.getMessage());
        }
    }

    public void handleCreerDevis(MouseEvent mouseEvent) {
        Bien bienChoisi = (Bien) bienCombo.getValue();
        Inspecteur inspecteurChoisi = (Inspecteur) inspecteurCombo.getValue();

        if (bienChoisi == null || inspecteurChoisi == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un bien et un inspecteur.");
            alert.showAndWait();
            return;
        }
        clearAll();
        visible(paneCreaDevisType);
        initPageDevis();
    }

    @FXML
    public void validerDevis(MouseEvent mouseEvent) {
        if (prestationsSelectionnees.isEmpty()) {
            System.err.println("Le devis est vide !");
            return;
        }

        // On récupère le devis créé pour pouvoir chercher les entrepreneurs
        DevisType devisCree = handleValiderChantier(
                "Chantier créé via l'app",
                (Bien) bienCombo.getValue(),
                (Inspecteur) inspecteurCombo.getValue()
        );

        if (devisCree != null) {
            // LOGIQUE MÉTIER : Trouver les entrepreneurs qui savent tout faire
            List<Entrepreneur> eligibles = chantierService.trouverEntrepreneursQualifies(devisCree.getId());

            System.out.println("Chantier et Devis enregistrés !");
            System.out.println("Nombre d'entrepreneurs éligibles trouvés : " + eligibles.size());

            // Affichage des résultats pour test
            for (Entrepreneur ent : eligibles) {
                System.out.println("Entrepreneur qualifié : " + ent.getNom());
            }

            // Ici, tu peux par exemple ouvrir une nouvelle fenêtre pour afficher ces entrepreneurs
            // ou envoyer un email automatique.
        }

        retourAccueil(mouseEvent);
    }

    public void ListeBiens(MouseEvent mouseEvent) {
        clearAll();
        visible(paneListBien);
        List<Bien> listeBiens = bienService.getAllBiens();
        ObservableList<Bien> observableListe = FXCollections.observableArrayList(listeBiens);
        bienTable.setItems(observableListe);
        bienTable.refresh();
    }

    public void EnregistrerModif(MouseEvent mouseEvent) {
        try {
            if (idBienActuel == null) {
                System.err.println("Aucun bien sélectionné !");
                return;
            }
            String surfaceRaw = txtVSurface.getText().trim();
            surfaceRaw = surfaceRaw.replace(",", ".");
            if (surfaceRaw.isEmpty()) {
                System.err.println("La surface ne peut pas être vide");
                return;
            }
            BigDecimal surfaceBien = new BigDecimal(surfaceRaw);
            Utilisateur nouveauProprio = (Utilisateur) modifProprietaireCombo.getValue();
            Integer idProprio = (nouveauProprio != null) ? nouveauProprio.getId() : null;
            handleValiderModif(txtAdresse.getText(),txtVille.getText(),txtCodePostal.getText(),surfaceBien,idProprio);
            System.out.println("Modification réussie !");

        } catch (NumberFormatException e) {
            System.err.println("Erreur : La surface doit être un nombre valide (ex: 120.50)");
        }
    }

    public void validerSelection(MouseEvent mouseEvent) {
        Prestataire p = (Prestataire) prestataireCombo.getValue();
        Integer qte = (Integer) quantitySpinner.getValue();

        if (p != null && qte > 0) {
            prestationsSelectionnees.put(p, qte);
            LigneDevis ligne = new LigneDevis(p, qte);
            prestationTable.getItems().add(ligne);
            calculerTotalGlobal();
        }
    }

    public static class LigneDevis {
        private final Prestataire prestataire;
        private final int quantite;
        private final BigDecimal total;

        public LigneDevis(Prestataire p, int qte) {
            this.prestataire = p;
            this.quantite = qte;
            this.total = p.getPrixBase().multiply(new BigDecimal(qte));
        }

        public String getNomPrestation() {
            return prestataire.getLibelle();
        }

        public String getCategorie() {
            if (prestataire.getCategorie() != null) {
                return prestataire.getCategorie().getLibelle();
            }
            return "N/A";
        }

        public int getQuantite() { return quantite; }

        public BigDecimal getPrixUnitaire() {
            return prestataire.getPrixBase();
        }

        public BigDecimal getTotal() { return total; }
    }
}




