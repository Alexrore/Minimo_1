package edu.upc.dsa.models;
import java.util.*;

public class Libro {
    private String id, ISBN, titulo, editorial, autor, tematica;
    private int añoPublicacion, edicion, cantidad;

    public Libro() {}
    public Libro(String id, String ISBN, String titulo, String editorial, int añoPublicacion,int edicion, String autor, String tematica) {
        this.id = id;
        this.ISBN = ISBN;
        this.titulo = titulo;
        this.editorial = editorial;
        this.añoPublicacion = añoPublicacion;
        this.autor = autor;
        this.tematica = tematica;
        this.cantidad = 0;
        this.edicion = edicion;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getISBN() {
        return ISBN;
    }
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getEditorial() {
        return editorial;
    }
    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }
    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }
    public String getTematica() {
        return tematica;
    }
    public void setTematica(String tematica) {
        this.tematica = tematica;
    }
    public int getEdicion() {
        return edicion;
    }
    public void setEdicion(int edicion) {
        this.edicion = edicion;
    }
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public void addCantidad(int cantidad) {
        this.cantidad += cantidad;
    }
    public int getAñoPublicacion(){
        return añoPublicacion;
    }

    public void setAñoPublicacion(int añoPublicacion) {
        this.añoPublicacion = añoPublicacion;
    }
}
