/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.controller;

import com.mysql.cj.jdbc.PreparedStatementWrapper;
import com.mysql.cj.protocol.Resultset;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.diegopatzan.bean.Departamentos;
import org.diegopatzan.db.Conexion;
import org.diegopatzan.report.GenerarReporte;
import org.diegopatzan.system.Principal;

/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 14/06/2021
 * @time 12:18:40
 */
public class DepartamentosController implements Initializable{

    private Principal escenarioPrincipal;
    @FXML
    private ImageView imgNuevo;
    @FXML
    private ImageView imgEditar;
    @FXML
    private ImageView imgEliminar;
    @FXML
    private ImageView imgReporte;
    
    private enum Operaciones{
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO;
    }
    
    private Operaciones operacion = Operaciones.NINGUNO;
    private ObservableList<Departamentos> listaDepartamentos;
    
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
    private TextField txtNombre;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableView tblDepartamentos;

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
    }
    
    public ObservableList<Departamentos> getDepartamentos(){
        ArrayList<Departamentos> lista = new ArrayList<>();
        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarDepartamentos}");
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                lista.add(new Departamentos(rs.getInt("id"), rs.getString("nombre")));
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        listaDepartamentos = FXCollections.observableArrayList(lista);
        return listaDepartamentos;
    }
    public void cargarDatos(){
        tblDepartamentos.setItems(getDepartamentos());
        colId.setCellValueFactory(new PropertyValueFactory<Departamentos, Integer>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Departamentos, String>("nombre"));
        
    }
    @FXML
    private void seleccionarElemento() {
        try {
            txtId.setText(String.valueOf(((Departamentos)tblDepartamentos.getSelectionModel().getSelectedItem()).getId()));
            txtNombre.setText(((Departamentos)tblDepartamentos.getSelectionModel().getSelectedItem()).getNombre());
        } catch (Exception e) {
            
        }
        
    }
    
    private void agregarDepartamentos() {
        Departamentos registro = new Departamentos();
        registro.setNombre(txtNombre.getText());
        try {
            PreparedStatement pstmt;
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarDepartamentos(?)}");
            pstmt.setString(1, registro.getNombre());
            pstmt.execute();
            cargarDatos();
            limpiarCampos();
            deshabilitarCampos();
            //listaDepartamentos.add(registro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void editarDepartamentos() {
        Departamentos registro = (Departamentos) tblDepartamentos.getSelectionModel().getSelectedItem();
        registro.setId(Integer.parseInt(txtId.getText()));
        registro.setNombre(txtNombre.getText());
        try {
            PreparedStatement pstmt;
            pstmt = pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarDepartamentos(?,?)}");
            pstmt.setInt(1, registro.getId());
            pstmt.setString(2, registro.getNombre());
            pstmt.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    private void eliminarDepartamentos() {
        try {
            PreparedStatement pstmt;
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarDepartamentos(?)}");
            pstmt.setInt(1, Integer.parseInt(txtId.getText()));
            pstmt.executeQuery();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Error");
                                alert.setContentText("Este dato no puede ser eliminado, un empleado tiene asociado este departamento \n id:" + txtId.getText());
                                alert.show();
        }
    }
    
    public void limpiarCampos(){
        txtId.clear();
        txtNombre.clear();
    }
    
    public void deshabilitarCampos(){
        txtId.setEditable(false);
        txtNombre.setEditable(false);
    }
    public void habilitarCampos(){
        txtId.setEditable(false);
        txtNombre.setEditable(true);
    }

    @FXML
    private void nuevo(ActionEvent event) {
        switch(operacion){
            case NINGUNO:
                habilitarCampos();
                limpiarCampos();
                operacion = DepartamentosController.Operaciones.GUARDAR;
                btnNuevo.setText("Guardar");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_save_50px.png"));
                btnModificar.setText("Cancelar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/cancelar.png"));
                btnEliminar.setDisable(true);
                btnReporte.setDisable(true);
                break;
            case GUARDAR:
                if(txtNombre.getText().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    agregarDepartamentos();
                    btnNuevo.setText("Nuevo");
                    imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                    btnModificar.setText("Modificar");
                    imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                    btnEliminar.setDisable(false);
                    btnReporte.setDisable(false);
                    operacion = DepartamentosController.Operaciones.NINGUNO;
                }
                break;


                
        }
    }

    @FXML
    private void modificar(ActionEvent event) {
        switch(operacion){
            case NINGUNO:
                if(txtId.getText().trim().equals("") && txtNombre.getText().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Para modificar tiene que seleccionar un registro");
                                alert.show();
                }else{
                    habilitarCampos();
                    btnModificar.setText("Actualizar");
                    imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_save_50px.png"));
                    btnEliminar.setText("Cancelar");
                    imgEliminar.setImage(new Image("/org/diegopatzan/resource/images/cancelar.png"));
                    btnNuevo.setDisable(true);
                    btnReporte.setDisable(true);
                    operacion = DepartamentosController.Operaciones.ACTUALIZAR;
                }
                break;
            case ACTUALIZAR:
                if (txtNombre.getText().trim().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                editarDepartamentos();
                cargarDatos();
                limpiarCampos();
                deshabilitarCampos();
                btnModificar.setText("Modificar");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                btnEliminar.setText("Elminar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                btnNuevo.setDisable(false);
                btnReporte.setDisable(false);
                operacion = DepartamentosController.Operaciones.NINGUNO;
                }
                break;
            case GUARDAR:
                btnNuevo.setText("Nuevo");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                btnModificar.setText("Modificar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                btnEliminar.setDisable(false);
                btnReporte.setDisable(false);
                operacion = DepartamentosController.Operaciones.NINGUNO;
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
                operacion = DepartamentosController.Operaciones.NINGUNO;
                limpiarCampos();
                deshabilitarCampos();
                break;
            case NINGUNO:
                if(txtId.getText().trim().equals("") && txtNombre.getText().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Para eliminar tiene que seleccionar un registro");
                                alert.show();
                }else{
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText(null);
                    alert.setContentText("Esta seguro que desea eliminar este registro?" + "\nID: " + txtId.getText() + "\nNombre: " + txtNombre.getText());
                    
                    Optional<ButtonType> respuesta = alert.showAndWait();
                    
                    if(respuesta.get() == ButtonType.OK){
                        eliminarDepartamentos();
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
        
        GenerarReporte.getInstance().mostrarReporte("ReporteDepartamentos.jasper", parametros, "Reporte de Departamentos");
    }

    @FXML
    private void regresar(MouseEvent event) {
        this.escenarioPrincipal.mostrarEscenaAdministracion();
    }

}
