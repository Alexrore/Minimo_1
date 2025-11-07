package edu.upc.dsa.util;
import edu.upc.dsa.models.*;
import java.util.*;

public interface Manager {
    Lector añadirLector(String id, String nombre, String apellidos, String dni, String lugarNacimiento, String direccion, Date fecha);
    Libro añadirLibroAlmacen(String id, String isbn, String titulo, String editorial, int anyoPublicacion, int numeroEdicion, String autor, String tematica);
    Libro catalogarLibro();
    Prestamo prestarLibro(String id, String idLibro, String idLector, Date fechaPrestamo, Date fechaDevloucion);
    List<Prestamo> prestamosDeUnLector(String idLector);
    void clear();
}
