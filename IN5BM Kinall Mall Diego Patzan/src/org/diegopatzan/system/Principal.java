/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.system;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.diegopatzan.bean.CuentasPorPagar;
import org.diegopatzan.bean.Usuario;
import org.diegopatzan.controller.AdministracionController;
import org.diegopatzan.controller.AutorController;
import org.diegopatzan.controller.CargosController;
import org.diegopatzan.controller.ClientesController;
import org.diegopatzan.controller.CuentasPorCobrarController;
import org.diegopatzan.controller.CuentasPorPagarController;
import org.diegopatzan.controller.DepartamentosController;
import org.diegopatzan.controller.EmpleadosController;
import org.diegopatzan.controller.HorariosController;
import org.diegopatzan.controller.LocalesController;
import org.diegopatzan.controller.LoginController;
import org.diegopatzan.controller.MenuPrincipalController;
import org.diegopatzan.controller.ProveedoresController;
import org.diegopatzan.controller.TipoClienteController;



/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 5/05/2021
 * @time 11:27:40
 */
public class Principal extends Application{
    private final String PAQUETE_VIEW = "/org/diegopatzan/view/";
    private final String PAQUETE_IMAGES = "/org/diegopatzan/resource/images/";
    private Stage escenarioPrincipal;
    private Scene escena;
    private Usuario usuario;
    
    public static void main(String[] args) {
        launch(args);
        
    }

    @Override
    public void start(Stage stage) throws Exception {
        usuario = new Usuario();
        this.escenarioPrincipal = stage;
        /*String password = "YPsg2W84";
        String password64 = Base64.getEncoder().encodeToString(password.getBytes());
        System.out.println(password64);*/
        mostrarEscenaLoging();
        
        
    }
    
    public void menuPrincipal(){
        
        try {
            MenuPrincipalController menuPrincipal = null;
            menuPrincipal = (MenuPrincipalController) cambiarEscena("MenuPrincipalView.fxml", 890, 500);
            menuPrincipal.setEscenarioPrincipal(this);
            menuPrincipal.validarPermisos();
        } catch (Exception ex) {
            System.err.println("Se produjo un error al momento de cargar la vista del menu principal");
            ex.printStackTrace();
        }
    }
    
    public void mostrarEscenaAutor(){
        try {
            AutorController autorController = (AutorController) cambiarEscena("AutorView.fxml", 600, 302);
            escenarioPrincipal.setTitle("Kinal Mall");
            autorController.setEscenarioPrincipal(this);
        } catch(Exception e){
            System.err.println("Se produjo un error al cargar la vista del autor");
            e.printStackTrace();
        }
        
    }
    
    public void mostrarEscenaAdministracion(){
        try {
            AdministracionController  adminController;
            adminController = (AdministracionController) cambiarEscena("AdministracionView.fxml", 890, 500);
            adminController.setEscenarioPrincipal(this);
        }catch(Exception e){
            System.err.println("Se produjo un error al cargar la vista de administración");
            e.printStackTrace();
        }
    }
    
    public void mostrarEscenaClientes(){
        try{
            ClientesController clientesController;
            clientesController = (ClientesController) cambiarEscena("ClientesView.fxml", 1100, 600);
            clientesController.setEscenarioPrincipal(this);
        }catch(Exception e){
            System.out.println("Se produjo un error al cargar la vista Clientes");
            e.printStackTrace();
        }
    }
    public void mostrarEscenaTipoCliente(){
        try {
            TipoClienteController tipoClienteController;
            tipoClienteController = (TipoClienteController) cambiarEscena("TipoClienteView.fxml", 890, 500);
            tipoClienteController.setEscenarioClientes(this);
        } catch (Exception e) {
            System.err.println("Se produjo un error al cargar la vista Tipo Cliente");
            e.printStackTrace();
        }
    }
    
    public void mostrarEscenaDepartamentos(){
        try {
            DepartamentosController departamentosController;
            departamentosController = (DepartamentosController) cambiarEscena("DepartamentosView.fxml", 890, 500);
            departamentosController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.err.println("Se produjo un error al cargar la vista Departamentos");
            e.printStackTrace();
        }
    }
    public void mostrarEscenaLocales(){
        try {
            LocalesController localesController;
            localesController = (LocalesController) cambiarEscena("LocalesView.fxml", 1100, 600);
            localesController.setEscenarioClientes(this);
        } catch (Exception e) {
            System.err.println("Se produjo un error al cargar la vista Locales");
            e.printStackTrace();
        }
    }
    
    public void mostrarEscenaCuentasPorCobrar(){
        try {
            CuentasPorCobrarController cuentasPorCobrarController;
            cuentasPorCobrarController = (CuentasPorCobrarController) cambiarEscena("CuentasPorCobrarView.fxml", 1100, 600);
            cuentasPorCobrarController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.err.println("Se produjo un error al cargar la vista Cuentas por cobrar");
            e.printStackTrace();
        }
    }
    
    public void mostrarEscenaProveedores(){
        try {
            ProveedoresController proveedoresController;
            proveedoresController = (ProveedoresController) cambiarEscena("ProveedoresView.fxml", 1100, 600);
            proveedoresController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.err.println("Se produjo un error al cargar la vista Proveedores");
            e.printStackTrace();
        }
    }
    
    public void mostrarEscenaHorarios(){
        try {
            HorariosController horariosController;
            horariosController = (HorariosController) cambiarEscena("HorariosView.fxml", 1100, 600);
            horariosController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.err.println("Se produjo un error al cargar la vista Horarios");
            e.printStackTrace();
        }
    }
    
    public void mostrarEscenaCuentasPorPagar(){
        try {
            CuentasPorPagarController cuentasPorPagarController;
            cuentasPorPagarController = (CuentasPorPagarController) cambiarEscena("CuentasPorPagarView.fxml", 1100, 600);
            cuentasPorPagarController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.err.println("Se produjo un error al cargar la vista Cuentas por pagar");
            e.printStackTrace();
        }
    }
    
    public void mostrarEscenaEmpleados(){
        try {
            EmpleadosController empleadosController;
            empleadosController = (EmpleadosController) cambiarEscena("EmpleadosView.fxml", 1100, 600);
            empleadosController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.err.println("Se produjo un error al cargar la vista Empleados");
            e.printStackTrace();
        }
    }
    
    public void mostrarEscenaCargos(){
        try {
            CargosController cargosController;
            cargosController = (CargosController) cambiarEscena("CargosView.fxml", 890, 500);
            cargosController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.err.println("Se produjo un error al cargar la vista Cargos");
            e.printStackTrace();
        }
    }
    
    public void mostrarEscenaLoging(){
        try {
            LoginController loginController;
            loginController = (LoginController) cambiarEscena("LoginView.fxml", 890, 500);
            loginController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Se produjo un error al cargar la vista Login" + e.getMessage());
                                alert.show();
            e.printStackTrace();
        }
    }
    
    public Initializable cambiarEscena(String viewFxml, int ancho, int alto) throws IOException{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VIEW + viewFxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VIEW + viewFxml));
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo), ancho, alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        escenarioPrincipal.getIcons().add(new Image(PAQUETE_IMAGES + "Logo Kinal Mall.png"));
        escenarioPrincipal.setTitle("Kinal Mall");
        escenarioPrincipal.setResizable(false);
        escenarioPrincipal.show();
        resultado = cargadorFXML.getController();
        return resultado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


    
}
