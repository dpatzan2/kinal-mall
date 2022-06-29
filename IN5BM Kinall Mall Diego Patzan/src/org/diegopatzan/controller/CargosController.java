/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.diegopatzan.bean.Cargos;
import org.diegopatzan.system.Principal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import org.diegopatzan.db.Conexion;
import org.diegopatzan.report.GenerarReporte;

/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 13/07/2021
 * @time 09:07:30
 */
public class CargosController implements Initializable{

    private Principal escenarioPrincipal;
    
    private ObservableList<Cargos> listaCargos;
    
    public boolean existeElementoSeleccionado() {
        return tblCargos.getSelectionModel().getSelectedItem() != null;
    }
    
    @FXML
    private Button btnNuevo;
    @FXML
    private ImageView imgNuevo;
    @FXML
    private Button btnModificar;
    @FXML
    private ImageView imgEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private ImageView imgEliminar;
    @FXML
    private Button btnReporte;
    @FXML
    private ImageView imgReporte;
    @FXML
    private TableView tblCargos;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtNombre;

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    @FXML
    private void seleccionarElemento() {
        if (existeElementoSeleccionado()) {
            txtId.setText(String.valueOf(((Cargos)tblCargos.getSelectionModel().getSelectedItem()).getId()));
            txtNombre.setText(((Cargos) tblCargos.getSelectionModel().getSelectedItem()).getNombre());
        }
    }

  

    
    
    private enum Operaciones{
        NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO
    }
    
    private Operaciones operacion = Operaciones.NINGUNO;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
    }
    
    public ObservableList<Cargos> getCargos(){
        ArrayList<Cargos> listado = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarCargos}");
            rs = pstmt.executeQuery();
            
            while(rs.next()){
                listado.add(new Cargos(
                        rs.getInt("id"), 
                        rs.getString("nombre")
                )
                );
            }
            listaCargos =  FXCollections.observableArrayList(listado);
            
        } catch (SQLException e) {
            System.err.println("\n Se produjo un error al intentar consultar la tabla Proveedores en la base de datos");
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return listaCargos;
    }
    
    public void cargarDatos(){
        tblCargos.setItems(getCargos());
        colId.setCellValueFactory(new PropertyValueFactory<Cargos, Integer>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Cargos, String>("nombre"));

    }
    
    public void agregarCargos() {
        Cargos cargos = new Cargos();
        cargos.setNombre(txtNombre.getText());
        
        
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarCargos(?)}");
            pstmt.setString(1, cargos.getNombre());

            

            
            pstmt.execute();
            //listaCargos.add(cargos);
            
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar agregar un nueva cargos");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void editarCargos() {
        Cargos cargos = new Cargos();
        cargos.setId(Integer.parseInt(txtId.getText()));
        cargos.setNombre(txtNombre.getText());
        
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarCargos(?, ?)}");
            
            pstmt.setInt(1, cargos.getId());
            pstmt.setString(2, cargos.getNombre());

            

            
            pstmt.execute();
            
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar editar un cargo");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }        
    }
    
    public void eliminarCargos() {

            Cargos cargos = (Cargos) tblCargos.getSelectionModel().getSelectedItem();
            

            
            PreparedStatement pstmt = null;
            
            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarCargos(?)}");
                pstmt.setInt(1, cargos.getId());
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
                                alert.setContentText("Este dato no puede ser eliminado, un empleado tiene asociado este cargo \n id:" + txtId.getText());
                                alert.show();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    pstmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        
        
    }
    
    public void habilitarCampos(){
        txtId.setEditable(false);
        txtNombre.setEditable(true);
    }
    
    public void limpiarCampos(){
        txtId.clear();
        txtNombre.clear();
    }
    
    public void deshabilitarCampos(){
        txtId.setEditable(false);
        txtNombre.setEditable(false);
    }
    
     @FXML
    private void nuevo(ActionEvent event) {
        
        switch(operacion){
            case NINGUNO:
                habilitarCampos();
                limpiarCampos();
                operacion = CargosController.Operaciones.GUARDAR;
                btnNuevo.setText("Guardar");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_save_50px.png"));
                btnModificar.setText("Cancelar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/cancelar.png"));
                btnEliminar.setDisable(true);
                btnReporte.setDisable(true);
                break;
            case GUARDAR:
                if(txtNombre.getText().trim().equals("") ){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    agregarCargos();
                    cargarDatos();
                    deshabilitarCampos();
                    limpiarCampos();
                    btnNuevo.setText("Nuevo");
                    imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                    btnModificar.setText("Modificar");
                    imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                    btnEliminar.setDisable(false);
                    btnReporte.setDisable(false);
                    operacion = CargosController.Operaciones.NINGUNO;  

                    
                }
                break;


                
        }
    }
    
    
    
    

    @FXML
    private void modificar(ActionEvent event) {
        switch(operacion){
            case NINGUNO:
                if(existeElementoSeleccionado()){
                    habilitarCampos();
                    btnModificar.setText("Actualizar");
                    imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_save_50px.png"));
                    btnEliminar.setText("Cancelar");
                    imgEliminar.setImage(new Image("/org/diegopatzan/resource/images/cancelar.png"));
                    btnNuevo.setDisable(true);
                    btnReporte.setDisable(true);
                    operacion = CargosController.Operaciones.ACTUALIZAR;
                }else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText("Advertencia");
                    alert.setContentText("Para modificar tiene que seleccionar un registro");
                    alert.show();
                }
                break;

            case ACTUALIZAR:
                if (txtNombre.getText().trim().equals("") ) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                        editarCargos();
                        cargarDatos();
                        limpiarCampos();
                        deshabilitarCampos();
                        imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                        btnModificar.setText("Modificar");
                        btnEliminar.setText("Elminar");
                        imgEliminar.setImage(new Image("/org/diegopatzan/resource/images/icons8_empty_trash_32px.png"));
                         btnNuevo.setDisable(false);
                        btnReporte.setDisable(false);
                        operacion = CargosController.Operaciones.NINGUNO; 
                    
                
                }
                break;
            case GUARDAR:
                btnNuevo.setText("Nuevo");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                btnModificar.setText("Modificar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                btnEliminar.setDisable(false);
                btnReporte.setDisable(false);
                operacion = CargosController.Operaciones.NINGUNO;
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
                operacion = CargosController.Operaciones.NINGUNO;
                limpiarCampos();
                deshabilitarCampos();
                break;
            case NINGUNO:
                if (existeElementoSeleccionado()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText(null);
                    alert.setContentText("Esta seguro que desea eliminar este registro?" + "\nID: " + txtId.getText() + "\nNomnre: " + txtNombre.getText());
                    
                    Optional<ButtonType> respuesta = alert.showAndWait();
                    
                    if(respuesta.get() == ButtonType.OK){
                        eliminarCargos();
                        limpiarCampos();
                        cargarDatos();
                    }
                }else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText("Advertencia");
                    alert.setContentText("Para eliminar tiene que seleccionar un registro");
                    alert.show();
                }
                break;

        }
    }

    @FXML
    private void Reporte(ActionEvent event) {
        
        Map parametros = new HashMap();
        
        GenerarReporte.getInstance().mostrarReporte("ReporteCargos.jasper", parametros, "Reporte de Cargos");
    }

    @FXML
    private void regresar(MouseEvent event) {
        this.escenarioPrincipal.mostrarEscenaAdministracion();
    }

}
