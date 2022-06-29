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
import java.sql.Date;
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
import org.diegopatzan.bean.Cargos;
import org.diegopatzan.bean.Departamentos;
import org.diegopatzan.bean.Empleados;
import org.diegopatzan.bean.Horarios;
import org.diegopatzan.db.Conexion;
import org.diegopatzan.report.GenerarReporte;
import org.diegopatzan.system.Principal;

/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 13/07/2021
 * @time 09:14:24
 */
public class EmpleadosController implements Initializable{

    private Principal escenarioPrincipal;
    
    private enum Operaciones{
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO;
    }
    
    private Operaciones operacion = Operaciones.NINGUNO;
    
    private ObservableList<Empleados> listaEmpleados;
    private ObservableList<Administracion> listaAdministracion;
    private ObservableList<Cargos> listaCargos;
    private ObservableList<Horarios> listaHorarios;
    private ObservableList<Departamentos> listaDepartamentos;
    
    @FXML
    private TableView tblEmpleados;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colNombres;
    @FXML
    private TableColumn colApellidos;
    @FXML
    private TableColumn colEmail;
    @FXML
    private TableColumn colTelefono;
    @FXML
    private TableColumn colFechaContrato;
    @FXML
    private TableColumn colSueldo;
    @FXML
    private TableColumn colDepartamento;
    @FXML
    private TableColumn colCargo;
    @FXML
    private TableColumn colHorario;
    @FXML
    private TableColumn colAdministracion;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtEmail;
    @FXML
    private JFXDatePicker dpFechaContrato;
    @FXML
    private TextField txtSueldo;
    @FXML
    private TextField txtTelefono;
    @FXML
    private ComboBox cmbDepartamento;
    @FXML
    private ComboBox cmbHorario;
    @FXML
    private ComboBox cmbCargo;
    @FXML
    private ComboBox cmbAdministracion;
    
    public boolean existeElementoSeleccionado() {
        return tblEmpleados.getSelectionModel().getSelectedItem() != null;
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
    private void nuevo(ActionEvent event) {
        
        switch(operacion){
            case NINGUNO:
                habilitarCampos();
                limpiarCampos();
                operacion = EmpleadosController.Operaciones.GUARDAR;
                btnNuevo.setText("Guardar");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_save_50px.png"));
                btnModificar.setText("Cancelar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/cancelar.png"));
                btnEliminar.setDisable(true);
                btnReporte.setDisable(true);
                break;
            case GUARDAR:
                if(txtNombres.getText().trim().equals("") || 
                        txtApellidos.getText().trim().equals("") || 
                        txtEmail.getText().trim().equals("") || 
                        txtTelefono.getText().trim().equals("") || 
                        dpFechaContrato.getValue() == null || 
                        cmbAdministracion.getSelectionModel().getSelectedItem() == null || 
                        cmbCargo.getSelectionModel().getSelectedItem() == null || 
                        cmbDepartamento.getSelectionModel().getSelectedItem() == null || 
                        cmbHorario.getSelectionModel().getSelectedItem() == null){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    if (validarEmail(txtEmail.getText())) {
                        if (validarTelefono(txtTelefono.getText())) {
                            if (validarNumeroReal(txtSueldo.getText())) {
                                agregarEmpleados();
                                cargarDatos();
                                deshabilitarCampos();
                                limpiarCampos();
                                btnNuevo.setText("Nuevo");
                                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                                btnModificar.setText("Modificar");
                                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                                btnEliminar.setDisable(false);
                                btnReporte.setDisable(false);
                                operacion = EmpleadosController.Operaciones.NINGUNO; 
                            }else{
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("El numero ingresado en sueldo no es valido, solo se permiten valores numericos " + "\n" + "\nEjemplo:" + "\n0 a 10000000.00");
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
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("El correo es invalido \nEjemplo: \nEjemploCorreo@ejemplo.ejemplo");
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
                if(existeElementoSeleccionado()){
                    habilitarCampos();
                    btnModificar.setText("Actualizar");
                    imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_save_50px.png"));
                    btnEliminar.setText("Cancelar");
                    imgEliminar.setImage(new Image("/org/diegopatzan/resource/images/cancelar.png"));
                    btnNuevo.setDisable(true);
                    btnReporte.setDisable(true);
                    operacion = EmpleadosController.Operaciones.ACTUALIZAR;
                }else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText("Advertencia");
                    alert.setContentText("Para modificar tiene que seleccionar un registro");
                    alert.show();
                }
                break;



            case ACTUALIZAR:
                if (txtNombres.getText().trim().equals("") || 
                        txtApellidos.getText().trim().equals("") || 
                        txtEmail.getText().trim().equals("") || 
                        txtTelefono.getText().trim().equals("") || 
                        dpFechaContrato.getValue() == null || 
                        cmbAdministracion.getSelectionModel().getSelectedItem() == null || 
                        cmbCargo.getSelectionModel().getSelectedItem() == null || 
                        cmbDepartamento.getSelectionModel().getSelectedItem() == null || 
                        cmbHorario.getSelectionModel().getSelectedItem() == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    if (validarEmail(txtEmail.getText())) {
                        if (validarTelefono(txtTelefono.getText())) {
                            if (validarNumeroReal(txtSueldo.getText())) {
                                editarEmpleados();
                                cargarDatos();
                                limpiarCampos();
                                deshabilitarCampos();
                                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                                btnModificar.setText("Modificar");
                                btnEliminar.setText("Elminar");
                                imgEliminar.setImage(new Image("/org/diegopatzan/resource/images/icons8_empty_trash_32px.png"));
                                btnNuevo.setDisable(false);
                                btnReporte.setDisable(false);
                                operacion = EmpleadosController.Operaciones.NINGUNO; 
                            }else{
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("El numero ingresado en sueldo no es valido, solo se permiten valores numericos " + "\n" + "\nEjemplo:" + "\n0 a 10000000.00");
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
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("El correo es invalido \nEjemplo: \nEjemploCorreo@ejemplo.ejemplo");
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
                operacion = EmpleadosController.Operaciones.NINGUNO;
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
                operacion = EmpleadosController.Operaciones.NINGUNO;
                limpiarCampos();
                deshabilitarCampos();
                break;
            case NINGUNO:
                if (existeElementoSeleccionado()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText(null);
                    alert.setContentText("Esta seguro que desea eliminar este registro?" + "\nID: " + txtId.getText() + "\nNombres: " + txtNombres.getText() + "\nApellidos: " + txtApellidos.getText());
                    
                    Optional<ButtonType> respuesta = alert.showAndWait();
                    
                    if(respuesta.get() == ButtonType.OK){
                        eliminarEmpleados();
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
        
       GenerarReporte.getInstance().mostrarReporte("ReporteEmpleados.jasper", parametros, "Reporte de empleados");
    }

    @FXML
    private void regresar(MouseEvent event) {
        this.escenarioPrincipal.menuPrincipal();
    }

    @FXML
    private void mostrarEscenaHorarios(ActionEvent event) {
        this.escenarioPrincipal.mostrarEscenaHorarios();
    }

    @FXML
    private void seleccionarElemento() {
        if(existeElementoSeleccionado()){
        txtId.setText(String.valueOf(((Empleados)tblEmpleados.getSelectionModel().getSelectedItem()).getId()));
        txtNombres.setText(((Empleados)tblEmpleados.getSelectionModel().getSelectedItem()).getNombres());
        txtApellidos.setText(((Empleados) tblEmpleados.getSelectionModel().getSelectedItem()).getApellidos());
        txtEmail.setText(((Empleados)tblEmpleados.getSelectionModel().getSelectedItem()).getEmail());
        txtTelefono.setText(((Empleados)tblEmpleados.getSelectionModel().getSelectedItem()).getTelefono());
        dpFechaContrato.setValue(((Empleados)tblEmpleados.getSelectionModel().getSelectedItem()).getFechaContratacion().toLocalDate());
        cmbCargo.getSelectionModel().select(buscarCargos(((Empleados)tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoCargo()));
        cmbHorario.getSelectionModel().select(buscarHorarios(((Empleados)tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoHorario()));
        cmbAdministracion.getSelectionModel().select(buscarAdministracion(((Empleados)tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoAdministracion()));
        cmbDepartamento.getSelectionModel().select(buscarDepartamentos(((Empleados)tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoDepartamento()));
        txtSueldo.setText(String.valueOf(((Empleados)tblEmpleados.getSelectionModel().getSelectedItem()).getSueldo()));
        }else{
        
    }
    }


    
    
 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dpFechaContrato.setEditable(false);
       cargarDatos();
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
    public boolean validarNumeroReal(String numero){
        String patron = "^[0-9]{1,8}([.][0-9]{2})?";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(numero);
        
        return matcher.matches();
    }
    
    public void agregarEmpleados() {
        Empleados empleados = new Empleados();
        empleados.setNombres(txtNombres.getText());
        empleados.setApellidos(txtApellidos.getText());
        empleados.setEmail(txtEmail.getText());
        empleados.setTelefono(txtTelefono.getText());
        empleados.setFechaContratacion(Date.valueOf(dpFechaContrato.getValue()));
        empleados.setSueldo(new BigDecimal(txtSueldo.getText()));
        empleados.setCodigoAdministracion(((Administracion)cmbAdministracion.getSelectionModel().getSelectedItem()).getId());
        empleados.setCodigoCargo(((Cargos)cmbCargo.getSelectionModel().getSelectedItem()).getId());
        empleados.setCodigoHorario(((Horarios)cmbHorario.getSelectionModel().getSelectedItem()).getId());
        empleados.setCodigoDepartamento(((Departamentos)cmbDepartamento.getSelectionModel().getSelectedItem()).getId());
        
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarEmpleados(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            pstmt.setString(1, empleados.getNombres());
            pstmt.setString(2, empleados.getApellidos());
            pstmt.setString(3, empleados.getEmail());
            pstmt.setString(4, empleados.getTelefono());
            pstmt.setDate(5, empleados.getFechaContratacion());
            pstmt.setBigDecimal(6, empleados.getSueldo());
            pstmt.setInt(7, empleados.getCodigoDepartamento());
            pstmt.setInt(8, empleados.getCodigoCargo());
            pstmt.setInt(9, empleados.getCodigoHorario());
            pstmt.setInt(10, empleados.getCodigoAdministracion());
            

            
            pstmt.execute();
            listaEmpleados.add(empleados);
            
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar agregar un nuevo empleado");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
     public void editarEmpleados() {
        Empleados empleados = new Empleados();
        empleados.setId(Integer.parseInt(txtId.getText()));
        empleados.setNombres(txtNombres.getText());
        empleados.setApellidos(txtApellidos.getText());
        empleados.setEmail(txtEmail.getText());
        empleados.setTelefono(txtTelefono.getText());
        empleados.setFechaContratacion(Date.valueOf(dpFechaContrato.getValue()));
        empleados.setSueldo(new BigDecimal(txtSueldo.getText()));
        empleados.setCodigoAdministracion(((Administracion)cmbAdministracion.getSelectionModel().getSelectedItem()).getId());
        empleados.setCodigoCargo(((Cargos)cmbCargo.getSelectionModel().getSelectedItem()).getId());
        empleados.setCodigoHorario(((Horarios)cmbHorario.getSelectionModel().getSelectedItem()).getId());
        empleados.setCodigoDepartamento(((Departamentos)cmbDepartamento.getSelectionModel().getSelectedItem()).getId());
        
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarEmpleados(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            
            pstmt.setInt(1, empleados.getId());
            pstmt.setString(2, empleados.getNombres());
            pstmt.setString(3, empleados.getApellidos());
            pstmt.setString(4, empleados.getEmail());
            pstmt.setString(5, empleados.getTelefono());
            pstmt.setDate(6, empleados.getFechaContratacion());
            pstmt.setBigDecimal(7, empleados.getSueldo());
            pstmt.setInt(8, empleados.getCodigoDepartamento());
            pstmt.setInt(9, empleados.getCodigoCargo());
            pstmt.setInt(10, empleados.getCodigoHorario());
            pstmt.setInt(11, empleados.getCodigoAdministracion());
            

            
            pstmt.execute();
            
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar editar un empleado");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }        
    }
     
     public void eliminarEmpleados() {

            Empleados empleados = (Empleados) tblEmpleados.getSelectionModel().getSelectedItem();
            

            
            PreparedStatement pstmt = null;
            
            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarEmpleados(?)}");
                pstmt.setInt(1, empleados.getId());
                

                
                pstmt.execute();
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();
                
            } catch (SQLException e) {
                System.err.println("\nSe produjo un error al intentar eliminar el registro con el id " + empleados.getId());
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
    
    public ObservableList<Empleados> getEmpleados(){
        ArrayList<Empleados> lista  = new ArrayList<>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarEmpleados}");
            rs = pstmt.executeQuery();
            
            while(rs.next()){
                Empleados empleados = new Empleados(
                        rs.getInt("id"), 
                        rs.getString("nombres"), 
                        rs.getString("apellidos"), 
                        rs.getString("email"), 
                        rs.getString("telefono"), 
                        rs.getDate("fechaContratacion"), 
                        rs.getBigDecimal("sueldo"), 
                        rs.getInt("codigoDepartamento"), 
                        rs.getInt("codigoCargo"), 
                        rs.getInt("codigoHorario"), 
                        rs.getInt("codigoAdministracion"));
                lista.add(empleados);
            }
            

            
            listaEmpleados =FXCollections.observableArrayList(lista);
        } catch (SQLException e) {
            System.err.println("Se produjo un error al intentar consultar la lista de horarios");
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }finally{
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return listaEmpleados;
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
    
    public ObservableList<Cargos> getCargos(){
        
        ArrayList<Cargos> listado = new ArrayList<Cargos>();
        
        try{
            //CallableStatement stmt; 
            PreparedStatement stmt;
            stmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarCargos()}");
            ResultSet rs = stmt.executeQuery();
            
            while(rs.next()){
                listado.add(new Cargos(
                        rs.getInt("id"), 
                        rs.getString("nombre")
                )
                );
            }
            
            rs.close();
            stmt.close();
        }catch(SQLException e){
            System.err.println("\n Se produjo un error al intentar consultar la tabla Cargos en la base de datos");
            e.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }
        listaCargos = FXCollections.observableArrayList(listado);
        return listaCargos;
    }
    
    public ObservableList<Horarios> getHorarios(){
        ArrayList<Horarios> lista  = new ArrayList<>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarHorarios}");
            rs = pstmt.executeQuery();
            
            while(rs.next()){
                Horarios horario = new Horarios(
                        rs.getInt("id"), 
                        rs.getTime("horarioEntrada"), 
                        rs.getTime("horarioSalida"), 
                        rs.getBoolean("lunes"), 
                        rs.getBoolean("martes"),
                        rs.getBoolean("miercoles"), 
                        rs.getBoolean("jueves"), 
                        rs.getBoolean("viernes"));
                lista.add(horario);
            }
            

            
            listaHorarios =FXCollections.observableArrayList(lista);
        } catch (SQLException e) {
            System.err.println("Se produjo un error al intentar consultar la lista de horarios");
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }finally{
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return listaHorarios;
    }
    
    public ObservableList<Departamentos> getDepartamentos(){
        ArrayList<Departamentos> lista = new ArrayList<>();
        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarDepartamentos}");
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                lista.add(new Departamentos(
                        rs.getInt("id"), 
                        rs.getString("nombre")));
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        listaDepartamentos = FXCollections.observableArrayList(lista);
        return listaDepartamentos;
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
    
    public Cargos buscarCargos(int id){
        Cargos registro = null;
        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarCargos(?)}");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                registro = new Cargos(
                        rs.getInt("id"), 
                        rs.getString("nombre")
                );
                
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registro;
    }
    
    public Horarios buscarHorarios(int id){
        Horarios registro = null;
        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarHorarios(?)}");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                registro = new Horarios(
                        rs.getInt("id"), 
                        rs.getTime("horarioEntrada"), 
                        rs.getTime("horarioSalida"), 
                        rs.getBoolean("lunes"), 
                        rs.getBoolean("martes"),
                        rs.getBoolean("miercoles"), 
                        rs.getBoolean("jueves"), 
                        rs.getBoolean("viernes"));
                
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registro;
    }
    
    public Departamentos buscarDepartamentos(int id){
        Departamentos registro = null;
        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarDepartamentos(?)}");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                registro = new Departamentos(
                        rs.getInt("id"), 
                        rs.getString("nombre")
                );
                
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registro;
    }
    
    public void cargarDatos(){
        tblEmpleados.setItems(getEmpleados());
        colId.setCellValueFactory(new PropertyValueFactory<Empleados, Integer>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Empleados, String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Empleados, String>("apellidos"));
        colEmail.setCellValueFactory(new PropertyValueFactory<Empleados, String>("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<Empleados, String>("telefono"));
        colFechaContrato.setCellValueFactory(new PropertyValueFactory<Empleados, String>("fechaContratacion"));
        colSueldo.setCellValueFactory(new PropertyValueFactory<Empleados, BigDecimal>("sueldo"));
        colDepartamento.setCellValueFactory(new PropertyValueFactory<Empleados, Integer>("codigoDepartamento"));
        colHorario.setCellValueFactory(new PropertyValueFactory<Empleados, Integer>("codigoHorario"));
        colCargo.setCellValueFactory(new PropertyValueFactory<Empleados, Integer>("codigoCargo"));
        colAdministracion.setCellValueFactory(new PropertyValueFactory<Empleados, Integer>("codigoAdministracion"));
        

        cmbAdministracion.setItems(getAdministracion());
        cmbCargo.setItems(getCargos());
        cmbHorario.setItems(getHorarios());
        cmbDepartamento.setItems(getDepartamentos());

    }
    
    public void limpiarCampos(){
        txtId.clear();
        dpFechaContrato.getEditor().clear();
        txtNombres.clear();
        txtApellidos.clear();
        txtEmail.clear();
        txtTelefono.clear();
        txtSueldo.clear();
        cmbAdministracion.valueProperty().set(null);
        cmbCargo.valueProperty().set(null);
        cmbDepartamento.valueProperty().set(null);
        cmbHorario.valueProperty().set(null);
        
    }
    
    public void habilitarCampos(){
        txtId.setEditable(false);
        txtId.setDisable(false);
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtEmail.setEditable(true);
        txtTelefono.setEditable(true);
        txtSueldo.setEditable(true);
        dpFechaContrato.setDisable(false);
        cmbAdministracion.setDisable(false);
        cmbCargo.setDisable(false);
        cmbDepartamento.setDisable(false);
        cmbHorario.setDisable(false);

    
    }
    
    public void deshabilitarCampos(){
        txtId.setEditable(false);
        txtId.setDisable(true);
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtEmail.setEditable(false);
        txtTelefono.setEditable(false);
        txtSueldo.setEditable(false);
        dpFechaContrato.setDisable(true);
        cmbAdministracion.setDisable(true);
        cmbCargo.setDisable(true);
        cmbDepartamento.setDisable(true);
        cmbHorario.setDisable(true);

    
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

}
