package mateolopez.taller;

import mateolopez.taller.model.Coche;
import mateolopez.taller.model.Mecanico;
import mateolopez.taller.model.Reparacion;
import mateolopez.taller.repository.CocheRepository;
import mateolopez.taller.repository.MecanicoRepository;
import mateolopez.taller.repository.ReparacionRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(mateolopez.taller.fixtures.DataFixtures.class)
@ActiveProfiles("test")
@Transactional
public class Tests {

    @Autowired
    private CocheRepository cocheRepository;

    @Autowired
    private MecanicoRepository mecanicoRepository;

    @Autowired
    private ReparacionRepository reparacionRepository;

    @Autowired
    private EntityManager entityManager;

    // ------------------------
    // Tests unitarios de repositorio (10)
    // ------------------------
    @Test void testCantidadCoches() { assertEquals(10, cocheRepository.findAll().size()); }
    @Test void testCantidadMecanicos() { assertEquals(5, mecanicoRepository.findAll().size()); }
    @Test void testCantidadReparaciones() { assertEquals(10, reparacionRepository.findAll().size()); }
    @Test void testCocheNoNull() { cocheRepository.findAll().forEach(c -> assertNotNull(c)); }
    @Test void testMecanicoNoNull() { mecanicoRepository.findAll().forEach(m -> assertNotNull(m)); }
    @Test void testReparacionNoNull() { reparacionRepository.findAll().forEach(r -> assertNotNull(r)); }
    @Test void testIdsCoche() { cocheRepository.findAll().forEach(c -> assertNotNull(c.getId())); }
    @Test void testIdsMecanico() { mecanicoRepository.findAll().forEach(m -> assertNotNull(m.getId())); }
    @Test void testIdsReparacion() { reparacionRepository.findAll().forEach(r -> assertNotNull(r.getId())); }
    @Test void testMatriculasUnicas() {
        List<Coche> coches = cocheRepository.findAll();
        long uniqueCount = coches.stream().map(Coche::getMatricula).distinct().count();
        assertEquals(coches.size(), uniqueCount);
    }

    // ------------------------
    // Tests de integración (10)
    // ------------------------
    @Test void testRelacionCocheReparacion() {
        reparacionRepository.findAll().forEach(r -> {
            assertNotNull(r.getCoche());
            assertNotNull(r.getMecanico());
        });
    }

    @Test void testDatosCoche() {
        Coche c = cocheRepository.findAll().get(0);
        assertNotNull(c.getMarca());
        assertNotNull(c.getModelo());
        assertNotNull(c.getMatricula());
    }

    @Test void testDatosMecanico() {
        Mecanico m = mecanicoRepository.findAll().get(0);
        assertNotNull(m.getNombre());
    }

    @Test void testDatosReparacion() {
        Reparacion r = reparacionRepository.findAll().get(0);
        assertNotNull(r.getDescripcion());
        assertNotNull(r.getFecha());
        assertNotNull(r.getHoras());
        assertNotNull(r.getPrecio());
    }

    @Test void testRelacionCocheMultipleReparaciones() {
        Coche c = cocheRepository.findAll().get(0);
        long count = reparacionRepository.findAll().stream()
                .filter(r -> r.getCoche().getId().equals(c.getId()))
                .count();
        assertTrue(count >= 1);
    }

    @Test void testRelacionMecanicoMultipleReparaciones() {
        Mecanico m = mecanicoRepository.findAll().get(0);
        long count = reparacionRepository.findAll().stream()
                .filter(r -> r.getMecanico().getId().equals(m.getId()))
                .count();
        assertTrue(count >= 1);
    }

    @Test void testHorasPositivas() {
        reparacionRepository.findAll().forEach(r -> assertTrue(r.getHoras() > 0));
    }

    @Test void testPrecioPositivo() {
        reparacionRepository.findAll().forEach(r -> assertTrue(r.getPrecio() > 0));
    }

    @Test void testDescripcionNoVacia() {
        reparacionRepository.findAll().forEach(r -> assertFalse(r.getDescripcion().isEmpty()));
    }

    @Test void testFechaNoFutura() {
        reparacionRepository.findAll().forEach(r ->
                assertFalse(r.getFecha().isAfter(LocalDate.now())));
    }

    // ------------------------
    // Tests de aceptación (15)
    // ------------------------
    @Test void testCrearNuevoCoche() {
        Coche nuevo = new Coche("AAAA111", "Tesla", "Model 3");
        Coche guardado = cocheRepository.save(nuevo);
        assertNotNull(guardado.getId());
    }

    @Test void testCrearNuevoMecanico() {
        Mecanico nuevo = new Mecanico("Laura", "Suarez");
        Mecanico guardado = mecanicoRepository.save(nuevo);
        assertNotNull(guardado.getId());
    }

    @Test void testCrearNuevaReparacion() {
        Coche c = cocheRepository.findAll().get(0);
        Mecanico m = mecanicoRepository.findAll().get(0);
        Reparacion r = new Reparacion(c, m, LocalDate.now(), "Prueba", 2, 120.0);
        Reparacion guardada = reparacionRepository.save(r);
        assertNotNull(guardada.getId());
    }

    @Test void testActualizarCoche() {
        Coche c = cocheRepository.findAll().get(0);
        c.setMarca("UpdatedBrand");
        cocheRepository.save(c);
        assertEquals("UpdatedBrand",
                cocheRepository.findById(c.getId()).orElseThrow().getMarca());
    }

    @Test void testActualizarMecanico() {
        Mecanico m = mecanicoRepository.findAll().get(0);
        m.setNombre("UpdatedName");
        mecanicoRepository.save(m);
        assertEquals("UpdatedName",
                mecanicoRepository.findById(m.getId()).orElseThrow().getNombre());
    }

    @Test void testActualizarReparacion() {
        Reparacion r = reparacionRepository.findAll().get(0);
        r.setDescripcion("Actualizado");
        reparacionRepository.save(r);
        assertEquals("Actualizado",
                reparacionRepository.findById(r.getId()).orElseThrow().getDescripcion());
    }

    @Test void testEliminarCoche() {
        Coche c = cocheRepository.save(new Coche("ZZZZ999", "Renault", "Clio"));
        Long id = c.getId();
        cocheRepository.delete(c);
        assertFalse(cocheRepository.findById(id).isPresent());
    }

    @Test void testEliminarMecanico() {
        Mecanico m = mecanicoRepository.save(new Mecanico("Pedro", "Garcia"));
        Long id = m.getId();
        mecanicoRepository.delete(m);
        assertFalse(mecanicoRepository.findById(id).isPresent());
    }

    @Test void testEliminarReparacion() {
        Coche c = cocheRepository.findAll().get(0);
        Mecanico m = mecanicoRepository.findAll().get(0);
        Reparacion r = reparacionRepository.save(
                new Reparacion(c, m, LocalDate.now(), "Eliminar", 1, 50.0));
        Long id = r.getId();
        reparacionRepository.delete(r);
        assertFalse(reparacionRepository.findById(id).isPresent());
    }

    @Test void testCrearVariasReparaciones() {
        Coche c = cocheRepository.findAll().get(1);
        Mecanico m = mecanicoRepository.findAll().get(1);
        for (int i = 0; i < 5; i++) {
            reparacionRepository.save(
                    new Reparacion(c, m, LocalDate.now(), "Multi " + i, 1, 30.0));
        }
        long count = reparacionRepository.findAll().stream()
                .filter(r -> r.getCoche().getId().equals(c.getId()))
                .count();
        assertTrue(count >= 5);
    }

    @Test void testActualizarHorasYPrecioReparacion() {
        Reparacion r = reparacionRepository.findAll().get(0);
        r.setHoras(10);
        r.setPrecio(500.0);
        reparacionRepository.save(r);
        Reparacion actualizado = reparacionRepository.findById(r.getId()).orElseThrow();
        assertEquals(10, actualizado.getHoras());
        assertEquals(500.0, actualizado.getPrecio());
    }

    @Test void testCrearCocheDuplicadoMatricula() {
        assertThrows(Exception.class, () -> {
            cocheRepository.save(new Coche("1111AAA", "Duplicado", "Test"));
            entityManager.flush();
        });
    }

    @Test void testCrearReparacionSinCoche() {
        Mecanico m = mecanicoRepository.findAll().get(0);
        assertThrows(Exception.class, () -> {
            reparacionRepository.save(
                    new Reparacion(null, m, LocalDate.now(), "Sin coche", 1, 50.0));
            entityManager.flush();
        });
    }

    @Test void testCrearReparacionSinMecanico() {
        Coche c = cocheRepository.findAll().get(0);
        assertThrows(Exception.class, () -> {
            reparacionRepository.save(
                    new Reparacion(c, null, LocalDate.now(), "Sin mecanico", 1, 50.0));
            entityManager.flush();
        });
    }

    @Test void testActualizarMatriculaCoche() {
        Coche c = cocheRepository.findAll().get(0);
        String old = c.getMatricula();
        c.setMatricula("NEW1234");
        cocheRepository.save(c);
        assertEquals("NEW1234",
                cocheRepository.findById(c.getId()).orElseThrow().getMatricula());
        c.setMatricula(old);
        cocheRepository.save(c);
    }
}
