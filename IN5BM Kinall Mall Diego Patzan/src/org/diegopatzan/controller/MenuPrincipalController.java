/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import org.diegopatzan.system.Principal;

/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 5/05/2021
 * @time 11:43:32
 */
public class MenuPrincipalController implements Initializable {
    
    private Principal escenarioPrincipal;
    @FXML
    private MenuItem menuAdministracion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    public void validarPermisos(){
        if (escenarioPrincipal.getUsuario().getRol() != 1) {
            menuAdministracion.setVisible(false);
        }
    }
    


    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    @FXML
    private void mostrarVistaAutor(ActionEvent event) {
        escenarioPrincipal.mostrarEscenaAutor();
    }

    @FXML
    private void mostrarVistaAdministracion(ActionEvent event) {
        this.escenarioPrincipal.mostrarEscenaAdministracion();
    }

    @FXML
    private void mostrarEscenaClientes(ActionEvent event) {
        this.escenarioPrincipal.mostrarEscenaClientes();
    }

 

    @FXML
    private void mostrarEscenaProveedores(ActionEvent event) {
        this.escenarioPrincipal.mostrarEscenaProveedores();
    }



    @FXML
    private void mostrarEscenaEmpleados(ActionEvent event) {
        this.escenarioPrincipal.mostrarEscenaEmpleados();
    }

    @FXML
    private void cerrarSecion(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText(null);
                    alert.setContentText("¿Esta seguro que desea cerrar sesión?");
                    
                    Optional<ButtonType> respuesta = alert.showAndWait();
                    
                    if(respuesta.get() == ButtonType.OK){
                        
                        escenarioPrincipal.getUsuario().setRol(0);
                        escenarioPrincipal.mostrarEscenaLoging();
                    }
        
    }


    
}
