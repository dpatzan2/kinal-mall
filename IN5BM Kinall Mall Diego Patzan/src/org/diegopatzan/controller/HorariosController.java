/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.controller;

import com.jfoenix.controls.JFXTimePicker;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.diegopatzan.bean.Horarios;
import org.diegopatzan.system.Principal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import org.diegopatzan.db.Conexion;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javafx.css.converter.StringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import org.diegopatzan.report.GenerarReporte;


/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 7/07/2021
 * @time 07:12:23
 */
public class HorariosController implements Initializable{

    private Principal escenarioPrincipal;
    
    private enum Operaciones{
        NUEVO, GUARDAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO;
    }
    
     public boolean existeElementoSeleccionado() {
        return tblHorarios.getSelectionModel().getSelectedItem() != null;
    }
    
    private Operaciones operacion = Operaciones.NINGUNO;
    
    private ObservableList<Horarios> listaHorarios;
    
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
    private TableView tblHorarios;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colHoraEntrada;
    @FXML
    private TableColumn colHoraSalida;
    @FXML
    private TableColumn colLunes;
    @FXML
    private TableColumn colMartes;
    @FXML
    private TableColumn colMiercoles;
    @FXML
    private TableColumn colJueves;
    @FXML
    private TableColumn colViernes;
    @FXML
    private TextField txtId;
    @FXML
    private JFXTimePicker tpHorarioSalida;
    @FXML
    private JFXTimePicker tpHorarioEntrada;
    @FXML
    private CheckBox chkLunes;
    @FXML
    private CheckBox chkMartes;
    @FXML
    private CheckBox chkMiercoles;
    @FXML
    private CheckBox chkJueves;
    @FXML
    private CheckBox chkViernes;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //tpHorarioEntrada.set24HourView(true);
        //StringConverter<LocalTime> defaultCoverter = new LocalTimeStringConverter(FormatStyle.SHORT,Locale.US);
        
        tpHorarioEntrada.setEditable(false);
        tpHorarioSalida.setEditable(false);
        cargarDatos();     
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
    
    public void cargarDatos(){
        tblHorarios.setItems(getHorarios());
        colId.setCellValueFactory(new PropertyValueFactory<Horarios, Integer>("id"));
        colHoraEntrada.setCellValueFactory(new PropertyValueFactory<Horarios, Time>("horarioEntrada"));
        colHoraSalida.setCellValueFactory(new PropertyValueFactory<Horarios, Time>("horarioSalida"));
        colLunes.setCellValueFactory(new PropertyValueFactory<Horarios, Boolean>("lunes"));
        colMartes.setCellValueFactory(new PropertyValueFactory<Horarios, Boolean>("martes"));
        colMiercoles.setCellValueFactory(new PropertyValueFactory<Horarios, Boolean>("miercoles"));
        colJueves.setCellValueFactory(new PropertyValueFactory<Horarios, Boolean>("jueves"));
        colViernes.setCellValueFactory(new PropertyValueFactory<Horarios, Boolean>("viernes"));
    }
    
    public void agregarHorarios(){
        Horarios horario = new Horarios();
        horario.setHorarioEntrada(Time.valueOf(tpHorarioEntrada.getValue()));
        horario.setHorarioSalida(Time.valueOf(tpHorarioSalida.getValue()));
        horario.setLunes(chkLunes.isSelected());
        horario.setMartes(chkMartes.isSelected());
        horario.setMiercoles(chkMiercoles.isSelected());
        horario.setJueves(chkJueves.isSelected());
        horario.setViernes(chkViernes.isSelected());
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarHorarios(?, ?, ?, ?, ?, ?, ?)}");
            pstmt.setTime(1, horario.getHorarioEntrada());
            pstmt.setTime(2, horario.getHorarioSalida());
            pstmt.setBoolean(3, horario.isLunes());
            pstmt.setBoolean(4, horario.isMartes());
            pstmt.setBoolean(5, horario.isMiercoles());
            pstmt.setBoolean(6, horario.isJueves());
            pstmt.setBoolean(7, horario.isViernes());
            
            pstmt.execute();
            listaHorarios.add(horario);
        } catch (SQLException e) {
            System.out.println("Se produjo un error al intentar insertar datos");
            e.printStackTrace();
        }finally{
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
    public void editarHorarios() {
        //Horarios horario = new Horarios();
        Horarios horario = (Horarios) tblHorarios.getSelectionModel().getSelectedItem();
        horario.setId(Integer.parseInt(txtId.getText()));
        horario.setHorarioEntrada(Time.valueOf(tpHorarioEntrada.getValue()));
        horario.setHorarioSalida(Time.valueOf(tpHorarioSalida.getValue()));
        horario.setLunes(chkLunes.isSelected());
        horario.setMartes(chkMartes.isSelected());
        horario.setMiercoles(chkMiercoles.isSelected());
        horario.setJueves(chkJueves.isSelected());
        horario.setViernes(chkViernes.isSelected());
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarHorarios(?, ?, ?, ?, ?, ?, ?, ?)}");
            
            pstmt.setInt(1, horario.getId());
            
            pstmt.setTime(2, horario.getHorarioEntrada());
            pstmt.setTime(3, horario.getHorarioSalida());
            pstmt.setBoolean(4, horario.isLunes());
            pstmt.setBoolean(5, horario.isMartes());
            pstmt.setBoolean(6, horario.isMiercoles());
            pstmt.setBoolean(7, horario.isJueves());
            pstmt.setBoolean(8, horario.isViernes());
            

            
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

    public void eliminarHorarios() {
        if (existeElementoSeleccionado()) {
            Horarios horario = (Horarios) tblHorarios.getSelectionModel().getSelectedItem();
            

            
            PreparedStatement pstmt = null;
            
            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarHorarios(?)}");
                pstmt.setInt(1, horario.getId());
                

                
                pstmt.execute();
                listaHorarios.remove(tblHorarios.getSelectionModel().getSelectedItem());
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();
                
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Error");
                                alert.setContentText("Este dato no puede ser eliminado, un empleado tiene asociado este horario \n id:" + txtId.getText());
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
        tpHorarioEntrada.getEditor().clear();
        tpHorarioSalida.getEditor().clear();
        chkLunes.setSelected(false);
        chkMartes.setSelected(false);
        chkMiercoles.setSelected(false);
        chkJueves.setSelected(false);
        chkViernes.setSelected(false);
        
    }
    
    public void habilitarCampos(){
        txtId.setEditable(false);
        txtId.setDisable(false);
        tpHorarioEntrada.setDisable(false);
        tpHorarioSalida.setDisable(false);
        chkLunes.setDisable(false);
        chkMartes.setDisable(false);
        chkMiercoles.setDisable(false);
        chkJueves.setDisable(false);
        chkViernes.setDisable(false);
    
    }
    
    public void deshabilitarCampos(){
        txtId.setEditable(false);
        txtId.setDisable(true);
        tpHorarioEntrada.setDisable(true);
        tpHorarioSalida.setDisable(true);
        chkLunes.setDisable(true);
        chkMartes.setDisable(true);
        chkMiercoles.setDisable(true);
        chkJueves.setDisable(true);
        chkViernes.setDisable(true);
    
    }
    
    
    @FXML
    private void nuevo(ActionEvent event) {
        
        switch(operacion){
            case NINGUNO:
                habilitarCampos();
                limpiarCampos();
                operacion = HorariosController.Operaciones.GUARDAR;
                btnNuevo.setText("Guardar");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_save_50px.png"));
                btnModificar.setText("Cancelar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/cancelar.png"));
                btnEliminar.setDisable(true);
                btnReporte.setDisable(true);
                break;
            case GUARDAR:
                if((tpHorarioEntrada.getValue() ==  null) || (tpHorarioSalida.getValue() ==  null)){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                    agregarHorarios();
                    cargarDatos();
                    deshabilitarCampos();
                    limpiarCampos();
                    btnNuevo.setText("Nuevo");
                    imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                    btnModificar.setText("Modificar");
                    imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                    btnEliminar.setDisable(false);
                    btnReporte.setDisable(false);
                    operacion = HorariosController.Operaciones.NINGUNO;
                }
                break;


                
        }
    }
    
   
    @FXML
    private void modificar(ActionEvent event) {
        switch(operacion){
            case NINGUNO:
                if((tpHorarioEntrada.getValue() ==  null) || (tpHorarioSalida.getValue() ==  null)){
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
                    operacion = HorariosController.Operaciones.ACTUALIZAR;
                }
                break;
            case ACTUALIZAR:
                if ((tpHorarioEntrada.getValue() ==  null) || (tpHorarioSalida.getValue() ==  null)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Información");
                                alert.setContentText("Asegurese que todos los campos esten llenos");
                                alert.show();
                }else{
                editarHorarios();
                cargarDatos();
                limpiarCampos();
                deshabilitarCampos();
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                btnModificar.setText("Modificar");
                btnEliminar.setText("Elminar");
                imgEliminar.setImage(new Image("/org/diegopatzan/resource/images/icons8_empty_trash_32px.png"));
                btnNuevo.setDisable(false);
                btnReporte.setDisable(false);
                operacion = HorariosController.Operaciones.NINGUNO;
                }
                break;
            case GUARDAR:
                btnNuevo.setText("Nuevo");
                imgNuevo.setImage(new Image("/org/diegopatzan/resource/images/icons8_new_copy_32px.png"));
                btnModificar.setText("Modificar");
                imgEditar.setImage(new Image("/org/diegopatzan/resource/images/icons8_edit_file_32px.png"));
                btnEliminar.setDisable(false);
                btnReporte.setDisable(false);
                operacion = HorariosController.Operaciones.NINGUNO;
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
                operacion = HorariosController.Operaciones.NINGUNO;
                limpiarCampos();
                deshabilitarCampos();
                break;
            case NINGUNO:
                if ((tpHorarioEntrada.getValue() ==  null) || (tpHorarioSalida.getValue() ==  null)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Kinal mall");
                                alert.setHeaderText("Advertencia");
                                alert.setContentText("Para eliminar tiene que seleccionar un registro");
                                alert.show();
                }else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Kinal mall");
                    alert.setHeaderText(null);
                    alert.setContentText("Esta seguro que desea eliminar este registro?" + "\nID: " + txtId.getText()
                    + "\nHora entrada: " + tpHorarioEntrada.getValue()
                    + "\nHora salida: " + tpHorarioSalida.getValue());
                    
                    Optional<ButtonType> respuesta = alert.showAndWait();
                    
                    if(respuesta.get() == ButtonType.OK){
                        eliminarHorarios();
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
        
       GenerarReporte.getInstance().mostrarReporte("ReporteHorarios.jasper", parametros, "Reporte de horarios");
    }

    @FXML
    private void regresar(MouseEvent event) {
        this.escenarioPrincipal.mostrarEscenaEmpleados();
    }

    @FXML
    private void seleccionarElemento() {
        
        if(existeElementoSeleccionado()){
            txtId.setText(String.valueOf(((Horarios)tblHorarios.getSelectionModel().getSelectedItem()).getId()));
            tpHorarioEntrada.setValue(((Horarios)tblHorarios.getSelectionModel().getSelectedItem()).getHorarioEntrada().toLocalTime());
            tpHorarioSalida.setValue(((Horarios)tblHorarios.getSelectionModel().getSelectedItem()).getHorarioSalida().toLocalTime());
            chkLunes.setSelected(((Horarios)tblHorarios.getSelectionModel().getSelectedItem()).isLunes());
            chkMartes.setSelected(((Horarios)tblHorarios.getSelectionModel().getSelectedItem()).isMartes());
            chkMiercoles.setSelected(((Horarios) tblHorarios.getSelectionModel().getSelectedItem()).isMiercoles());
            chkJueves.setSelected(((Horarios)tblHorarios.getSelectionModel().getSelectedItem()).isJueves());
            chkViernes.setSelected(((Horarios)tblHorarios.getSelectionModel().getSelectedItem()).isViernes());
        }
    }
    
    
  


}
