/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.controller;

import com.jfoenix.controls.JFXDatePicker;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.diegopatzan.bean.Administracion;
import org.diegopatzan.bean.CuentasPorPagar;
import org.diegopatzan.bean.Proveedores;
import org.diegopatzan.db.Conexion;
import org.diegopatzan.report.GenerarReporte;
import org.diegopatzan.system.Principal;

/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 7/07/2021
 * @time 07:44:08
 */
public class CuentasPorPagarController implements Initializable{
    
    private Principal escenarioPrincipal;
    
    public boolean existeElementoSeleccionado() {
        return tblCuentasPorPagar.getSelectionModel().getSelectedItem() != null;
    }
    
    @FXML
    private JFXDatePicker dpFechaLimite;

    @FXML
    private void seleccionarElemento() {
        if(existeElementoSeleccionado()){
            txtId.setText(String.valueOf(((CuentasPorPagar)tblCuentasPorPagar.getSelectionModel().getSelectedItem()).getId()));
        txtNumeroFactura.setText(((CuentasPorPagar)tblCuentasPorPagar.getSelectionModel().getSelectedItem()).getNumeroFactura());
        dpFechaLimite.setValue(((CuentasPorPagar)tblCuentasPorPagar.getSelectionModel().getSelectedItem()).getFechaLimitePago().toLocalDate());
        cmbEstado.getSelectionModel().select(((CuentasPorPagar)tblCuentasPorPagar.getSelectionModel().getSelectedItem()).getEstadoPago());
        cmbAdministracion.getSelectionModel().select(buscarAdministracion(((CuentasPorPagar)tblCuentasPorPagar.getSelectionModel().getSelectedItem()).getCodigoAdministracion()));
        cmbProveedores.getSelectionModel().select(buscarProveedores(((CuentasPorPagar)tblCuentasPorPagar.getSelectionModel().getSelectedItem()).getCodigoProveedor()));
        txtValor.setText(String.valueOf(((CuentasPorPagar)tblCuentasPorPagar.getSelectionModel().getSelectedItem()).getValorNetoPago()));
        }else{
        
    }
    }

    
    private enum Operaciones{
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO;
    }
    
    private Operaciones operacion = Operaciones.NINGUNO;
    
    private ObservableList<Administracion> listaAdministracion;
    private ObservableList<Proveedores> listaProveedores;
    private ObservableList<CuentasPorPagar> listaCuentasPorPagar;
    
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
    private TableView tblCuentasPorPagar;
    @FXML
    private ComboBox cmbAdministracion;
    @FXML
    private ComboBox cmbEstado;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colNumeroFactura;
    @FXML
    private TableColumn colFechaLimite;
    @FXML
    private TableColumn colEstado;
    @FXML
    private TableColumn colValor;
    @FXML
    private TableColumn colAdministracion;
    @FXML
    private TableColumn colProveedor;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtNumeroFactura;
    @FXML
    private TextField txtValor;
    @FXML
    private ComboBox cmbProveedores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dpFechaLimite.setEditable(false);
        cargarDatos();
        estadoPago();
    }
    
    public void estadoPago(){
        cmbEstado.getItems().addAll("Pagado");
        cmbEstado.getItems().addAll("Pendiente");
        cmbEstado.getItems().addAll("Retrasado");
    }
    
    public ObservableList<CuentasPorPagar> getCuentasPorPagar(){
        ArrayList<CuentasPorPagar> listado = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarCuentasPorPagar}");
            rs = pstmt.executeQuery();
            
            while(rs.next()){
                listado.add(new CuentasPorPagar(
                        rs.getInt("id"), 
                        rs.getString("numeroFactura"), 
                        rs.getDate("fechaLimitePago"), 
                        rs.getString("estadoPago"), 
                        rs.getBigDecimal("valorNetoPago"), 
                        rs.getInt("codigoAdministracion"), 
                        rs.getInt("codigoProveedor")
                )
                );
            }
            listaCuentasPorPagar =  FXCollections.observableArrayList(listado);
            
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
        return listaCuentasPorPagar;
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
            System.err.println("\n Se produjo un error al intentar consultar la tabla Administracion en la base de datos");
            e.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }
        listaAdministracion = FXCollections.observableArrayList(listado);
        return listaAdministracion;
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
        tblCuentasPorPagar.setItems(getCuentasPorPagar());
        colId.setCellValueFactory(new PropertyValueFactory<CuentasPorPagar, Integer>("id"));
        colNumeroFactura.setCellValueFactory(new PropertyValueFactory<CuentasPorPagar, String>("numeroFactura"));
        colFechaLimite.setCellValueFactory(new PropertyValueFactory<CuentasPorPagar, Date>("fechaLimitePago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<CuentasPorPagar, String>("estadoPago"));
        colValor.setCellValueFactory(new PropertyValueFactory<CuentasPorPagar, BigDecimal>("valorNetoPago"));
        colAdministracion.setCellValueFactory(new PropertyValueFactory<CuentasPorPagar, Integer>("codigoAdministracion"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<CuentasPorPagar, Integer>("codigoProveedor"));

        cmbAdministracion.setItems(getAdministracion());
        cmbProveedores.setItems(getProveedores());

    }
    
    public Administracion buscarAdministracion(int id){
        Administracion registro = null;
        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarAdministracion(?)}");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                registro = new Administracion(
                        rs.getInt("id"), 
                        rs.getString("direccion"), 
                        rs.getString("telefono"));
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registro;
    }
    
    public Proveedores buscarProveedores(int id){
        Proveedores registro = null;
        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarProveedores(?)}");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                registro = new Proveedores(rs.getInt("id"), 
                        rs.getString("nit"), 
                        rs.getString("servicioPrestado"), 
                        rs.getString("telefono"), 
                        rs.getString("direccion"), 
                        rs.getBigDecimal("saldoFavor"), 
                        rs.getBigDecimal("saldoContra"));
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registro;
    }
    
    public void agregarCuentasPorPagar() {
        CuentasPorPagar cuentaPagar = new CuentasPorPagar();
        cuentaPagar.setNumeroFactura(txtNumeroFactura.getText());
        cuentaPagar.setFechaLimitePago(Date.valueOf(dpFechaLimite.getValue()));
        cuentaPagar.setEstadoPago((String)cmbEstado.getSelectionModel().getSelectedItem());
        cuentaPagar.setValorNetoPago(new BigDecimal(txtValor.getText()));
        cuentaPagar.setCodigoAdministracion(((Administracion)cmbAdministracion.getSelectionModel().getSelectedItem()).getId());
        cuentaPagar.setCodigoProveedor(((Proveedores)cmbProveedores.getSelectionModel().getSelectedItem()).getId());
        
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarCuentasPorPagar(?, ?, ?, ?, ?, ?)}");
            pstmt.setString(1, cuentaPagar.getNumeroFactura());
            pstmt.setDate(2, cuentaPagar.getFechaLimitePago());
            pstmt.setString(3, cuentaPagar.getEstadoPago());
            pstmt.setBigDecimal(4, cuentaPagar.getValorNetoPago());
            pstmt.setInt(5, cuentaPagar.getCodigoAdministracion());
            pstmt.setInt(6, cuentaPagar.getCodigoProveedor());

            

            
            pstmt.execute();
            listaCuentasPorPagar.add(cuentaPagar);
            
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar agregar un nueva Cuenta por cobrar");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void editarCuentasPorPagar() {
        CuentasPorPagar cuentaPagar = new CuentasPorPagar();
        cuentaPagar.setId(Integer.parseInt(txtId.getText()));
        cuentaPagar.setNumeroFactura(txtNumeroFactura.getText());
        cuentaPagar.setFechaLimitePago(Date.valueOf(dpFechaLimite.getValue()));
        cuentaPagar.setEstadoPago((String)cmbEstado.getSelectionModel().getSelectedItem());
        cuentaPagar.setValorNetoPago(new BigDecimal(txtValor.getText()));
        cuentaPagar.setCodigoAdministracion(((Administracion)cmbAdministracion.getSelectionModel().getSelectedItem()).getId());
        cuentaPagar.setCodigoProveedor(((Proveedores)cmbProveedores.getSelectionModel().getSelectedItem()).getId());
        
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarCuentasPorPagar(?, ?, ?, ?, ?, ?, ?)}");
            
            pstmt.setInt(1, cuentaPagar.getId());
            pstmt.setString(2, cuentaPagar.getNumeroFactura());
            pstmt.setDate(3, cuentaPagar.getFechaLimitePago());
            pstmt.setString(4, cuentaPagar.getEstadoPago());
            pstmt.setBigDecimal(5, cuentaPagar.getValorNetoPago());
            pstmt.setInt(6, cuentaPagar.getCodigoAdministracion());
            pstmt.setInt(7, cuentaPagar.getCodigoProveedor());
            

            
            pstmt.execute();
            
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar editar una Cuenta por cobrar");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }        
    }
    
    public void eliminarCuentasPorPagar() {
        if (existeElementoSeleccionado()) {
            CuentasPorPagar cuentaPagar = (CuentasPorPagar) tblCuentasPorPagar.getSelectionModel().getSelectedItem();
            

            
            PreparedStatement pstmt = null;
            
            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarCuentasPorPagar(?)}");
                pstmt.setInt(1, cuentaPagar.getId());
                

                
                pstmt.execute();
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();
                
            } catch (SQLException e) {
                System.err.println("\nSe produjo un error al intentar eliminar el registro con el id " + cuentaPagar.getId());
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
        
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    
    public void habilitarCampos(){
        txtId.setEditable(false);
        txtNumeroFactura.setEditable(true);
        txtValor.setEditable(true);
        cmbAdministracion.setDisable(false);
        cmbEstado.setDisable(false);
        cmbProveedores.setDisable(false);
        dpFechaLimite.setDisable(false);
    }
    
    public void limpiarCampos(){
        txtId.clear();
        txtNumeroFactura.clear();
        txtValor.clear();
        cmbAdministracion.valueProperty().set(null);
        cmbEstado.valueProperty().set("Estado");
        cmbProveedores.valueProperty().set(null);
        dpFechaLimite.getEditor().clear();
    }
    
    public void deshabilitarCampos(){
        txtId.setEditable(false);
        txtNumeroFactura.setEditable(false);
        txtValor.setEditable(false);
        cmbAdministracion.setDisable(true);
        cmbEstado.setDisable(true);
        cmbProveedores.setDisable(true);
        dpFechaLimite.setDisable(true);
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
                operacion = CuentasPorPagarController.Operaciones.GUARDAR;
                btnNuevo.setText("Guardar");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_save_50px.png"));
                btnModificar.setText("Cancelar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/cancelar.png"));
                btnEliminar.setDisable(true);
                btnReporte.setDisable(true);
                break;
            case GUARDAR:
                if(txtNumeroFactura.getText().trim().equals("") || txtValor.getText().trim().equals("") || dpFechaLimite.getValue() == null || cmbAdministracion.getSelectionModel().getSelectedItem() == null || cmbEstado.getSelectionModel().getSelectedItem() == null || cmbProveedores.getSelectionModel().getSelectedItem() == null){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    if (validarNumeroReal(txtValor.getText())) {
                    agregarCuentasPorPagar();
                    cargarDatos();
                    deshabilitarCampos();
                    limpiarCampos();
                    btnNuevo.setText("Nuevo");
                    imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                    btnModificar.setText("Modificar");
                    imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                    btnEliminar.setDisable(false);
                    btnReporte.setDisable(false);
                    operacion = CuentasPorPagarController.Operaciones.NINGUNO;  
                    }else{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("El numero ingresado en valor no es valido, solo se permiten valores numericos " + "\n" + "\nEjemplo:" + "\n100 o 100.00");
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
                if(txtNumeroFactura.getText().trim().equals("") || txtValor.getText().trim().equals("")){
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
                    operacion = CuentasPorPagarController.Operaciones.ACTUALIZAR;
                }
                break;
            case ACTUALIZAR:
                if (txtNumeroFactura.getText().trim().equals("") || txtValor.getText().trim().equals("") || dpFechaLimite.getValue() == null || cmbAdministracion.getSelectionModel().getSelectedItem() == null || cmbEstado.getSelectionModel().getSelectedItem() == null || cmbProveedores.getSelectionModel().getSelectedItem() == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    if (validarNumeroReal(txtValor.getText())) {
                       editarCuentasPorPagar();
                        cargarDatos();
                        limpiarCampos();
                        deshabilitarCampos();
                        imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                        btnModificar.setText("Modificar");
                        btnEliminar.setText("Elminar");
                        imgEliminar.setImage(new Image("/org/diegopatzan/resource/images/icons8_empty_trash_32px.png"));
                         btnNuevo.setDisable(false);
                        btnReporte.setDisable(false);
                        operacion = CuentasPorPagarController.Operaciones.NINGUNO; 
                    }else{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("El numero ingresado en valor no es valido, solo se permiten valores numericos " + "\n" + "\nEjemplo:" + "\n100 o 100.00");
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
                operacion = CuentasPorPagarController.Operaciones.NINGUNO;
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
                operacion = CuentasPorPagarController.Operaciones.NINGUNO;
                limpiarCampos();
                deshabilitarCampos();
                break;
            case NINGUNO:
                if (txtNumeroFactura.getText().trim().equals("") || txtValor.getText().trim().equals("")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Para eliminar tiene que seleccionar un registro");
                                alert.show();
                }else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText(null);
                    alert.setContentText("Esta seguro que desea eliminar este registro?" + "\nID: " + txtId.getText() + "\nEstado: " + cmbEstado.getSelectionModel().getSelectedItem());
                    
                    Optional<ButtonType> respuesta = alert.showAndWait();
                    
                    if(respuesta.get() == ButtonType.OK){
                        eliminarCuentasPorPagar();
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
        
       GenerarReporte.getInstance().mostrarReporte("ReporteCuentasPorPagar.jasper", parametros, "Reporte de cuentas por pagar");
    }

    @FXML
    private void regresar(MouseEvent event) {
        this.escenarioPrincipal.mostrarEscenaProveedores();
    }

}
