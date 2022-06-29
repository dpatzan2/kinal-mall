/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 26/05/2021
 * @time 09:33:02
 */
public class Conexion {
    
    private Connection conexion;
    private final String URL;
    private static Conexion instancia;
    private final String PUERTO;
    private final String BD;
    private final String USER;
    private final String PASS;
    private final String SERVER;
    
    private Conexion(){
        SERVER = "localhost";
        PUERTO = "3306";
        BD = "IN5BM_KinalMall";
        USER = "root";
        PASS = "password";
        URL = "jdbc:mysql://" + SERVER + ":" + PUERTO + "/" + BD + "?allowPublicKeyRetrieval=true&serverTimezone=UTC&useSSL=false&useTimezone=true";
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Class.forName("com.mysql.cj.jdbc.Driver");
            
            conexion = DriverManager.getConnection(URL, USER, PASS);        
        }catch(ClassNotFoundException e){
            System.out.println("No se encuentra ninguna definicion para la clase");
            e.printStackTrace();
        }catch(InstantiationException e){
            System.out.println("No se puede crear una instancia del objeto");
            e.printStackTrace();
        }catch(IllegalAccessException e){
            System.out.println("No se tiene los permisos para acceder al paquete");
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            System.out.println("Se produjo un error");
            e.printStackTrace();
        }
    }
    
    public static Conexion getInstance(){
        if(instancia == null){
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }

    public void setConexion(Connection conexion) {
        this.conexion = conexion;
    }
    
    
}
