/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import org.diegopatzan.system.Principal;
import java.sql.*;
import java.util.Base64;
import javafx.scene.control.Alert;
import org.diegopatzan.bean.Usuario;
import org.diegopatzan.db.Conexion;

/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 4/08/2021
 * @time 12:40:07
 */
public class LoginController implements Initializable{

    private Principal escenarioPrincipal;
    @FXML
    private JFXTextField txtUsuario;
    @FXML
    private JFXPasswordField pfPassword;
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

        private String getPassword(String user){
        String passDecodificado = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarUsuario(?)}");
            pstmt.setString(1, user);
            System.out.println(pstmt.toString());
            rs = pstmt.executeQuery();
            
            while (rs.next()) {      
                escenarioPrincipal.getUsuario().setNombre(rs.getString("user"));
                escenarioPrincipal.getUsuario().setPass(rs.getString("pass"));
                escenarioPrincipal.getUsuario().setNombre(rs.getString("nombre"));
                escenarioPrincipal.getUsuario().setRol(rs.getInt("rol"));
                
                passDecodificado = new String(Base64.getDecoder().decode(escenarioPrincipal.getUsuario().getPass()));
            }
            
            
    } catch (SQLException e) {
        e.printStackTrace();
    } catch (Exception e){
        e.printStackTrace();
    }finally{
            try {            
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
        }
        return passDecodificado;
}
    
    
    @FXML
    private void login() {
        if (!(txtUsuario.getText().isEmpty() || pfPassword.getText().isEmpty())) {
            if (escenarioPrincipal.getUsuario() != null) {
                if (pfPassword.getText().equals(getPassword(txtUsuario.getText().trim()))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("Bienvenido: \n" + escenarioPrincipal.getUsuario().getNombre());
                    alert.showAndWait();
                    escenarioPrincipal.menuPrincipal();
                }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("El usuario o contraseña son invalidos");
                alert.show();
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("El usuario o contraseña son invalidos");
                alert.show();
            }
        }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Asegurese de llenar todos los campos");
                alert.show();
        }
        
        
        
        
    }



    
    

}
