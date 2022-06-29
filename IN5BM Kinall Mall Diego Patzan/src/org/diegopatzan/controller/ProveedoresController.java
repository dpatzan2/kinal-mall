/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.swing.JOptionPane;
import org.diegopatzan.bean.CuentasPorCobrar;
import org.diegopatzan.bean.Proveedores;
import org.diegopatzan.db.Conexion;
import org.diegopatzan.report.GenerarReporte;
import org.diegopatzan.system.Principal;

/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 6/07/2021
 * @time 08:52:21
 */
public class ProveedoresController implements Initializable{

    private Principal escenarioPrincipal;
    
    private ObservableList<Proveedores> listaProveedores;
    @FXML
    private TextField txtSaldoLiquido;
    
    public boolean existeElementoSeleccionado() {
        return tblProveedores.getSelectionModel().getSelectedItem() != null;
    }

    @FXML
    private void seleccionarElemento() {
        try {
            txtId.setText(String.valueOf(((Proveedores)tblProveedores.getSelectionModel().getSelectedItem()).getId()));
            txtNit.setText(((Proveedores) tblProveedores.getSelectionModel().getSelectedItem()).getNit());
            txtServicio.setText(((Proveedores) tblProveedores.getSelectionModel().getSelectedItem()).getServicioPrestado());
            txtTelefono.setText(((Proveedores) tblProveedores.getSelectionModel().getSelectedItem()).getTelefono());
            txtDireccion.setText(((Proveedores) tblProveedores.getSelectionModel().getSelectedItem()).getDireccion());
            txtSaldoFavor.setText(String.valueOf(((Proveedores)tblProveedores.getSelectionModel().getSelectedItem()).getSaldoFavor()));
            txtSaldoContra.setText(String.valueOf(((Proveedores)tblProveedores.getSelectionModel().getSelectedItem()).getSaldoContra()));
            BigDecimal favor = ((Proveedores)tblProveedores.getSelectionModel().getSelectedItem()).getSaldoFavor();
            BigDecimal contra = ((Proveedores)tblProveedores.getSelectionModel().getSelectedItem()).getSaldoContra();
            txtSaldoLiquido.setText(String.valueOf(favor.subtract(contra)));
        } catch (Exception e) {
        }
    }

    @FXML
    private void mostrarEscenaCuentasPorPagar(ActionEvent event) {
        this.escenarioPrincipal.mostrarEscenaCuentasPorPagar();
    }

    @FXML
    private void calcularSaldoLiquido() {
        BigDecimal favor = new BigDecimal(txtSaldoFavor.getText());
        BigDecimal contra = new BigDecimal(txtSaldoContra.getText());
        txtSaldoLiquido.setText(String.valueOf(favor.subtract(contra)));
    }

    
    private enum Operaciones{
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO;
    }
    
    private Operaciones operacion = Operaciones.NINGUNO;
    
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
    private TableView tblProveedores;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colNit;
    @FXML
    private TableColumn colServicio;
    @FXML
    private TableColumn colTelefono;
    @FXML
    private TableColumn colDireccion;
    @FXML
    private TableColumn colSaldoFavor;
    @FXML
    private TableColumn colSaldoContra;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtServicio;
    @FXML
    private TextField txtNit;
    @FXML
    private TextField txtDireccion;
    @FXML
    private TextField txtSaldoContra;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtSaldoFavor;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
    }
    
    public ObservableList<Proveedores> getProveedores(){
        ArrayList<Proveedores> listado = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarProveedores}");
            rs = pstmt.executeQuery();
            
            while(rs.next()){
                listado.add(new Proveedores(
                        rs.getInt("id"), 
                        rs.getString("nit"), 
                        rs.getString("servicioPrestado"), 
                        rs.getString("telefono"), 
                        rs.getString("direccion"), 
                        rs.getBigDecimal("saldoFavor"), 
                        rs.getBigDecimal("saldoContra")
                )
                );
            }
            listaProveedores =  FXCollections.observableArrayList(listado);
            
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
        return listaProveedores;
    }
    
    public void cargarDatos(){
        tblProveedores.setItems(getProveedores());
        colId.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, Integer>("id"));
        colNit.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, String>("nit"));
        colServicio.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, String>("servicioPrestado"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, String>("telefono"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, String>("direccion"));
        colSaldoFavor.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, BigDecimal>("saldoFavor"));
        colSaldoContra.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, BigDecimal>("saldoContra"));
    }
    
    public void agregarProveedores(){
        Proveedores proveedor = new Proveedores();
        String nit = txtNit.getText();
        if (nit.contains("-")){
            
            nit = nit.replace("-", "");
            proveedor.setNit(nit);
        }else{
            proveedor.setNit(txtNit.getText());
        }
        proveedor.setServicioPrestado(txtServicio.getText());
        proveedor.setTelefono(txtTelefono.getText());
        proveedor.setDireccion(txtDireccion.getText());
        proveedor.setSaldoFavor(new BigDecimal(txtSaldoFavor.getText()));
        proveedor.setSaldoContra(new BigDecimal(txtSaldoContra.getText()));
        
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarProveedores(?, ?, ?, ?, ?, ?)}");
            pstmt.setString(1, proveedor.getNit());
            pstmt.setString(2, proveedor.getServicioPrestado());
            pstmt.setString(3, proveedor.getTelefono());
            pstmt.setString(4, proveedor.getDireccion());
            pstmt.setBigDecimal(5, proveedor.getSaldoFavor());
            pstmt.setBigDecimal(6, proveedor.getSaldoContra());
            
            pstmt.execute();
            listaProveedores.add(proveedor);
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar agregar un nueva Cuenta por cobrar");
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                pstmt.close();
            } catch (Exception e) {
            }
        }
    }
    
    public void editarProveedores(){
        Proveedores proveedor = new Proveedores();
        proveedor.setId(Integer.parseInt(txtId.getText()));
        String nit =  txtNit.getText();
        if (nit.contains("-")) {
            nit = nit.replace("-", "");
            proveedor.setNit(nit);
        }else{
            proveedor.setNit(txtNit.getText());
        }
        proveedor.setServicioPrestado(txtServicio.getText());
        proveedor.setTelefono(txtTelefono.getText());
        proveedor.setDireccion(txtDireccion.getText());
        proveedor.setSaldoFavor(new BigDecimal(txtSaldoFavor.getText()));
        proveedor.setSaldoContra(new BigDecimal(txtSaldoContra.getText()));
        
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarProveedores(?, ?, ?, ?, ?, ?, ?)}");
            pstmt.setInt(1, proveedor.getId());
            pstmt.setString(2, proveedor.getNit());
            pstmt.setString(3, proveedor.getServicioPrestado());
            pstmt.setString(4, proveedor.getTelefono());
            pstmt.setString(5, proveedor.getDireccion());
            pstmt.setBigDecimal(6, proveedor.getSaldoFavor());
            pstmt.setBigDecimal(7, proveedor.getSaldoContra());
            
            pstmt.execute();
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar editar un proveedor");
            e.printStackTrace();
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
    public void eliminarProveedores() {
        if (existeElementoSeleccionado()) {
            Proveedores proveedor = (Proveedores) tblProveedores.getSelectionModel().getSelectedItem();
            

            
            PreparedStatement pstmt = null;
            
            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarProveedores(?)}");
                pstmt.setInt(1, proveedor.getId());
                
                
                pstmt.execute();
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();
                
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Error");
                                alert.setContentText("Este dato no puede ser eliminado, este proveedor esta asociado en cuentas por pagar\n id:" + txtId.getText());
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
        
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void limpiarCampos(){
        txtId.clear();
        txtNit.clear();
        txtServicio.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtSaldoFavor.clear();
        txtSaldoContra.clear();
        txtSaldoLiquido.clear();
    }
    
    public void deshabilitarCampos(){
        txtId.setEditable(false);
        txtNit.setEditable(false);
        txtServicio.setEditable(false);
        txtTelefono.setEditable(false);
        txtDireccion.setEditable(false);
        txtSaldoFavor.setEditable(false);
        txtSaldoContra.setEditable(false);
        txtSaldoLiquido.setEditable(false);
    }
    
    public void habilitarCampos(){
        txtId.setEditable(false);
        txtNit.setEditable(true);
        txtServicio.setEditable(true);
        txtTelefono.setEditable(true);
        txtDireccion.setEditable(true);
        txtSaldoFavor.setEditable(true);
        txtSaldoContra.setEditable(true);
    }
    
    public boolean validarNit(String nit){
        
        String patron = "^[0-9]{6,}[-]?[0-9]{1}$";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(nit);
        return matcher.matches();
    }
    
    public boolean validarTelefono(String numero){
        
        Pattern pattern1 = Pattern.compile("^[0-9]{8}$");
        Matcher matcher1 = pattern1.matcher(numero);
        return matcher1.matches();
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
                operacion = ProveedoresController.Operaciones.GUARDAR;
                btnNuevo.setText("Guardar");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_save_50px.png"));
                btnModificar.setText("Cancelar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/cancelar.png"));
                btnEliminar.setDisable(true);
                btnReporte.setDisable(true);
                break;
            case GUARDAR:
                if(txtNit.getText().trim().equals("") || txtServicio.getText().trim().equals("") || txtTelefono.getText().trim().equals("") || txtDireccion.getText().trim().equals("") || txtSaldoFavor.getText().trim().equals("") || txtSaldoContra.getText().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    
                    if (validarNit(txtNit.getText())) {
                        if (validarTelefono(txtTelefono.getText())) {
                            if (validarNumeroReal(txtSaldoFavor.getText())) {
                                if (validarNumeroReal(txtSaldoContra.getText())) {
                                        agregarProveedores();
                                        cargarDatos();
                                        deshabilitarCampos();
                                        limpiarCampos();
                                        btnNuevo.setText("Nuevo");
                                        imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                                        btnModificar.setText("Modificar");
                                        imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                                        btnEliminar.setDisable(false);
                                        btnReporte.setDisable(false);
                                        operacion = ProveedoresController.Operaciones.NINGUNO;  
                                }else{
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("El numero ingresado en saldo en contra no es valido, solo se permiten valores numericos " + "\n" + "\nEjemplo:" + "\n100 o 100.00");
                                alert.show();
                                }
                                
                            }else{
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("El numero ingresado en saldo a favor no es valido, solo se permiten valores numericos " + "\n" + "\nEjemplo:" + "\n100 o 100.00");
                                alert.show();
                            
                            }
                        
                        }else{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Numero de telefono invalido \nEjemplo: \n11111111 \n(8 Digitos)" );
                                alert.show();
                        }
                        
                    }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Nit invalido \n Ejemplo: \n 879546-1 o 7894651");
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
                if(txtNit.getText().trim().equals("") || txtNit.getText().trim().equals("") || txtServicio.getText().trim().equals("") || txtTelefono.getText().trim().equals("") || txtDireccion.getText().trim().equals("") || txtSaldoFavor.getText().trim().equals("") || txtSaldoContra.getText().trim().equals("")){
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
                    operacion = ProveedoresController.Operaciones.ACTUALIZAR;
                }
                break;
            case ACTUALIZAR:
                if (txtNit.getText().trim().equals("") || txtServicio.getText().trim().equals("") || txtTelefono.getText().trim().equals("") || txtDireccion.getText().trim().equals("") || txtSaldoFavor.getText().trim().equals("") || txtSaldoContra.getText().trim().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    if (validarNit(txtNit.getText())) {
                        if (validarTelefono(txtTelefono.getText())) {
                            if (validarNumeroReal(txtSaldoFavor.getText())) {
                                if (validarNumeroReal(txtSaldoContra.getText())) {
                                   editarProveedores();
                                    cargarDatos();
                                    limpiarCampos();
                                    deshabilitarCampos();
                                    imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                                    btnModificar.setText("Modificar");
                                    btnEliminar.setText("Elminar");
                                    imgEliminar.setImage(new Image("/org/diegopatzan/resource/images/icons8_empty_trash_32px.png"));
                                    btnNuevo.setDisable(false);
                                    btnReporte.setDisable(false);
                                    operacion = ProveedoresController.Operaciones.NINGUNO;  
                                }else{
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("El numero ingresado en saldo en contra no es valido, solo se permiten valores numericos " + "\n" + "\nEjemplo:" + "\n100 o 100.00");
                                alert.show();
                                }
                                
                            }else{
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("El numero ingresado en saldo a favor no es valido, solo se permiten valores numericos " + "\n" + "\nEjemplo:" + "\n100 o 100.00");
                                alert.show();
                            }
                           
                        }else{
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Numero de telefono invalido \nEjemplo: \n11111111 \n(8 Digitos)" );
                                alert.show();
                        
                        }
                        
                    }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Nit invalido \n Ejemplo: \n 879546-1 o 7894651");
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
                operacion = ProveedoresController.Operaciones.NINGUNO;
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
                operacion = ProveedoresController.Operaciones.NINGUNO;
                limpiarCampos();
                deshabilitarCampos();
                break;
            case NINGUNO:
                if (txtNit.getText().trim().equals("") || txtNit.getText().trim().equals("") || txtServicio.getText().trim().equals("") || txtTelefono.getText().trim().equals("") || txtDireccion.getText().trim().equals("") || txtSaldoFavor.getText().trim().equals("") || txtSaldoContra.getText().trim().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Para eliminar tiene que seleccionar un registro");
                                alert.show();
                }else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText(null);
                    alert.setContentText("Esta seguro que desea eliminar este registro?" + "\nID: " + txtId.getText() + "\nNit: " + txtNit.getText() + "\nServicio: " + txtServicio.getText());
                    
                    Optional<ButtonType> respuesta = alert.showAndWait();
                    
                    if(respuesta.get() == ButtonType.OK){
                        eliminarProveedores();
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
        
       GenerarReporte.getInstance().mostrarReporte("ReporteProveedores.jasper", parametros, "Reporte de proveedores");
    }

    @FXML
    private void regresar(MouseEvent event) {
        this.escenarioPrincipal.menuPrincipal();
    }

}
