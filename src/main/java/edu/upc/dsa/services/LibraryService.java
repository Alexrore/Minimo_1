package edu.upc.dsa.services;

import edu.upc.dsa.util.Manager;
import edu.upc.dsa.util.ManagerImpl;
import edu.upc.dsa.models.*;

import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.*;

@Api(value = "/library", tags = "Library") // sin description para quitar el warning deprecado
@Path("/library")
@Produces(MediaType.APPLICATION_JSON)
public class LibraryService {

    private final Manager manager;

    public LibraryService() {
        this.manager = ManagerImpl.getInstance();
    }

    // ---- util fecha ----
    private static Date parseDate(String s) {
        if (s == null) return null;
        // intenta varios formatos simples
        String[] patterns = {"yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy"};
        for (String p : patterns) {
            try {
                return new SimpleDateFormat(p).parse(s);
            } catch (ParseException ignore) {}
        }
        return null;
    }

    // ---------- Salud ----------
    @GET @Path("/health")
    @ApiOperation(value = "Ping del servicio", response = String.class)
    public Response health() {
        return Response.ok("{\"status\":\"ok\"}").build();
    }

    // ---------- 1) Añadir lector ----------
    public static class ReaderDTO {
        public String id;
        public String nombre;
        public String apellidos;
        public String dni;
        public String lugarNacimiento;
        public String direccion;
        public String fecha; // string que convertimos a Date
    }

    @POST @Path("/readers")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Añade un lector", response = Lector.class)
    public Response addReader(ReaderDTO dto) {
        if (dto == null || dto.id == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"lector inválido\"}").build();
        }
        Date f = parseDate(dto.fecha);
        Lector l = manager.añadirLector(dto.id, dto.nombre, dto.apellidos, dto.dni, dto.lugarNacimiento, dto.direccion, f);
        return Response.ok(l).build();
    }

    // ---------- 2) Añadir libro al almacén ----------
    public static class BookDTO {
        public String id;
        public String isbn;
        public String titulo;
        public String editorial;
        public Integer añoPublicacion;
        public Integer numeroEdicion;
        public String autor;
        public String tematica;
    }

    @POST @Path("/books/storage")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Añade un libro al almacén (montones de 10)", response = Libro.class)
    public Response addBookToStorage(BookDTO dto) {
        if (dto == null || dto.id == null || dto.isbn == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"libro inválido\"}").build();
        }
        Libro lib = manager.añadirLibroAlmacen(
                dto.id,
                dto.isbn,
                dto.titulo,
                dto.editorial,
                dto.añoPublicacion != null ? dto.añoPublicacion : 0,
                dto.numeroEdicion != null ? dto.numeroEdicion : 0,
                dto.autor,
                dto.tematica
        );
        return Response.ok(lib).build();
    }

    // ---------- 3) Catalogar siguiente libro ----------
    @POST @Path("/books/catalog")
    @ApiOperation(value = "Cataloga el siguiente libro del almacén", response = Libro.class)
    public Response catalogNext() {
        try {
            Libro out = manager.catalogarLibro();
            if (out == null) {
                // algunas impls devuelven null cuando no hay libros
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\":\"no hay libros pendientes\"}")
                        .build();
            }
            return Response.ok(out).build();
        } catch (IllegalStateException | NoSuchElementException e) {
            // típico si los montones están vacíos o se hace pop() sin elementos
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"no hay libros pendientes\"}")
                    .build();
        } catch (Exception e) {
            // cualquier otra cosa inesperada: evitar 500 HTML
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"no se pudo catalogar\",\"detalle\":\"" + e.getClass().getSimpleName() + "\"}")
                    .build();
        }
    }

    // ---------- 4) Prestar libro ----------
    public static class LoanDTO {
        public String id;
        public String idLibro;
        public String idLector;
        public String fechaPrestamo;
        public String fechaDevloucion; // ojo: el POJO usa 'Devloucion'
    }

    @POST @Path("/loans")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Crea un préstamo", response = Prestamo.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "Lector o libro no existe"),
            @ApiResponse(code = 409, message = "No hay ejemplares disponibles")
    })
    public Response createLoan(LoanDTO dto) {
        if (dto == null || dto.id == null || dto.idLibro == null || dto.idLector == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"payload inválido\"}").build();
        }
        Date fp = parseDate(dto.fechaPrestamo);
        Date fd = parseDate(dto.fechaDevloucion);
        try {
            Prestamo p = manager.prestarLibro(dto.id, dto.idLibro, dto.idLector, fp, fd);
            if (p == null) {
                // según tu impl puede devolver null cuando no hay stock
                return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"sin ejemplares\"}").build();
            }
            return Response.ok(p).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"lector o libro no encontrado\"}").build();
        }
    }

    // ---------- 5) Préstamos de un lector ----------
    @GET @Path("/loans/reader/{idLector}")
    @ApiOperation(value = "Lista los préstamos de un lector", response = Prestamo.class, responseContainer = "List")
    public Response loansByReader(@PathParam("idLector") String idLector) {
        List<Prestamo> lista = manager.prestamosDeUnLector(idLector);
        return Response.ok(lista).build();
    }
}
