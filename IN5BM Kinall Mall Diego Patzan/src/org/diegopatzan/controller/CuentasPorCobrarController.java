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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.swing.JOptionPane;
import org.diegopatzan.bean.Administracion;
import org.diegopatzan.bean.Clientes;
import org.diegopatzan.bean.CuentasPorCobrar;
import org.diegopatzan.bean.Locales;
import org.diegopatzan.db.Conexion;
import org.diegopatzan.report.GenerarReporte;
import org.diegopatzan.system.Principal;

/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 30/06/2021
 * @time 10:12:29
 */
public class CuentasPorCobrarController implements Initializable{
    
    private Principal escenarioPrincipal;
    @FXML
    private ComboBox cmbEstado;
    
    private enum Operaciones{
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO;
    }
    
    private Operaciones operacion = Operaciones.NINGUNO;
    
    private ObservableList<CuentasPorCobrar> listaCuentasPorCobrar;
    private ObservableList<Clientes> listaClienntes;
    private ObservableList<Locales> listaLocales;
    private ObservableList<Administracion> listaAdministracion;
    
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
    private TableView tblCuentasPorCobrar;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colFactura;
    @FXML
    private TableColumn colAnio;
    @FXML
    private TableColumn colMes;
    @FXML
    private TableColumn colValor;
    @FXML
    private TableColumn colEstado;
    @FXML
    private TableColumn colCodigoAdministracion;
    @FXML
    private TableColumn colCodigoCliente;
    @FXML
    private TableColumn colCodigoLocal;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtNumeroFactura;
    @FXML
    private Spinner<Integer> spnAnio;
    
    private SpinnerValueFactory<Integer> valueFactoryAnio;
    @FXML
    private TextField txtValorNetoPago;
    @FXML
    private ComboBox cmbAdministracion;
    @FXML
    private ComboBox cmbCliente;
    @FXML
    private ComboBox cmbLocal;
    
    @FXML
    private Spinner<Integer> spnMes;
    
    private SpinnerValueFactory<Integer> valueFactoryMes;
    
    public boolean existeElementoSeleccionado() {
        return tblCuentasPorCobrar.getSelectionModel().getSelectedItem() != null;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        valueFactoryAnio = new SpinnerValueFactory.IntegerSpinnerValueFactory(2020, 2050, 2021);
        valueFactoryMes = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 6);
        spnAnio.setValueFactory(valueFactoryAnio);
        spnMes.setValueFactory(valueFactoryMes);
        cargarDatos();
        estadoPago();
    }

    public ObservableList<CuentasPorCobrar> getCuentasPorCobrar(){
        ArrayList<CuentasPorCobrar> listado = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarCuentasPorCobrar}");
            rs = pstmt.executeQuery();
            
            while(rs.next()){
                listado.add(new CuentasPorCobrar(rs.getInt("id"), 
                        rs.getString("numeroFactura"), 
                        rs.getInt("anio"), 
                        rs.getInt("mes"), 
                        rs.getBigDecimal("valorNetoPago"), 
                        rs.getString("estadoPago"), 
                        rs.getInt("codigoAdministracion"), 
                        rs.getInt("codigoCliente"), 
                        rs.getInt("codigoLocal")));
            }
            listaCuentasPorCobrar =  FXCollections.observableArrayList(listado);
            
        } catch (SQLException e) {
            System.err.println("\n Se produjo un error al intentar consultar la tabla CuentasPorCobrar en la base de datos");
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
        return listaCuentasPorCobrar;
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
    
    public ObservableList<Locales> getLocales(){
        ArrayList<Locales> lista = new ArrayList<>();
        try{
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarLocales}");
            ResultSet rs = pstmt.executeQuery();
            
            while(rs.next()){
                Locales registro = new Locales();
                registro.setId(rs.getInt("id"));
                registro.setSaldoFavor(rs.getBigDecimal("saldoFavor"));
                registro.setSaldoContra(rs.getBigDecimal("saldoContra"));
                registro.setMesesPendientes(rs.getInt("mesesPendientes"));
                registro.setDisponibilidad(rs.getBoolean("disponibilidad"));
                registro.setValorLocal(rs.getBigDecimal("valorLocal"));
                registro.setValorAdministracion(rs.getBigDecimal("valorAdministracion"));
                
                lista.add(registro);
            }
            rs.close();
            pstmt.close();
        }catch(SQLException e){
            System.err.println("\n Se produjo un error al intentar consultar la tabla Locales en la base de datos");
            e.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }
        listaLocales = FXCollections.observableArrayList(lista);
        return listaLocales;
    }
    
    public ObservableList<Clientes> getClientes(){
        ArrayList<Clientes> lista = new ArrayList<>();
        try{
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarClientes}");
            ResultSet rs = pstmt.executeQuery();
            
            while(rs.next()){
                lista.add(new Clientes(
                        rs.getInt("id"), 
                        rs.getString("nombres"), 
                        rs.getString("apellidos"), 
                        rs.getString("telefono"), 
                        rs.getString("direccion"), 
                        rs.getString("email"), 
                        rs.getInt("codigoTipoCliente")
                    )
                );
                
                System.out.println("");
            }
            rs.close();
            pstmt.close();
        }catch(SQLException e){
            System.err.println("\n Se produjo un error al intentar consultar la tabla Clientes en la base de datos");
            e.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }
        listaClienntes = FXCollections.observableArrayList(lista);
        return listaClienntes;
    }
    
    
    public void cargarDatos(){
        tblCuentasPorCobrar.setItems(getCuentasPorCobrar());
        colId.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, Integer>("id"));
        colFactura.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, String>("numeroFactura"));
        colAnio.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, Integer>("anio"));
        colMes.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, Integer>("mes"));
        colValor.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, BigDecimal>("valorNetoPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, String>("estadoPago"));
        colCodigoAdministracion.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, Integer>("codigoAdministracion"));
        colCodigoCliente.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, Integer>("codigoCliente"));
        colCodigoLocal.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, Integer>("codigoLocal"));
        
        
        cmbAdministracion.setItems(getAdministracion());
        cmbCliente.setItems(getClientes());
        cmbLocal.setItems(getLocales());
    }
    
    public void agregarCuentasPorCobrar() {
        CuentasPorCobrar cuentaCobrar = new CuentasPorCobrar();
        cuentaCobrar.setNumeroFactura(txtNumeroFactura.getText());
        cuentaCobrar.setAnio(spnAnio.getValue());
        cuentaCobrar.setMes(spnMes.getValue());
        cuentaCobrar.setValorNetoPago(new BigDecimal(txtValorNetoPago.getText()));
        cuentaCobrar.setEstadoPago((String) cmbEstado.getSelectionModel().getSelectedItem());
        cuentaCobrar.setCodigoAdministracion(  ((Administracion) cmbAdministracion.getSelectionModel().getSelectedItem()).getId());
        cuentaCobrar.setCodigoCliente( ((Clientes) cmbCliente.getSelectionModel().getSelectedItem()).getId());
        cuentaCobrar.setCodigoLocal( ((Locales) cmbLocal.getSelectionModel().getSelectedItem()).getId() );
        
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarCuentasPorCobrar(?, ?, ?, ?, ?, ?, ?, ?)}");
            pstmt.setString(1, cuentaCobrar.getNumeroFactura());
            pstmt.setInt(2, cuentaCobrar.getAnio());
            pstmt.setInt(3, cuentaCobrar.getMes());
            pstmt.setBigDecimal(4, cuentaCobrar.getValorNetoPago());
            pstmt.setString(5, cuentaCobrar.getEstadoPago());
            pstmt.setInt(6, cuentaCobrar.getCodigoAdministracion());
            pstmt.setInt(7, cuentaCobrar.getCodigoCliente());
            pstmt.setInt(8, cuentaCobrar.getCodigoLocal());
            

            
            pstmt.execute();
            listaCuentasPorCobrar.add(cuentaCobrar);
            
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
    
    public void editarCuentasPorCobrar() {
        CuentasPorCobrar cuentaCobrar = new CuentasPorCobrar();
        cuentaCobrar.setId(Integer.parseInt(txtId.getText()));
        cuentaCobrar.setNumeroFactura(txtNumeroFactura.getText());
        cuentaCobrar.setAnio(spnAnio.getValue());
        cuentaCobrar.setMes(spnMes.getValue());
        cuentaCobrar.setValorNetoPago(new BigDecimal(txtValorNetoPago.getText()));
        cuentaCobrar.setEstadoPago((String) cmbEstado.getSelectionModel().getSelectedItem());
        cuentaCobrar.setCodigoAdministracion(  ((Administracion) cmbAdministracion.getSelectionModel().getSelectedItem()).getId());
        cuentaCobrar.setCodigoCliente( ((Clientes) cmbCliente.getSelectionModel().getSelectedItem()).getId());
        cuentaCobrar.setCodigoLocal( ((Locales) cmbLocal.getSelectionModel().getSelectedItem()).getId() );
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarCuentasPorCobrar(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            
            pstmt.setInt(1, cuentaCobrar.getId());
            
            pstmt.setString(2, cuentaCobrar.getNumeroFactura());
            pstmt.setInt(3, cuentaCobrar.getAnio());
            pstmt.setInt(4, cuentaCobrar.getMes());
            pstmt.setBigDecimal(5, cuentaCobrar.getValorNetoPago());
            pstmt.setString(6, cuentaCobrar.getEstadoPago());
            pstmt.setInt(7, cuentaCobrar.getCodigoAdministracion());
            pstmt.setInt(8, cuentaCobrar.getCodigoCliente());
            pstmt.setInt(9, cuentaCobrar.getCodigoLocal());
            

            
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
    
   public void eliminarCuentasPorCobrar() {
        if (existeElementoSeleccionado()) {
            CuentasPorCobrar cuentaCobrar = (CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem();
            

            
            PreparedStatement pstmt = null;
            
            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarCuentasPorCobrar(?)}");
                pstmt.setInt(1, cuentaCobrar.getId());
                

                
                pstmt.execute();
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();
                
            } catch (SQLException e) {
                System.err.println("\nSe produjo un error al intentar eliminar el registro con el id " + cuentaCobrar.getId());
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
    
    
    
    public Clientes buscarClientes(int id) {
        Clientes cliente = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarClientes(?)}");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            while(rs.next()) {
                cliente = new Clientes(
                        rs.getInt("id"), 
                        rs.getString("nombres"), 
                        rs.getString("apellidos"), 
                        rs.getString("telefono"), 
                        rs.getString("direccion"), 
                        rs.getString("email"), 
                        rs.getInt("codigoTipoCliente")
                );
            }
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar buscar un Cliente con el id: " + id);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        
        return cliente;
    }
    
    public Locales buscarLocales(int id) {
        Locales local = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarLocales(?)}");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                local = new Locales();
                local.setId(rs.getInt("id"));
                local.setSaldoFavor(rs.getBigDecimal("saldoFavor"));
                local.setSaldoContra(rs.getBigDecimal("saldoContra"));
                local.setMesesPendientes(rs.getInt("mesesPendientes"));
                local.setDisponibilidad(rs.getBoolean("disponibilidad"));
                local.setValorLocal(rs.getBigDecimal("valorLocal"));
                local.setValorAdministracion(rs.getBigDecimal("valorAdministracion"));
            }
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar buscar un Local con el id: " + id);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return local;
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
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void limpiarCampos(){
        txtId.clear();
        txtNumeroFactura.clear();
        txtValorNetoPago.clear();
        spnAnio.getValueFactory().setValue(2021);
        spnMes.getValueFactory().setValue(1);
        cmbAdministracion.valueProperty().set(null);
        cmbCliente.valueProperty().set(null);
        cmbLocal.valueProperty().set(null);
        cmbEstado.valueProperty().set("Estado");
    }
    
    public void deshabilitarCampos(){
        txtId.setEditable(false);
        txtNumeroFactura.setEditable(false);
        txtValorNetoPago.setEditable(false);
        spnAnio.setDisable(true);
        spnMes.setDisable(true);
        cmbAdministracion.setDisable(true);
        cmbCliente.setDisable(true);
        cmbLocal.setDisable(true);
        cmbEstado.setDisable(true);
    }
    public void habilitarCampos(){
        txtId.setEditable(false);
        txtNumeroFactura.setEditable(true);
        txtValorNetoPago.setEditable(true);
        spnAnio.setDisable(false);
        spnMes.setDisable(false);
        cmbAdministracion.setDisable(false);
        cmbCliente.setDisable(false);
        cmbLocal.setDisable(false);
        cmbEstado.setDisable(false);
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
                operacion = CuentasPorCobrarController.Operaciones.GUARDAR;
                btnNuevo.setText("Guardar");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_save_50px.png"));
                btnModificar.setText("Cancelar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/cancelar.png"));
                btnEliminar.setDisable(true);
                btnReporte.setDisable(true);
                break;
            case GUARDAR:
                if(txtNumeroFactura.getText().trim().equals("") || txtValorNetoPago.getText().trim().equals("") || cmbAdministracion.getSelectionModel().getSelectedItem() == null || cmbCliente.getSelectionModel().getSelectedItem() == null || cmbEstado.getSelectionModel().getSelectedItem() == null || cmbLocal.getSelectionModel().getSelectedItem() == null){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    if (validarNumeroReal(txtValorNetoPago.getText())) {
                        agregarCuentasPorCobrar();
                    cargarDatos();
                    deshabilitarCampos();
                    limpiarCampos();
                    btnNuevo.setText("Nuevo");
                    imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                    btnModificar.setText("Modificar");
                    imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                    btnEliminar.setDisable(false);
                    btnReporte.setDisable(false);
                    operacion = CuentasPorCobrarController.Operaciones.NINGUNO;
                    }else{
                     Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("El numero ingresado en Valor neto pago no es valido, solo se permiten valores numericos " + "\n" + "\nEjemplo:" + "\n100 o 100.00");
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
                if(txtNumeroFactura.getText().trim().equals("") || txtValorNetoPago.getText().trim().equals("") || cmbAdministracion.getSelectionModel().getSelectedItem() == null || cmbCliente.getSelectionModel().getSelectedItem() == null || cmbEstado.getSelectionModel().getSelectedItem() == null || cmbLocal.getSelectionModel().getSelectedItem() == null){
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
                    operacion = CuentasPorCobrarController.Operaciones.ACTUALIZAR;
                }
                break;
            case ACTUALIZAR:
                if (txtNumeroFactura.getText().trim().equals("") || txtValorNetoPago.getText().trim().equals("") || cmbAdministracion.getSelectionModel().getSelectedItem() == null || cmbCliente.getSelectionModel().getSelectedItem() == null || cmbEstado.getSelectionModel().getSelectedItem() == null || cmbLocal.getSelectionModel().getSelectedItem() == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    if (validarNumeroReal(txtValorNetoPago.getText())) {
                        editarCuentasPorCobrar();
                        cargarDatos();
                        limpiarCampos();
                        deshabilitarCampos();
                        imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                        btnModificar.setText("Modificar");
                        btnEliminar.setText("Elminar");
                        imgEliminar.setImage(new Image("/org/diegopatzan/resource/images/icons8_empty_trash_32px.png"));
                        btnNuevo.setDisable(false);
                        btnReporte.setDisable(false);
                        operacion = CuentasPorCobrarController.Operaciones.NINGUNO;
                    }else{
                     Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("El numero ingresado en Valor neto pago no es valido, solo se permiten valores numericos " + "\n" + "\nEjemplo:" + "\n100 o 100.00");
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
                operacion = CuentasPorCobrarController.Operaciones.NINGUNO;
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
                operacion = CuentasPorCobrarController.Operaciones.NINGUNO;
                limpiarCampos();
                deshabilitarCampos();
                break;
            case NINGUNO:
                if (txtNumeroFactura.getText().trim().equals("") || txtValorNetoPago.getText().trim().equals("") || cmbAdministracion.getSelectionModel().getSelectedItem() == null || cmbCliente.getSelectionModel().getSelectedItem() == null || cmbEstado.getSelectionModel().getSelectedItem() == null || cmbLocal.getSelectionModel().getSelectedItem() == null) {
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
                        eliminarCuentasPorCobrar();
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
        
       GenerarReporte.getInstance().mostrarReporte("ReporteCuentasPorCobrar.jasper", parametros, "Reporte de cuentas por cobrar");
    }

    @FXML
    private void regresar(MouseEvent event) {
        this.escenarioPrincipal.mostrarEscenaClientes();
    }

    @FXML
    private void seleccionarElemento() {
        try {
            txtId.setText(String.valueOf(((CuentasPorCobrar)tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getId()));
            txtNumeroFactura.setText(((CuentasPorCobrar)tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getNumeroFactura());
            spnAnio.getValueFactory().setValue(((CuentasPorCobrar)tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getAnio());
            spnMes.getValueFactory().setValue(((CuentasPorCobrar)tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getMes());
            txtValorNetoPago.setText(String.valueOf(((CuentasPorCobrar)tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getValorNetoPago()));
            cmbEstado.getSelectionModel().select(((CuentasPorCobrar)tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getEstadoPago());
            cmbAdministracion.getSelectionModel().select(buscarAdministracion(((CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getCodigoAdministracion()));
            cmbCliente.getSelectionModel().select(buscarClientes(((CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getCodigoCliente()));
            cmbLocal.getSelectionModel().select(buscarLocales(((CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getCodigoLocal()));
        } catch (Exception e) {
        }
    }
    
    public void estadoPago(){
        cmbEstado.getItems().addAll("Pagado");
        cmbEstado.getItems().addAll("Pendiente");
        cmbEstado.getItems().addAll("Retrasado");
    }


}
