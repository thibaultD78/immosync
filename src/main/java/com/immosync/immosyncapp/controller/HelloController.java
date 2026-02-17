package com.immosync.immosyncapp.controller;

import com.immosync.immosyncapp.entities.Bien;
import com.immosync.immosyncapp.entities.Utilisateur;
import com.immosync.immosyncapp.services.BienService;
import com.immosync.immosyncapp.services.UtilisateurService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class HelloController implements Initializable{
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField mdpField;
    @FXML
    private TableView<Bien> tableBiens;
    @FXML
    private TextField txtAdresse;
    @FXML
    private TextField txtVille;
    @FXML
    private TextField txtCP;
    @FXML
    private TextField txtSurface;

    //private Utilisateur currentUser;
    private Integer idBienActuel;

    private final UtilisateurService userService;
    private final BienService bienService;


    public HelloController(UtilisateurService userService, BienService bienService) {
        this.userService = userService;
        this.bienService = bienService;
    }

    public void handleLogin(String email,String mdp) {
        Utilisateur user = userService.existeUser(email, mdp);
        //currentUser=user;
        if (user != null) {
            System.out.println("Connexion réussie pour : " + user.getEmail());
        } else {
            System.out.println("Login ou mot de passe incorrect.");
        }
    }
//BigDecimal surface = new BigDecimal(txtSurface.getText());

    public void chargerLesBiens(Utilisateur utilisateurConnecte) {
        List<Bien> liste = bienService.getBiensUtilisateur(utilisateurConnecte);
        tableBiens.getItems().setAll(liste);
    }

    private void handleEnregistrer(String adresse,String ville,String cp,BigDecimal surface,Utilisateur user) {
        try {
            bienService.creerBien(adresse, ville, cp, surface, user);

            System.out.println("Bien créé avec succès !");

        } catch (NumberFormatException e) {
            System.err.println("Erreur : La surface doit être un nombre.");
        }
    }

    //méthode appeler quand on clique sur modifier dans ta liste
    public void chargerDonnees(Bien bien) {
        this.idBienActuel = bien.getId();
        txtAdresse.setText(bien.getAdresse());
        txtVille.setText(bien.getVille());
        txtCP.setText(bien.getCodePostal());
        txtSurface.setText(bien.getSurface().toString());
    }

    private void handleValiderModif(String adresse,String ville, String cp, BigDecimal surface) {
        try {
            bienService.modifierBien(
                    idBienActuel,
                    adresse,
                    ville,
                    cp,
                    surface
            );
            System.out.println("Modification réussie !");
        } catch (Exception e) {
            System.err.println("Erreur lors de la modif : " + e.getMessage());
        }
    }
    @FXML
    private void ClickValiderModif() {
        try {
            String surfaceRaw = txtSurface.getText().trim();
            surfaceRaw = surfaceRaw.replace(",", ".");
            if (surfaceRaw.isEmpty()) {
                System.err.println("La surface ne peut pas être vide");
                return;
            }
            BigDecimal surfaceBien = new BigDecimal(surfaceRaw);
            handleValiderModif(txtAdresse.getText(),txtVille.getText(),txtCP.getText(),surfaceBien);
            System.out.println("Modification réussie !");

        } catch (NumberFormatException e) {
            System.err.println("Erreur : La surface doit être un nombre valide (ex: 120.50)");
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}


