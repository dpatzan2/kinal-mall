/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.diegopatzan.report;

import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.diegopatzan.db.Conexion;

/**
 *
 * @author Diego Fernando Patzán Marroquín
 * @date 15/07/2021
 * @time 10:23:59
 */
public class GenerarReporte {
    
    private static GenerarReporte instancia;
    private JasperViewer jasperViewer;
    
    private GenerarReporte(){
        Locale locale = new Locale("es", "GT");
        Locale.setDefault(locale);
    }
    
    public static GenerarReporte getInstance(){
        if (instancia == null) {
            instancia = new GenerarReporte();
        }
        return instancia;
    }
    

    
    public void mostrarReporte(String nombreReporte, Map parametros, String titulo) {
    try {
        parametros.put("LOGO_HEADER", getClass().getResourceAsStream("/org/diegopatzan/resource/images/Logo Kinal Mall.png"));
        parametros.put("LOGO_REPORTE", getClass().getResourceAsStream("/org/diegopatzan/resource/images/icons8_report_file_100px_1.png"));
        
        InputStream reporte = GenerarReporte.class.getResourceAsStream(nombreReporte);
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reporte);
        
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, Conexion.getInstance().getConexion());
        
        //if (jasperViewer == null || !jasperViewer.isVisible()) {
            jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle(titulo);
            jasperViewer.setVisible(true);
        //}
    } catch (Exception e) {
    e.printStackTrace();
    }
}
    
}
