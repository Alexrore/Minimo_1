package edu.upc.dsa.util;

import edu.upc.dsa.models.Lector;
import edu.upc.dsa.models.Libro;
import edu.upc.dsa.models.Prestamo;
import org.apache.log4j.Logger;

import java.util.*;


public class ManagerImpl implements Manager {

    private static final Logger logger = Logger.getLogger(ManagerImpl.class);

    // --- Singleton ---
    private static ManagerImpl instance;
    public static  ManagerImpl getInstance() {
        if (instance == null) instance = new ManagerImpl();
        return instance;
    }

    // --- Estado ---
    //Montones de libros recibidos: FIFO de montones; dentro de cada montón, LIFO.
    private final List<Deque<Libro>> montones = new LinkedList<>();

    //Catálogo por ISBN con cantidad disponible.
    private final Map<String, Libro> catalogo = new HashMap<>();

    //Lectores por id.
    private final Map<String, Lector> lectores = new HashMap<>();

    // Préstamos por id lector.
    private final Map<String, Prestamo> prestamosById = new HashMap<>();

    // Préstamos agrupados por lector.
    private final Map<String, List<Prestamo>> prestamosByLector = new HashMap<>();

    public ManagerImpl() { }

    // --- Utilidades internas ---
    private static <T> List<T> immutableCopy(Collection<T> src) {
        return Collections.unmodifiableList(new ArrayList<>(src));
    }

    private Deque<Libro> ultimoMonton() {
        if (montones.isEmpty()) return null;
        return montones.get(montones.size() - 1);
    }

    private Deque<Libro> primerMonton() {
        if (montones.isEmpty()) return null;
        return montones.get(0);
    }


    @Override
    public synchronized Lector añadirLector(String id,
                                            String nombre,
                                            String apellidos,
                                            String dni,
                                            String lugarNacimiento,
                                            String direccion,
                                            Date fecha) {
        logger.info("añadirLector id=" + id + ", nombre=" + nombre + ", apellidos=" + apellidos);
        if (id == null) throw new IllegalArgumentException("id lector null");
        Lector l = lectores.get(id);
        if (l == null) {
            l = new Lector(id, nombre, apellidos, dni, lugarNacimiento, direccion, fecha);
            lectores.put(id, l);
        } else {
            // Actualización de datos si ya existe
            l.setNombre(nombre);
            l.setApellidos(apellidos);
            l.setDni(dni);
            l.setLugarNacimiento(lugarNacimiento);
            l.setDireccion(direccion);
            l.setFecha(fecha);
        }
        logger.info("Lector añadido -> id=" + l.getId());
        return l;
    }

    @Override
    public  Libro añadirLibroAlmacen(String id,
                                                 String isbn,
                                                 String titulo,
                                                 String editorial,
                                                 int añoPublicacion,
                                                 int numeroEdicion,
                                                 String autor,
                                                 String tematica) {
        logger.info("añadirLibroAlmacen libro id=" + id + ", isbn=" + isbn);
        if (isbn == null) throw new IllegalArgumentException("isbn null");
        Libro libro = new Libro(id, isbn, titulo, editorial, añoPublicacion, numeroEdicion, autor, tematica);
        Deque<Libro> ultimo = ultimoMonton();
        if (ultimo == null || ultimo.size() >= 10) {
            Deque<Libro> nuevo = new ArrayDeque<>();
            nuevo.push(libro); // LIFO dentro del montón
            montones.add(nuevo); // Se añade al final: FIFO de montones
            logger.info("Creado nuevo montón. Num montones=" + montones.size() + ", tamaño nuevo=1");
        } else {
            ultimo.push(libro);
            logger.info("Apilado en último montón. Tamaño montón=" + ultimo.size());
        }
        logger.info("Libro añadido");
        return libro;
    }

    @Override
    public  Libro catalogarLibro() {
        logger.info("catalogarLibro");
        if (montones.isEmpty()) {
            logger.error("No hay libros pendientes de catalogar");
            throw new NoSuchElementException("No hay libros pendientes de catalogar");
        }

        Libro extraido = null;
        while (!montones.isEmpty() && extraido == null) {
            Deque<Libro> primero = primerMonton();
            if (primero.isEmpty()) {
                montones.remove(0); // eliminar montones vacíos
            } else {
                extraido = primero.pop();
                if (primero.isEmpty()) montones.remove(0);
            }
        }
        if (extraido == null) {
            logger.error("No hay libros pendientes de catalogar (montones vacíos)");
            throw new NoSuchElementException("No hay libros pendientes de catalogar");
        }

        Libro cat = catalogo.get(extraido.getISBN());
        if (cat == null) {
            extraido.setCantidad(1);
            catalogo.put(extraido.getISBN(), extraido);
            logger.info("Catalogado nuevo ISBN=" + extraido.getISBN() + ", cantidad=1");
        } else {
            cat.setCantidad(cat.getCantidad() + 1);
            logger.info("Incrementada cantidad ISBN=" + cat.getISBN() + " -> " + cat.getCantidad());
        }
        logger.info("Libro catalogado ISBN=" + extraido.getISBN());
        return extraido;
    }

    @Override
    public  Prestamo prestarLibro(String id,
                                              String idLibro,
                                              String idLector,
                                              Date fechaPrestamo,
                                              Date fechaDevloucion) {
        logger.info("prestarLibro id=" + id + ", idLibro=" + idLibro + ", idLector=" + idLector);
        if (!lectores.containsKey(idLector)) {
            logger.error("Lector no existe: " + idLector);
            throw new NoSuchElementException("Lector no existe: " + idLector);
        }

        Libro cat = catalogo.get(idLibro);
        if (cat == null) {
            logger.error("Libro no catalogado (ISBN)=" + idLibro);
            throw new NoSuchElementException("Libro no catalogado: " + idLibro);
        }
        if (cat.getCantidad() <= 0) {
            logger.error("Sin ejemplares disponibles para ISBN=" + idLibro);
            throw new IllegalStateException("No hay ejemplares disponibles");
        }
        cat.setCantidad(cat.getCantidad() - 1);
        Prestamo p = new Prestamo(id, idLibro, idLector, fechaPrestamo, fechaDevloucion);
        p.setEnTramite(true);
        prestamosById.put(id, p);
        prestamosByLector.computeIfAbsent(idLector, k -> new ArrayList<>()).add(p);
        logger.info("Libro prestado, restantes=" + cat.getCantidad());
        return p;
    }

    @Override
    public List<Prestamo> prestamosDeUnLector(String idLector) {
        logger.info("prestamosDeUnLector idLector=" + idLector);
        List<Prestamo> res = prestamosByLector.getOrDefault(idLector, Collections.emptyList());
        // Copia inmutable para que los tests no puedan modificar el estado interno
        List<Prestamo> out = immutableCopy(res);
        logger.info("Libros prestados=" + out.size());
        return out;
    }

    // --- Métodos de soporte útiles en tests (no forman parte estricta de la interfaz, pero ayudan) ---
    public  List<Libro> getLibrosCatalogados() {
        logger.info("getLibrosCatalogados");
        List<Libro> res = new ArrayList<>(catalogo.values());
        res.sort(Comparator.comparing(Libro::getISBN));
        logger.info("Libros catalogados=" + res.size());
        return Collections.unmodifiableList(res);
    }


    public void clear() {
        logger.info("IN clear");
        montones.clear();
        catalogo.clear();
        lectores.clear();
        prestamosById.clear();
        prestamosByLector.clear();
        logger.info("OUT clear");
    }
}

