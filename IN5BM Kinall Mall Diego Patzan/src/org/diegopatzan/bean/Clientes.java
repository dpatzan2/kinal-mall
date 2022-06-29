/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.bean;

/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 9/06/2021
 * @time 09:07:39
 */
public class Clientes {
    private int id;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String email;

    private int codigoTipoCliente;

    public Clientes() {
    }

    public Clientes(int id, String nombres, String apellidos, String telefono, String direccion, String email, int codigoTipoCliente) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.direccion = direccion;
        this.email = email;
        this.codigoTipoCliente = codigoTipoCliente;
    }

   

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCodigoTipoCliente() {
        return codigoTipoCliente;
    }

    public void setCodigoTipoCliente(int codigoTipoCliente) {
        this.codigoTipoCliente = codigoTipoCliente;
    }

    @Override
    public String toString() { 
        return id + " | " + nombres + " " + apellidos ;
    }


    
    
}
