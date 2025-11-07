package edu.upc.dsa;

import edu.upc.dsa.models.Lector;
import edu.upc.dsa.models.Libro;
import edu.upc.dsa.models.Prestamo;
import edu.upc.dsa.util.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests de integración sobre ManagerImpl usando solo JUnit 4.9 (pom.xml).
 *
 * NOTA: asumimos que el "identificador del libro catalogado" para prestar es el ISBN
 * (clave del catálogo), tal y como está implementado en ManagerImpl.
 */
public class ManagerImplTest {

    private ManagerImpl m; // usamos la impl concreta para poder llamar a clear()

    @Before
    public void setUp() {
        m = ManagerImpl.getInstance();
        m.clear();
    }

    @Test
    public void testAñadirLector() {
        Date hoy = new Date();
        Lector l1 = m.añadirLector("u1","Ada","Lovelace","1234A","Londres","Baker St", hoy);
        assertNotNull(l1);
        assertEquals("u1", l1.getId());

        // Actualización sobre el mismo id
        Lector l2 = m.añadirLector("u1","Ada","Byron","1234A","Londres","Baker St 221B", hoy);
        assertSame(l1, l2);
        assertEquals("Byron", l2.getApellidos());
        assertEquals("Baker St 221B", l2.getDireccion());
    }

    @Test
    public void testAñadirLibroAlmacen() {
        // Añadimos 11 libros al almacén: deben quedar 2 montones (10 y 1), y en cada montón LIFO
        for (int i = 0; i < 11; i++) {
            String idx = String.valueOf(i);
            m.añadirLibroAlmacen("id"+idx, "ISBN-A", "Titulo"+idx, "Ed", 2020, 1, "Autor", "Tema");
        }
        // Para verificar el LIFO del primer montón: catalogamos 10 veces y la primera extracción
        // debe corresponder al último insertado de ese primer montón (i=9)
        Libro first = m.catalogarLibro();
        assertEquals("ISBN-A", first.getISBN());
        assertEquals("Titulo9", first.getTitulo());

        // Vaciamos el resto del primer montón (9 extracciones más)
        for (int i = 0; i < 9; i++) m.catalogarLibro();

        // Ahora el segundo montón (con 1 libro) pasa a ser el primero y al catalogar sale ese único libro (i=10)
        Libro fromSecondPile = m.catalogarLibro();
        assertEquals("Titulo10", fromSecondPile.getTitulo());
    }

    @Test
    public void testCatalogarLibro() {
        // metemos 3 libros con mismo ISBN en almacén y los catalogamos
        for (int i = 0; i < 3; i++) {
            m.añadirLibroAlmacen("x"+i, "ISBN-1", "T", "E", 2000, 1, "A", "T");
        }
        m.catalogarLibro();
        m.catalogarLibro();
        Libro ultimo = m.catalogarLibro();
        // El catálogo debe tener cantidad=3 para ese ISBN
        assertEquals("ISBN-1", ultimo.getISBN());
        // getLibrosCatalogados() devuelve vista, lo usamos para inspeccionar cantidad
        Libro cat = m.getLibrosCatalogados().stream()
                .filter(l -> "ISBN-1".equals(l.getISBN()))
                .findFirst().orElse(null);
        assertNotNull(cat);
        assertEquals(3, cat.getCantidad());
    }

    @Test
    public void testPrestarLibro() {
        // Preparamos: un lector y 2 ejemplares del mismo ISBN en catálogo
        m.añadirLector("l1","Linus","Torvalds","DNI","Helsinki","c/ Linux", new Date());
        m.añadirLibroAlmacen("a1","ISBN-7","Kernel","Ed", 1991,1,"Linus","SO");
        m.añadirLibroAlmacen("a2","ISBN-7","Kernel","Ed", 1991,1,"Linus","SO");
        m.catalogarLibro();
        m.catalogarLibro();

        // Prestamos un ejemplar del ISBN-7
        Date hoy = new Date();
        Prestamo p = m.prestarLibro("p1","ISBN-7","l1", hoy, addDays(hoy,14));
        assertNotNull(p);
        assertTrue(p.isEnTramite());

        // La cantidad en catálogo debe decrementar a 1
        Libro cat = m.getLibrosCatalogados().stream()
                .filter(l -> "ISBN-7".equals(l.getISBN()))
                .findFirst().orElse(null);
        assertNotNull(cat);
        assertEquals(1, cat.getCantidad());

        // Y debe aparecer en el listado del lector
        List<Prestamo> prestamos = m.prestamosDeUnLector("l1");
        assertEquals(1, prestamos.size());
        assertEquals("p1", prestamos.get(0).getId());
    }
    @Test
    public void testPrestamosDeUnLector_listaInmutable() {
        m.añadirLector("l1","Grace","Hopper","DNI","NY","c/ COBOL", new Date());
        m.añadirLibroAlmacen("a1","ISBN-9","COBOL","Ed", 1959,1,"Grace","PL");
        m.catalogarLibro();
        m.prestarLibro("p1","ISBN-9","l1", new Date(), new Date());

        List<Prestamo> prestamos = m.prestamosDeUnLector("l1");
        try {
            prestamos.add(new Prestamo("hack","ISBN-9","l1", new Date(), new Date()));
            fail("La lista debería ser inmodificable");
        } catch (UnsupportedOperationException expected) {
            // ok
        }
    }

    // --- util ---
    private static Date addDays(Date base, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(base);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }
}
