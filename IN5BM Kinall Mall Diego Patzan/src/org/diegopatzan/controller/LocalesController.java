/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javax.swing.JOptionPane;
import org.diegopatzan.bean.Locales;
import org.diegopatzan.db.Conexion;
import org.diegopatzan.report.GenerarReporte;
import org.diegopatzan.system.Principal;

/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 15/06/2021
 * @time 08:59:15
 */
public class LocalesController implements Initializable{

    private Principal escenarioClientes;
    private Boolean disponibilidad = false;
    
    public boolean existeElementoSeleccionado() {
        return tblLocales.getSelectionModel().getSelectedItem() != null;
    }
    private ObservableList<String> listadoCantidad;
    @FXML
    private TableView tblLocales;
    @FXML
    private CheckBox chDisponibilidad;
    @FXML
    private TextField txtSaldoLiquido;
    @FXML
    private ImageView imgNuevo;
    @FXML
    private ImageView imgEditar;
    @FXML
    private ImageView imgEliminar;
    @FXML
    private ImageView imgReporte;
    @FXML
    private TextField txtCantidad;

    @FXML
    private void disponibilidad(ActionEvent event) {
        if(chDisponibilidad.isSelected()){
            disponibilidad = true;
        }else{
            disponibilidad = false;
        }
    }
    
    private enum Operaciones{
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO;
    }
    
    private Operaciones operacion = Operaciones.NINGUNO;
    
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
    private TextField txtSaldoFavor;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colSaldoFavor;
    @FXML
    private TableColumn colSaldoContra;
    @FXML
    private TableColumn colMesesPendientes;
    @FXML
    private TableColumn colDisponibilidad;
    @FXML
    private TableColumn colValorLocal;
    @FXML
    private TableColumn colValorAdministracion;
    @FXML
    private TextField txtSaldoContra;
    @FXML
    private TextField txtMesesPendientes;
    @FXML
    private TextField txtValorLocal;
    @FXML
    private TextField txtValorAdministracion;


    public Principal getEscenarioClientes() {
        return escenarioClientes;
    }

    public void setEscenarioClientes(Principal escenarioClientes) {
        this.escenarioClientes = escenarioClientes;
    }
    
    private ObservableList<Locales> listaLocales;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       cargarDatos();
       //contarLocales();
    }
    
    public ObservableList<Locales> getLocales(){
        ArrayList<Locales> lista = new ArrayList<>();
        try{
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarLocales}");
            ResultSet rs = pstmt.executeQuery();
            
            while(rs.next()){
                lista.add(new Locales(
                        rs.getInt("id"), 
                        rs.getBigDecimal("saldoFavor"),
                        rs.getBigDecimal("saldoContra"),
                        rs.getInt("mesesPendientes"),
                        rs.getBoolean("disponibilidad"),
                        rs.getBigDecimal("valorLocal"),
                        rs.getBigDecimal("valorAdministracion")
                    )
                );
                
                System.out.println("");
            }
            rs.close();
            pstmt.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        listaLocales = FXCollections.observableArrayList(lista);
        return listaLocales;
    }

    
    public void contarLocales(){
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ContarLocales}");
            rs = pstmt.executeQuery();
            
            while (rs.next()) {                
                txtCantidad.setText(String.valueOf(rs.getInt("Cantidad")));
            }
        } catch (Exception e) {
        }
    }
    

    
        
   
    public void cargarDatos(){
        tblLocales.setItems(getLocales());
        colId.setCellValueFactory(new PropertyValueFactory<Locales, Integer>("id"));
        colSaldoFavor.setCellValueFactory(new PropertyValueFactory<Locales, BigDecimal>("saldoFavor"));
        colSaldoContra.setCellValueFactory(new PropertyValueFactory<Locales, BigDecimal>("saldoContra"));
        colMesesPendientes.setCellValueFactory(new PropertyValueFactory<Locales, Integer>("mesesPendientes"));
        contarLocales();
        colDisponibilidad.setCellValueFactory(new PropertyValueFactory<Locales, Boolean>("disponibilidad"));
        colValorLocal.setCellValueFactory(new PropertyValueFactory<Locales, BigDecimal>("valorLocal"));

        colValorAdministracion.setCellValueFactory(new PropertyValueFactory<Locales, BigDecimal>("valorAdministracion"));
        
    }
    @FXML
    private void seleccionarElemento() {
        try {
            txtId.setText(String.valueOf(((Locales)tblLocales.getSelectionModel().getSelectedItem()).getId()));
            txtSaldoFavor.setText(String.valueOf(((Locales)tblLocales.getSelectionModel().getSelectedItem()).getSaldoFavor()));
            txtSaldoContra.setText(String.valueOf(((Locales)tblLocales.getSelectionModel().getSelectedItem()).getSaldoContra()));
            txtMesesPendientes.setText(String.valueOf(((Locales)tblLocales.getSelectionModel().getSelectedItem()).getMesesPendientes()));
            BigDecimal favor = ((Locales)tblLocales.getSelectionModel().getSelectedItem()).getSaldoFavor();
            BigDecimal contra = ((Locales)tblLocales.getSelectionModel().getSelectedItem()).getSaldoContra();
            txtSaldoLiquido.setText(String.valueOf(favor.subtract(contra)));
            if(((Locales)tblLocales.getSelectionModel().getSelectedItem()).isDisponibilidad() == true){
            chDisponibilidad.setSelected(true);
        }else{
            chDisponibilidad.setSelected(false);
            }
            txtValorLocal.setText(String.valueOf(((Locales)tblLocales.getSelectionModel().getSelectedItem()).getValorLocal()));
            txtValorAdministracion.setText(String.valueOf(((Locales)tblLocales.getSelectionModel().getSelectedItem()).getValorAdministracion()));
        } catch (Exception e) { 
            
        }
        
    }
    
    private void agregarLocales() {
        Locales registro = new Locales();
        if(txtSaldoFavor.getText().trim().equals("")){
        registro.setSaldoFavor(new BigDecimal("0"));
        }else{
                registro.setSaldoFavor(new BigDecimal(txtSaldoFavor.getText()));

        
        }
        if(txtSaldoContra.getText().trim().equals("")){
        registro.setSaldoContra(new BigDecimal("0"));
        }else{
                registro.setSaldoContra(new BigDecimal(txtSaldoContra.getText()));

        
        }

        /*if(txtMesesPendientes.getText().trim().equals("")){
        registro.setMesesPendientes(Integer.parseInt("0"));
        }else{
        registro.setMesesPendientes(Integer.parseInt(txtMesesPendientes.getText()));
        }*/

        registro.setDisponibilidad(chDisponibilidad.isSelected());
        registro.setValorLocal(new BigDecimal(txtValorLocal.getText()));
        registro.setValorAdministracion(new BigDecimal(txtValorAdministracion.getText()));
        try {
            PreparedStatement pstmt;
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarLocales(?,?,?,?,?,?)}");
            pstmt.setBigDecimal(1, registro.getSaldoFavor());
            pstmt.setBigDecimal(2, registro.getSaldoContra());
            pstmt.setNull(3, registro.getMesesPendientes());
            pstmt.setBoolean(4, registro.isDisponibilidad());
            pstmt.setBigDecimal(5, registro.getValorLocal());
            pstmt.setBigDecimal(6, registro.getValorAdministracion());
            pstmt.execute();
            cargarDatos();
            limpiarCampos();
            deshabilitarCampos();
            listaLocales.add(registro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void editarLocales() {
        Locales registro = (Locales) tblLocales.getSelectionModel().getSelectedItem();
        registro.setId(Integer.parseInt(txtId.getText()));
        if(txtSaldoFavor.getText().trim().equals("")){
        registro.setSaldoFavor(new BigDecimal("0"));
        }else{
                registro.setSaldoFavor(new BigDecimal(txtSaldoFavor.getText()));

        
        }
        if(txtSaldoContra.getText().trim().equals("")){
        registro.setSaldoContra(new BigDecimal("0"));
        }else{
                registro.setSaldoContra(new BigDecimal(txtSaldoContra.getText()));

        
        }
        /*if(txtMesesPendientes.getText().trim().equals("")){
        registro.setMesesPendientes(Integer.parseInt("0"));
        }else{
        registro.setMesesPendientes(Integer.parseInt(txtMesesPendientes.getText()));
        }*/
        registro.setDisponibilidad(chDisponibilidad.isSelected());
        registro.setValorLocal(new BigDecimal(txtValorLocal.getText()));
        registro.setValorAdministracion(new BigDecimal(txtValorAdministracion.getText()));
        try {
            PreparedStatement pstmt;
            pstmt = pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarLocales(?,?,?,?,?,?,?)}");
            pstmt.setInt(1, registro.getId());
            pstmt.setBigDecimal(2, registro.getSaldoFavor());
            pstmt.setBigDecimal(3, registro.getSaldoContra());
            pstmt.setNull(4, registro.getMesesPendientes());
            pstmt.setBoolean(5, registro.isDisponibilidad());
            pstmt.setBigDecimal(6, registro.getValorLocal());
            pstmt.setBigDecimal(7, registro.getValorAdministracion());
            pstmt.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    private void eliminarLocales() {
        try {
            PreparedStatement pstmt;
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarLocales(?)}");
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
                                alert.setContentText("Este dato no puede ser eliminado, este local esta asociado en cuentas por cobrar \n id:" + txtId.getText());
                                alert.show();
        }
    }
    
    public boolean validarDinero(String numero){
        
        Pattern pattern1 = Pattern.compile("[0-9]$");
        Matcher matcher1 = pattern1.matcher(numero);
        return matcher1.matches();
    }
    
    @FXML
    public void calcularSaldoLiquido(){
        BigDecimal favor = new BigDecimal(txtSaldoFavor.getText());
        BigDecimal contra = new BigDecimal(txtSaldoContra.getText());
        txtSaldoLiquido.setText(String.valueOf(favor.subtract(contra)));
    }
    
    public void limpiarCampos(){
        txtId.clear();
        txtSaldoFavor.clear();
        txtSaldoContra.clear();
        txtMesesPendientes.clear();
        txtValorLocal.clear();
        txtValorAdministracion.clear();
        txtSaldoLiquido.clear();
        chDisponibilidad.setSelected(false);
    }
    
    public void deshabilitarCampos(){
        txtId.setEditable(false);
        txtSaldoFavor.setEditable(false);
        txtSaldoContra.setEditable(false);
        txtMesesPendientes.setEditable(false);
        txtValorLocal.setEditable(false);
        txtValorAdministracion.setEditable(false);
        chDisponibilidad.setDisable(true);
        txtSaldoLiquido.setEditable(false);
    }
    public void habilitarCampos(){
        txtId.setEditable(false);
        txtSaldoFavor.setEditable(true);
        txtSaldoContra.setEditable(true);
        txtMesesPendientes.setEditable(false);
        txtValorLocal.setEditable(true);
        txtValorAdministracion.setEditable(true);
        chDisponibilidad.setDisable(false);
    }
    
    public boolean validarNumeroReal(String numero){
        String patron = "^[0-9]{1,8}([.][0-9]{2})?";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(numero);
        
        return matcher.matches();
    }
    
    

    @FXML
    private void nuevo(ActionEvent event) {
        switch(operacion){
            case NINGUNO:
                habilitarCampos();
                limpiarCampos();
                operacion = LocalesController.Operaciones.GUARDAR;
                btnNuevo.setText("Guardar");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_save_50px.png"));
                btnModificar.setText("Cancelar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/cancelar.png"));
                btnEliminar.setDisable(true);
                btnReporte.setDisable(true);
                break;
            case GUARDAR:
                if(txtValorLocal.getText().trim().equals("") || txtValorAdministracion.getText().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    if (validarNumeroReal(txtValorLocal.getText()) ) {

                                if (validarNumeroReal(txtValorAdministracion.getText())) {
                                            agregarLocales();
                                            cargarDatos();
                                            limpiarCampos();
                                            deshabilitarCampos();
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
                                alert.setHeaderText("Información");
                                alert.setContentText("El numero ingresado en Valor administración no es valido, solo se permiten valores numericos " + "\n" + "\nEjemplo:" + "\n100 o 100.00");
                                alert.show();
                                }

                    }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("El numero ingresado en Valor local no es valido, solo se permiten valores numericos " + "\n" + "\nEjemplo:" + "\n100 o 100.00");
                                alert.show();
                    }
                    
                }


                
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
                    operacion = LocalesController.Operaciones.ACTUALIZAR;
                }else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText("Advertencia");
                    alert.setContentText("Para modificar tiene que seleccionar un registro");
                    alert.show();
                }
                break;

            case ACTUALIZAR:
                if(txtValorLocal.getText().trim().equals("") || txtValorAdministracion.getText().trim().equals("")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    if (validarNumeroReal(txtValorLocal.getText()) ) {
   
                                if (validarNumeroReal(txtValorAdministracion.getText())) {
                                    editarLocales();
                                    cargarDatos();
                                    limpiarCampos();
                                    deshabilitarCampos();
                                    btnModificar.setText("Modificar");
                                    imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                                    btnEliminar.setText("Elminar");
                                    imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                                    btnNuevo.setDisable(false);
                                    btnReporte.setDisable(false);
                                    operacion = LocalesController.Operaciones.NINGUNO; 
                                }else{
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("El numero ingresado en Valor administración no es valido, solo se permiten valores numericos " + "\n" + "\nEjemplo:" + "\n100 o 100.00");
                                alert.show();
                                }
                            
                        
                    }else{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("El numero ingresado en Valor  local no es valido, solo se permiten valores numericos " + "\n" + "\nEjemplo:" + "\n100 o 100.00");
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
                operacion = LocalesController.Operaciones.NINGUNO;
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
                operacion = LocalesController.Operaciones.NINGUNO;
                limpiarCampos();
                deshabilitarCampos();
                break;
            case NINGUNO:
                if(existeElementoSeleccionado()){
                    
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText(null);
                    alert.setContentText("¿Esta seguro que desea eliminar este registro?"+"\n -----Datos a eliminar -----"
                            +"\n id: " + txtId.getText() + "\n Saldo a favor: " + txtSaldoFavor.getText() +
                            "\n Saldo en contra: " + txtSaldoContra.getText() +
                            "\n Meses pendientes: " + txtMesesPendientes.getText() +
                            "\n Valor del local: " + txtValorLocal.getText() + 
                            "\n Valor de administración: " + txtValorAdministracion.getText());
                    
                    Optional<ButtonType> respuesta = alert.showAndWait();
                    
                    if(respuesta.get() == ButtonType.OK){
                        eliminarLocales();
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
        
       GenerarReporte.getInstance().mostrarReporte("ReporteLocales.jasper", parametros, "Reporte de locales");
    }

    @FXML
    private void regresar(MouseEvent event) {
        this.escenarioClientes.mostrarEscenaAdministracion();
    }
    

}
