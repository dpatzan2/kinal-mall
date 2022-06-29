/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.controller;


import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.swing.JOptionPane;
import org.diegopatzan.bean.Clientes;
import org.diegopatzan.bean.TipoCliente;
import org.diegopatzan.db.Conexion;
import org.diegopatzan.report.GenerarReporte;
import org.diegopatzan.system.Principal;



/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 14/06/2021
 * @time 09:36:30
 */
public class TipoClienteController implements Initializable{
    
    private Principal escenarioClientes;

    @FXML
    private void seleccionarElemento() {
        try {
            txtId.setText(String.valueOf(((TipoCliente)tblTipoCliente.getSelectionModel().getSelectedItem()).getId()));
            txtDescripcion.setText(((TipoCliente)tblTipoCliente.getSelectionModel().getSelectedItem()).getDescripcion());
        } catch (Exception e) {
            
        }
        
    }

    
    private enum Operaciones{
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO;
    }
    
    private Operaciones operacion = Operaciones.NINGUNO;
    private ObservableList<TipoCliente> listaTipoCliente;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnReporte;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtDescripcion;
    @FXML
    private TableView tblTipoCliente;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colDescripcion;

    public Principal getEscenarioClientes() {
        return escenarioClientes;
    }

    public void setEscenarioClientes(Principal escenarioClientes) {
        this.escenarioClientes = escenarioClientes;
    }
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
    }
    
    public ObservableList<TipoCliente> getTipoClientes(){
        ArrayList<TipoCliente> lista = new ArrayList<>();
        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarTipoCliente}");
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                lista.add(new TipoCliente(rs.getInt("id"), rs.getString("descripcion")));
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        listaTipoCliente = FXCollections.observableArrayList(lista);
        return listaTipoCliente;
    }
    public void cargarDatos(){
        tblTipoCliente.setItems(getTipoClientes());
        colId.setCellValueFactory(new PropertyValueFactory<TipoCliente, Integer>("id"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<TipoCliente, String>("descripcion"));
    }
    private void agregarTipoCliente() {
        TipoCliente registro = new TipoCliente();
        registro.setDescripcion(txtDescripcion.getText());
        try {
            PreparedStatement pstmt;
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarTipoCliente(?)}");
            pstmt.setString(1, registro.getDescripcion());
            pstmt.execute();
            cargarDatos();
            limpiarCampos();
            deshabilitarCampos();
           // listaTipoCliente.add(registro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void editarTipoCliente() {
        TipoCliente registro = (TipoCliente) tblTipoCliente.getSelectionModel().getSelectedItem();
        registro.setId(Integer.parseInt(txtId.getText()));
        registro.setDescripcion(txtDescripcion.getText());
        try {
            PreparedStatement pstmt;
            pstmt = pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarTipoCliente(?,?)}");
            pstmt.setInt(1, registro.getId());
            pstmt.setString(2, registro.getDescripcion());
            pstmt.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    private void eliminarTipoCliente() {
        try {
            try {
                PreparedStatement pstmt;
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarTipoCliente(?)}");
                pstmt.setInt(1, Integer.parseInt(txtId.getText()));
                pstmt.executeQuery();
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Error");
                                alert.setContentText("Este dato no puede ser eliminado, un cliente tiene asociado esta categoria \n id:" + txtId.getText());
                                alert.show();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void limpiarCampos(){
        txtId.clear();
        txtDescripcion.clear();
    }
    
    public void deshabilitarCampos(){
        txtId.setEditable(false);
        txtDescripcion.setEditable(false);
    }
    public void habilitarCampos(){
        txtId.setEditable(false);
        txtDescripcion.setEditable(true);
    }

    @FXML
    private void nuevo(ActionEvent event) {
        switch(operacion){
            case NINGUNO:
                habilitarCampos();
                limpiarCampos();
                operacion = TipoClienteController.Operaciones.GUARDAR;
                btnNuevo.setText("Guardar");
                btnModificar.setText("Cancelar");
                btnEliminar.setDisable(true);
                btnReporte.setDisable(true);
                break;
            case GUARDAR:
                if(txtDescripcion.getText().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    agregarTipoCliente();
                    btnNuevo.setText("Nuevo");
                    btnModificar.setText("Modificar");
                    btnEliminar.setDisable(false);
                    btnReporte.setDisable(false);
                    operacion = TipoClienteController.Operaciones.NINGUNO;
                }
                break;


                
        }
    }

    @FXML
    private void modificar(ActionEvent event) {
        switch(operacion){
            case NINGUNO:
                if(txtId.getText().equals("") && txtDescripcion.getText().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Para modificar tiene que seleccionar un registro");
                                alert.show();
                }else{
                    habilitarCampos();
                    btnModificar.setText("Actualizar");
                    btnEliminar.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnReporte.setDisable(true);
                    operacion = TipoClienteController.Operaciones.ACTUALIZAR;
                }
                break;
            case ACTUALIZAR:
                if(txtDescripcion.getText().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                editarTipoCliente();
                cargarDatos();
                limpiarCampos();
                deshabilitarCampos();
                btnModificar.setText("Modificar");
                btnEliminar.setText("Elminar");
                btnNuevo.setDisable(false);
                btnReporte.setDisable(false);
                operacion = TipoClienteController.Operaciones.NINGUNO;
                }
                break;
            case GUARDAR:
                btnNuevo.setText("Nuevo");
                btnModificar.setText("Modificar");
                btnEliminar.setDisable(false);
                btnReporte.setDisable(false);
                operacion = TipoClienteController.Operaciones.NINGUNO;
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
                btnEliminar.setText("Eliminar");
                operacion = TipoClienteController.Operaciones.NINGUNO;
                limpiarCampos();
                deshabilitarCampos();
                break;
            case NINGUNO:
                if(txtId.getText().equals("") && txtDescripcion.getText().equals("")){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Para eliminar tiene que seleccionar un registro");
                                alert.show();
                }else{
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText(null);
                    alert.setContentText("Esta seguro que desea eliminar este registro?" + "\nID: " + txtId.getText() + "\nDescripción: " + txtDescripcion.getText());
                    
                    Optional<ButtonType> respuesta = alert.showAndWait();
                    
                    if(respuesta.get() == ButtonType.OK){
                        eliminarTipoCliente();
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
        
       GenerarReporte.getInstance().mostrarReporte("ReporteTipoCliente.jasper", parametros, "Reporte de Tipo Cliente");
    }

    @FXML
    private void regresarClientes(MouseEvent event) {
        escenarioClientes.mostrarEscenaAdministracion();
    }

    
    

}
