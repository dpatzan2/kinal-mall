/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import org.diegopatzan.system.Principal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;
import org.diegopatzan.bean.Administracion;
import org.diegopatzan.db.Conexion;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.diegopatzan.report.GenerarReporte;

/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 13/05/2021
 * @time 10:28:47
 */
public class AdministracionController implements Initializable{

    private Principal escenarioPrincipal;
    private ObservableList<Administracion> listaAdministracion;
    
    public boolean existeElementoSeleccionado() {
        return tblAdministracion.getSelectionModel().getSelectedItem() != null;
    }
    
    @FXML
    private ImageView imgNuevo;
    @FXML
    private ImageView imgEditar;
    @FXML
    private ImageView imgEliminar;
    @FXML
    private ImageView imgReporte;

    @FXML
    private void mostrarEscenaDepartamentos(ActionEvent event) {
        this.escenarioPrincipal.mostrarEscenaDepartamentos();
    }

    @FXML
    private void mostrarEscenaTipoCliente(ActionEvent event) {
        this.escenarioPrincipal.mostrarEscenaTipoCliente();
    }

    @FXML
    private void mostrarEscenaLocales(ActionEvent event) {
        this.escenarioPrincipal.mostrarEscenaLocales();
    }

    @FXML
    private void mostrarEscenaCargos(ActionEvent event) {
        this.escenarioPrincipal.mostrarEscenaCargos();
    }
    
    private enum Operaciones{
        NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO
    }
    
    private Operaciones operacion = Operaciones.NINGUNO;
    
    @FXML
    private TextField txtId;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnReporte;
    @FXML
    private TextField txtDireccion;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TableView tblAdministracion;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colDireccion;
    @FXML
    private TableColumn colTelefono;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    @FXML
    public void regresar(MouseEvent event) {
        escenarioPrincipal.menuPrincipal();
    }
    
    public ObservableList<Administracion> getAdministracion(){
        
        ArrayList<Administracion> listado = new ArrayList<Administracion>();
        
        try{
            //CallableStatement stmt; 
            PreparedStatement stmt;
            stmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarAdministracion()}");
            ResultSet resultado = stmt.executeQuery();
            
            while(resultado.next()){
                listado.add(new Administracion(
                        resultado.getInt("id"), 
                        resultado.getString("direccion"), 
                        resultado.getString("telefono")
                )
                );
            }
            
            resultado.close();
            stmt.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        listaAdministracion = FXCollections.observableArrayList(listado);
        return listaAdministracion;
    }
    
    public void cargarDatos(){
        try {
            tblAdministracion.setItems(getAdministracion());
            colId.setCellValueFactory(new PropertyValueFactory<Administracion, Integer>("id"));
            colDireccion.setCellValueFactory(new PropertyValueFactory<Administracion, String>("direccion"));
            colTelefono.setCellValueFactory(new PropertyValueFactory<Administracion, String>("telefono"));
        } catch (Exception e) {
        }

    }
    
    @FXML
    public void seleccionarElemento(){
        
        try{
        txtId.setText(String.valueOf(((Administracion)tblAdministracion.getSelectionModel().getSelectedItem()).getId()));
        txtDireccion.setText(((Administracion) tblAdministracion.getSelectionModel().getSelectedItem()).getDireccion());
        txtTelefono.setText(((Administracion) tblAdministracion.getSelectionModel().getSelectedItem()).getTelefono());
        }catch (Exception e){

            
        }
        
    
        
    }

    private void habiliarCampos() {
        txtId.setEditable(false);
        txtDireccion.setEditable(true);
        txtTelefono.setEditable(true);
        
    }


    private void agregarAdministracion() {
        Administracion registro = new Administracion();
        registro.setDireccion(txtDireccion.getText());
        registro.setTelefono(txtTelefono.getText());
        
        try{
            //CallableStatement stmt;
            PreparedStatement stmt;
            stmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarAdministracion(?,?)}");
            stmt.setString(1, registro.getDireccion());
            stmt.setString(2, registro.getTelefono());
            //stmt.executeUpdate();
            stmt.execute();
            cargarDatos();
            limpiarCampos();
            deshabilitarCampos();
            //listaAdministracion.add(registro);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    private void editarAdministracion(){
        Administracion registro = (Administracion) tblAdministracion.getSelectionModel().getSelectedItem();
        registro.setId(Integer.parseInt(txtId.getText()));
        registro.setDireccion(txtDireccion.getText());
        registro.setTelefono(txtTelefono.getText());
        
        try{
            PreparedStatement stmt;
            stmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarAdministracion(?, ?, ?)}");
            stmt.setInt(1, registro.getId());
            stmt.setString(2, registro.getDireccion());
            stmt.setString(3, registro.getTelefono());
            
            stmt.execute();
            
            
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    private void eliminarAdministracion(){
        try{
            PreparedStatement stmt;
            stmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarAdministracion(?)}");
            stmt.setInt(1, Integer.parseInt(txtId.getText()));
            
            stmt.execute();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();
            
        }catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Error");
                                alert.setContentText("Este dato no puede ser eliminado, "
                                        + "esta administración esta asociada en empleados, cuentas por cobrar y "
                                        + "cuentas por pagar \n id:" + txtId.getText());
                                alert.show();
        }
    }
    
    
    
    public void limpiarCampos(){
        txtId.clear();
        txtDireccion.clear();
        txtTelefono.clear();
    }
    
    public void deshabilitarCampos(){
        txtId.setEditable(false);
        txtDireccion.setEditable(false);
        txtTelefono.setEditable(false);
    }
    
    public boolean validarTelefono(String numero){
        
        Pattern pattern1 = Pattern.compile("^[0-9]{8}$");
        Matcher matcher1 = pattern1.matcher(numero);
        return matcher1.matches();
    }
    

    @FXML
    private void nuevo(ActionEvent event) {
        switch(operacion){
            case NINGUNO:
                habiliarCampos();
                limpiarCampos();
                operacion = Operaciones.GUARDAR;
                btnNuevo.setText("Guardar");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_save_50px.png"));
                btnModificar.setText("Cancelar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/cancelar.png"));
                btnEliminar.setDisable(true);
                btnReporte.setDisable(true);
                break;
            case GUARDAR:
                if(txtDireccion.getText().trim().equals("") || txtTelefono.getText().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    if (validarTelefono(txtTelefono.getText())) {
                       agregarAdministracion();
                        btnNuevo.setText("Nuevo");
                        imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                        btnModificar.setText("Modificar");
                        imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                        btnEliminar.setDisable(false);
                        btnReporte.setDisable(false);
                        operacion = Operaciones.NINGUNO; 
                    }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Numero de telefono invalido \nEjemplo: \n11111111 \n(8 Digitos)" );
                                alert.show();
                    }
                    
                }
                break;


                
        }
    }

    @FXML
    
    private void modificar(ActionEvent event) {
        
        switch(operacion){
            case NINGUNO:
                if(txtId.getText().equals("") || txtDireccion.getText().equals("") || txtTelefono.getText().equals("")){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Para modificar tiene que seleccionar un registro");
                                alert.show();
                }else{
                    habiliarCampos();
                    btnModificar.setText("Actualizar");
                    imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_save_50px.png"));
                    btnEliminar.setText("Cancelar");
                    imgEliminar.setImage(new Image("/org/diegopatzan/resource/images/cancelar.png"));
                    btnNuevo.setDisable(true);
                    btnReporte.setDisable(true);
                    operacion = Operaciones.ACTUALIZAR;
                }
                break;
            case ACTUALIZAR:
                if (txtDireccion.getText().trim().equals("") || txtTelefono.getText().trim().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    if (validarTelefono(txtTelefono.getText())) {
                        editarAdministracion();
                        cargarDatos();
                        limpiarCampos();
                        deshabilitarCampos();
                        btnModificar.setText("Modificar");
                        imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                        btnEliminar.setText("Elminar");
                        imgEliminar.setImage(new Image("/org/diegopatzan/resource/images/icons8_empty_trash_32px.png"));
                        btnNuevo.setDisable(false);
                        btnReporte.setDisable(false);
                        operacion = Operaciones.NINGUNO;
                    }else{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Numero de telefono invalido \nEjemplo: \n11111111 \n(8 Digitos)" );
                                alert.show();
                    }
                
                }
                break;
            case GUARDAR:
                btnNuevo.setText("Nuevo");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                btnModificar.setText("Modificar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                btnEliminar.setDisable(false);
                btnReporte.setDisable(false);
                operacion = Operaciones.NINGUNO;
                deshabilitarCampos();
                limpiarCampos();
                break;
        }
    }

    @FXML
    private void Eliminar(ActionEvent event) {
        switch(operacion){
            case ACTUALIZAR:
                btnNuevo.setDisable(false);
                btnReporte.setDisable(false);
                btnModificar.setText("Modificar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                btnEliminar.setText("Eliminar");
                imgEliminar.setImage(new Image("/org/diegopatzan/resource/images/icons8_empty_trash_32px.png"));
                operacion = Operaciones.NINGUNO;
                limpiarCampos();
                deshabilitarCampos();
                break;
            case NINGUNO:
                if (txtDireccion.getText().trim().equals("") || txtTelefono.getText().trim().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Para eliminar tiene que seleccionar un registro");
                                alert.show();
                }else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText(null);
                    alert.setContentText("Esta seguro que desea eliminar este registro?" 
                            + "\nID: " + txtId.getText() 
                            + "\nDirección: " + txtDireccion.getText() 
                            + "\nTeléfono: " + txtTelefono.getText());
                    
                    Optional<ButtonType> respuesta = alert.showAndWait();
                    
                    if(respuesta.get() == ButtonType.OK){
                        
                        eliminarAdministracion();
                        limpiarCampos();
                        cargarDatos();
                    }
                }
                break;
        }
    }

    @FXML
    private void Reporte(ActionEvent event) {
        Map parametros = new HashMap();
        if (existeElementoSeleccionado()) {
            int codigoAdministracion = ((Administracion)tblAdministracion.getSelectionModel().getSelectedItem()).getId();
            parametros.put("id", codigoAdministracion);
            GenerarReporte.getInstance().mostrarReporte("ReporteAdministracionPorId.jasper", parametros, "Reporte de empleados por administracón");
        }else{
            GenerarReporte.getInstance().mostrarReporte("ReporteAdministracion.jasper", parametros, "Reporte de Administración");
        }
    }

}
