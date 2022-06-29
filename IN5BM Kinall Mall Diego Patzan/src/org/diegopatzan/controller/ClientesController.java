/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import org.diegopatzan.bean.Clientes;
import org.diegopatzan.bean.TipoCliente;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.diegopatzan.bean.Administracion;
import org.diegopatzan.bean.Locales;
import org.diegopatzan.db.Conexion;
import org.diegopatzan.bean.TipoCliente;
import org.diegopatzan.report.GenerarReporte;
/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 9/06/2021
 * @time 08:09:56
 */
public class ClientesController implements Initializable{
    
    private Principal escenarioPrincipal;

    

    
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtDireccion;
    @FXML
    private TextField txtEmail;
    @FXML
    private TableView tblClientes;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colNombres;
    @FXML
    private TableColumn colApellidos;
    @FXML
    private TableColumn colTelefono;
    @FXML
    private TableColumn colDireccion;
    @FXML
    private TableColumn colEmail;
    @FXML
    private TableColumn colCodigoTipoCliente;
    @FXML
    private ComboBox cmbTipoCliente;
    private CheckBox chDisponibilidad;
    @FXML
    private ImageView imgNuevo;
    @FXML
    private ImageView imgEditar;
    @FXML
    private ImageView imgEliminar;
    @FXML
    private ImageView imgReporte;

    private void mostrarEscenaTipoCliente(MouseEvent event) {
        this.escenarioPrincipal.mostrarEscenaTipoCliente();
    }

    @FXML
    private void mostrarEscenaCuentasPorCobrar(ActionEvent event) {
        this.escenarioPrincipal.mostrarEscenaCuentasPorCobrar();
    }



    

    
    private enum Operaciones{
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO;
    }
    
    private Operaciones operacion = Operaciones.NINGUNO;
    
    private ObservableList<Clientes> listaClienntes;
    private ObservableList<TipoCliente> listaTipoCliente;
    private ObservableList<Locales> listaLocales;
    private ObservableList<Administracion> listaAdministracion;
    
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnReporte;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
    }
    
    public boolean validar(ArrayList<TextField> listadoCampos){
        boolean respuesta = true;
        for(TextField campoTexto: listadoCampos){
            if(campoTexto.getText().trim().isEmpty()){
                return false;
            }
        }
        return respuesta;
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
        }catch(Exception e){
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
            e.printStackTrace();
        }
        listaClienntes = FXCollections.observableArrayList(lista);
        return listaClienntes;
    }
    
    public void cargarDatos(){
        tblClientes.setItems(getClientes());
        colId.setCellValueFactory(new PropertyValueFactory<Clientes, Integer>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Clientes, String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Clientes, String>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<Clientes, String>("telefono"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Clientes, String>("direccion"));
        colEmail.setCellValueFactory(new PropertyValueFactory<Clientes, String>("email"));
        colCodigoTipoCliente.setCellValueFactory(new PropertyValueFactory<Clientes, Integer>("codigoTipoCliente"));
        
        cmbTipoCliente.setItems(getTipoClientes());
    }
    public TipoCliente buscarTipoCliente(int id){
        TipoCliente registro = null;
        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarTipoCliente(?)}");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                registro = new TipoCliente(rs.getInt("id"), rs.getString("descripcion"));
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registro;
    }
    @FXML
    public void seleccionarElemento(){
        try {
            txtId.setText(String.valueOf(((Clientes)tblClientes.getSelectionModel().getSelectedItem()).getId()));
            txtNombres.setText(((Clientes)tblClientes.getSelectionModel().getSelectedItem()).getNombres());
            txtApellidos.setText(((Clientes)tblClientes.getSelectionModel().getSelectedItem()).getApellidos());
            txtTelefono.setText(((Clientes)tblClientes.getSelectionModel().getSelectedItem()).getTelefono());
            txtDireccion.setText(((Clientes)tblClientes.getSelectionModel().getSelectedItem()).getDireccion());
            txtEmail.setText(((Clientes)tblClientes.getSelectionModel().getSelectedItem()).getEmail());
            cmbTipoCliente.getSelectionModel().select(buscarTipoCliente(((Clientes)tblClientes.getSelectionModel().getSelectedItem()).getCodigoTipoCliente()));
        } catch (Exception e) {
            
        }
        
    }
    
    public void agregarClientes(){
        Clientes cliente = new Clientes();
        cliente.setNombres(txtNombres.getText());
        cliente.setApellidos(txtApellidos.getText());
        cliente.setTelefono(txtTelefono.getText());
        cliente.setDireccion(txtDireccion.getText());
        cliente.setEmail(txtEmail.getText());
        //if(cmbTipoCliente.getSelectionModel().getSelectedItem() != null)
        cliente.setCodigoTipoCliente(((TipoCliente)cmbTipoCliente.getSelectionModel().getSelectedItem()).getId());
        System.out.println(cliente);
        
        
        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarClientes(?,?,?,?,?,?)}");
            pstmt.setString(1, cliente.getNombres());
            pstmt.setString(2, cliente.getApellidos());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getEmail());
            pstmt.setInt(6, cliente.getCodigoTipoCliente());
            
            
            pstmt.execute();

            
            listaClienntes.add(cliente);
        /*}catch(SQLException e){ 
            if(e.getErrorCode() == 1452)
                JOptionPane.showMessageDialog(null, "Seleccione un tipo de cliente para continuar","Error",JOptionPane.INFORMATION_MESSAGE);*/

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void editarClientes(){
        Clientes cliente = new Clientes();
        cliente.setId(Integer.parseInt(txtId.getText()));
        cliente.setNombres(txtNombres.getText());
        cliente.setApellidos(txtApellidos.getText());
        cliente.setTelefono(txtTelefono.getText());
        cliente.setDireccion(txtDireccion.getText());
        cliente.setEmail(txtEmail.getText());
        cliente.setCodigoTipoCliente(((TipoCliente)cmbTipoCliente.getSelectionModel().getSelectedItem()).getId());
        
        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarClientes(?, ?, ?, ?, ?, ?, ?)}");
            pstmt.setInt(1, cliente.getId());
            pstmt.setString(2, cliente.getNombres());
            pstmt.setString(3, cliente.getApellidos());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getDireccion());
            pstmt.setString(6, cliente.getEmail());
            pstmt.setInt(7, cliente.getCodigoTipoCliente());
            pstmt.execute();
        
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void eliminarCliente(){
        Clientes cliente = null;
        if(tblClientes.getSelectionModel().getSelectedItem() != null){
            cliente = (Clientes)tblClientes.getSelectionModel().getSelectedItem();
            try {
                PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarClientes(?)}");
                pstmt.setInt(1, cliente.getId());
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
                                alert.setContentText("Este dato no puede ser eliminado, este cliente esta asociado en cuentas por cobrar \n id:" + txtId.getText());
                                alert.show();
            }
        }
    }
    
    
    public boolean validarTelefono(String numero){
        
        Pattern pattern1 = Pattern.compile("^[0-9]{8}$");
        Matcher matcher1 = pattern1.matcher(numero);
        return matcher1.matches();
    }
    
    public boolean validarEmail(String email){
        Pattern pattern = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
    public void activarControles(){
        txtId.setEditable(false);
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtTelefono.setEditable(true);
        txtDireccion.setEditable(true);
        txtEmail.setEditable(true);
        cmbTipoCliente.setDisable(false);
    }
    
    public void desactivarControles(){
        txtId.setEditable(false);
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtTelefono.setEditable(false);
        txtDireccion.setEditable(false);
        txtEmail.setEditable(false);
        cmbTipoCliente.setDisable(true);
    }
    
    public void limpiarControles(){
        txtId.clear();
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtEmail.clear();
        cmbTipoCliente.valueProperty().set(null);
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    @FXML
    private void nuevo(ActionEvent event) {
        switch(operacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                operacion = Operaciones.GUARDAR;
                btnNuevo.setText("Guardar");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_save_50px.png"));
                btnModificar.setText("Cancelar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/cancelar.png"));
                btnEliminar.setDisable(true);
                btnReporte.setDisable(true);
                break;
            case GUARDAR:
                if(txtNombres.getText().trim().equals("") || txtApellidos.getText().trim().equals("") || txtTelefono.getText().trim().equals("") || txtDireccion.getText().trim().equals("") || txtEmail.getText().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                
                }else{
                    if(cmbTipoCliente.getSelectionModel().getSelectedItem() == null){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Seleccione un tipo de cliente para continuar");
                                alert.show();
                }else{
                        if(validarTelefono(txtTelefono.getText())){
                            if(validarEmail(txtEmail.getText())){
                            agregarClientes();
                            cargarDatos();
                            desactivarControles();
                            limpiarControles();
                            btnNuevo.setText("Nuevo");
                            imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                            btnModificar.setText("Modificar");
                            imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                            btnEliminar.setDisable(false);
                            btnReporte.setDisable(false);
                            operacion = Operaciones.NINGUNO;
                            limpiarControles();
                            desactivarControles();
                            }else{
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("El correo es invalido \nEjemplo: \nEjemploCorreo@ejemplo.ejemplo");
                                alert.show();
                            }
                        }else{
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Numero de telefono invalido \nEjemplo: \n11111111 \n(8 Digitos)" );
                                alert.show();
                        }
                    
                }
                }
                break;


                
        }
    }

    @FXML
    private void modificar(ActionEvent event) {
        switch(operacion){
            case NINGUNO:
                if(txtNombres.getText().trim().equals("") || txtApellidos.getText().trim().equals("") || txtTelefono.getText().trim().equals("") || txtDireccion.getText().trim().equals("") || txtEmail.getText().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Para modificar tiene que seleccionar un registro");
                                alert.show();
                }else{
                    activarControles();
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
                if(txtNombres.getText().trim().equals("") || txtApellidos.getText().trim().equals("") || txtTelefono.getText().trim().equals("") || txtDireccion.getText().trim().equals("") || txtEmail.getText().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    editarClientes();
                    cargarDatos();
                    limpiarControles();
                    desactivarControles();
                    btnModificar.setText("Modificar");
                    imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                    btnEliminar.setText("Elminar");
                    imgEliminar.setImage(new Image("/org/diegopatzan/resource/images/icons8_empty_trash_32px.png"));
                    btnNuevo.setDisable(false);
                    btnReporte.setDisable(false);
                    operacion = Operaciones.NINGUNO;
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
                desactivarControles();
                limpiarControles();
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
                limpiarControles();
                desactivarControles();
                break;
            case NINGUNO:
                if(txtNombres.getText().trim().equals("") || txtApellidos.getText().trim().equals("") || txtTelefono.getText().trim().equals("") || txtDireccion.getText().trim().equals("") || txtEmail.getText().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Para eliminar tiene que seleccionar un registro");
                                alert.show();
                }else{
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText(null);
                    alert.setContentText("Esta seguro que desea eliminar este registro?" + "\nID: " + txtId.getText() + "\nNombres: " + txtNombres.getText() + "\nApellidos: " + txtApellidos.getText() + "\nDirección: " + txtDireccion.getText() + "\n E-mail: " + txtEmail.getText() + "\nTipo de cliente: " + cmbTipoCliente.getSelectionModel().getSelectedItem());
                    
                    Optional<ButtonType> respuesta = alert.showAndWait();
                    
                    if(respuesta.get() == ButtonType.OK){
                        eliminarCliente();
                        limpiarControles();
                        cargarDatos();
                    }
                    
                }
                break;
        }
    }

    @FXML
    private void Reporte(ActionEvent event) {
        Map parametros = new HashMap();
        
       GenerarReporte.getInstance().mostrarReporte("ReporteClientes.jasper", parametros, "Reporte de clientes");
    }

    @FXML
    private void regresar(MouseEvent event) {
        this.escenarioPrincipal.menuPrincipal();
    }

}
