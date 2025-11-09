package edu.upc.dsa.models;
import java.util.*;

public class Prestamo {
    private String id, idLibro,idLector;
    Date fechaPrestamo, fechaDevloucion;
    boolean enTramite;

    public Prestamo() {}

    public  Prestamo(String id, String idLibro, String idLector, Date fechaPrestamo, Date fechaDevloucion) {
        this.id = id;
        this.idLibro = idLibro;
        this.idLector = idLector;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevloucion = fechaDevloucion;
        this.enTramite = true;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getIdLibro() {
        return idLibro;
    }
    public void setIdLibro(String idLibro) {
        this.idLibro = idLibro;
    }
    public String getIdLector() {
        return idLector;
    }
    public void setIdLector(String idLector) {
        this.idLector = idLector;
    }
    public Date getFechaPrestamo() {
        return fechaPrestamo;
    }
    public void setFechaPrestamo(Date fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }
    public Date getFechaDevloucion() {
        return fechaDevloucion;
    }
    public void setFechaDevloucion(Date fechaDevloucion) {
        this.fechaDevloucion = fechaDevloucion;
    }
    public boolean isEnTramite() {
        return enTramite;
    }
    public void setEnTramite(boolean enTramite) {
        this.enTramite = enTramite;
    }

}
