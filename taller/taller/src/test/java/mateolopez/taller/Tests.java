package mateolopez.taller;

import mateolopez.taller.model.Coche;
import mateolopez.taller.model.Mecanico;
import mateolopez.taller.model.Reparacion;
import mateolopez.taller.repository.CocheRepository;
import mateolopez.taller.repository.MecanicoRepository;
import mateolopez.taller.repository.ReparacionRepository;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

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

    // Tests unitarios
    @Test
    void testCantidadCoches() {
        assertEquals(10, cocheRepository.findAll().size());
    }

    @Test
    void testCocheNoNull() {
        cocheRepository.findAll().forEach(c -> assertNotNull(c));
    }

    @Test
    void testIdsCoche() {
        cocheRepository.findAll().forEach(c -> assertNotNull(c.getId()));
    }

    @Test
    void testMatriculasUnicas() {
        List<Coche> coches = cocheRepository.findAll();
        long uniqueCount = coches.stream().map(Coche::getMatricula).distinct().count();
        assertEquals(coches.size(), uniqueCount);
    }

    @Test
    void testAddCar() {
        Coche c = new Coche("NEW123", "Toyota", "Corolla");
        cocheRepository.save(c);
        entityManager.flush();
        assertNotNull(c.getId());
    }

    @Test
    void testUpdateCar() {
        Coche c = cocheRepository.findAll().get(0);
        c.setMarca("UpdatedBrand");
        c.setModelo("UpdatedModel");
        cocheRepository.save(c);
        entityManager.flush();
        Coche actualizado = cocheRepository.findById(c.getId()).orElseThrow();
        assertEquals("UpdatedBrand", actualizado.getMarca());
        assertEquals("UpdatedModel", actualizado.getModelo());
    }

    @Test
    void testDeleteCar() {
        Coche c = cocheRepository.save(new Coche("DEL123", "Honda", "Civic"));
        Long id = c.getId();
        cocheRepository.delete(c);
        entityManager.flush();
        assertFalse(cocheRepository.findById(id).isPresent());
    }

    @Test
    void testAddMecanico() {
        Mecanico m = new Mecanico("Carlos", "Lopez");
        mecanicoRepository.save(m);
        entityManager.flush();
        assertNotNull(m.getId());
    }

    @Test
    void testUpdateMecanico() {
        Mecanico m = mecanicoRepository.findAll().get(0);
        m.setNombre("UpdatedName");
        mecanicoRepository.save(m);
        entityManager.flush();
        Mecanico actualizado = mecanicoRepository.findById(m.getId()).orElseThrow();
        assertEquals("UpdatedName", actualizado.getNombre());
    }

    @Test
    void testDeleteMecanico() {
        Mecanico m = mecanicoRepository.save(new Mecanico("Eliminar", "Apellido"));
        Long id = m.getId();
        mecanicoRepository.delete(m);
        entityManager.flush();
        assertFalse(mecanicoRepository.findById(id).isPresent());
    }

    @Test
    void testCrearCocheDuplicadoMatricula() {
        assertThrows(Exception.class, () -> {
            cocheRepository.save(new Coche("1111AAA", "Duplicado", "Test"));
            entityManager.flush();
        });
    }

    @Test
    void testCrearMecanicoDuplicadoNombre() {
        Mecanico m = new Mecanico("Juan", "Pérez");
        mecanicoRepository.save(m);
        entityManager.flush();
        assertNotNull(m.getId());
    }

    // Tests de integración
    @Test
    void testRelacionCocheReparacion() {
        reparacionRepository.findAll().forEach(r -> {
            assertNotNull(r.getCoche());
            assertNotNull(r.getMecanico());
        });
    }

    @Test
    void testRelacionCocheMultipleReparaciones() {
        Coche c = cocheRepository.findAll().get(0);
        long count = reparacionRepository.findAll().stream()
                .filter(r -> r.getCoche().getId().equals(c.getId()))
                .count();
        assertTrue(count >= 1);
    }

    @Test
    void testAddReparacion() {
        Coche c = cocheRepository.findAll().get(0);
        Mecanico m = mecanicoRepository.findAll().get(0);
        Reparacion r = new Reparacion(c, m, LocalDate.now(), "Cambio aceite", 2, 50.0);
        reparacionRepository.save(r);
        entityManager.flush();
        assertNotNull(r.getId());
    }

    @Test
    void testActualizarReparacion() {
        Reparacion r = reparacionRepository.findAll().get(0);
        r.setDescripcion("Actualizado");
        r.setHoras(5);
        r.setPrecio(150.0);
        reparacionRepository.save(r);
        entityManager.flush();
        Reparacion actualizado = reparacionRepository.findById(r.getId()).orElseThrow();
        assertEquals("Actualizado", actualizado.getDescripcion());
        assertEquals(5, actualizado.getHoras());
        assertEquals(150.0, actualizado.getPrecio());
    }

    @Test
    void testEliminarReparacion() {
        Coche c = cocheRepository.findAll().get(0);
        Mecanico m = mecanicoRepository.findAll().get(0);
        Reparacion r = reparacionRepository.save(new Reparacion(c, m, LocalDate.now(), "Eliminar prueba", 1, 50.0));
        Long id = r.getId();
        reparacionRepository.delete(r);
        entityManager.flush();
        assertFalse(reparacionRepository.findById(id).isPresent());
    }

    @Test
    void testCrearReparacionSinCoche() {
        Mecanico m = mecanicoRepository.findAll().get(0);
        assertThrows(Exception.class, () -> {
            reparacionRepository.save(new Reparacion(null, m, LocalDate.now(), "Sin coche", 1, 50.0));
            entityManager.flush();
        });
    }

    @Test
    void testCrearReparacionSinMecanico() {
        Coche c = cocheRepository.findAll().get(0);
        assertThrows(Exception.class, () -> {
            reparacionRepository.save(new Reparacion(c, null, LocalDate.now(), "Sin mecanico", 1, 50.0));
            entityManager.flush();
        });
    }

    // Tests de aceptación
    @Test
    void testCrearVariasReparaciones() {
        Coche c = cocheRepository.findAll().get(1);
        Mecanico m = mecanicoRepository.findAll().get(1);
        for (int i = 0; i < 5; i++) {
            reparacionRepository.save(new Reparacion(c, m, LocalDate.now(), "Multi " + i, 1, 30.0 + i));
        }
        entityManager.flush();
        long count = reparacionRepository.findAll().stream()
                .filter(r -> r.getCoche().getId().equals(c.getId()) && r.getMecanico().getId().equals(m.getId()))
                .count();
        assertTrue(count >= 5);
    }

    @Test
    void testEliminarCocheConReparaciones() {
        Coche c = cocheRepository.findAll().get(0);
        reparacionRepository.findAll().stream()
                .filter(r -> r.getCoche().getId().equals(c.getId()))
                .forEach(reparacionRepository::delete);
        entityManager.flush();
        cocheRepository.delete(c);
        entityManager.flush();
        assertFalse(cocheRepository.findById(c.getId()).isPresent());
    }

    @Test
    void testEliminarMecanicoConReparaciones() {
        Mecanico m = mecanicoRepository.findAll().get(0);
        reparacionRepository.findAll().stream()
                .filter(r -> r.getMecanico().getId().equals(m.getId()))
                .forEach(reparacionRepository::delete);
        entityManager.flush();
        mecanicoRepository.delete(m);
        entityManager.flush();
        assertFalse(mecanicoRepository.findById(m.getId()).isPresent());
    }

    @Test
    void testActualizarDescripcionReparacion() {
        Reparacion r = reparacionRepository.findAll().get(0);
        r.setDescripcion("Descripcion Actualizada");
        reparacionRepository.save(r);
        entityManager.flush();
        assertEquals("Descripcion Actualizada", reparacionRepository.findById(r.getId()).orElseThrow().getDescripcion());
    }

    @Test
    void testActualizarFechaReparacion() {
        Reparacion r = reparacionRepository.findAll().get(0);
        LocalDate nuevaFecha = LocalDate.now().minusDays(5);
        r.setFecha(nuevaFecha);
        reparacionRepository.save(r);
        entityManager.flush();
        assertEquals(nuevaFecha, reparacionRepository.findById(r.getId()).orElseThrow().getFecha());
    }

    @Test
    void testCrearCocheConMatriculaUnica() {
        Coche c = new Coche("UNIQUE123", "MarcaTest", "ModeloTest");
        cocheRepository.save(c);
        entityManager.flush();
        assertNotNull(c.getId());
    }

    @Test
    void testCrearMecanicoNuevo() {
        Mecanico m = new Mecanico("NuevoNombre", "NuevoApellido");
        mecanicoRepository.save(m);
        entityManager.flush();
        assertNotNull(m.getId());
    }

    @Test
    void testCrearReparacionVariasHorasYPrecio() {
        Coche c = cocheRepository.findAll().get(0);
        Mecanico m = mecanicoRepository.findAll().get(0);
        Reparacion r = new Reparacion(c, m, LocalDate.now(), "Varias horas y precio", 5, 200.0);
        reparacionRepository.save(r);
        entityManager.flush();
        Reparacion guardada = reparacionRepository.findById(r.getId()).orElseThrow();
        assertEquals(5, guardada.getHoras());
        assertEquals(200.0, guardada.getPrecio());
    }

    @Test
    void testActualizarTodosLosCamposReparacion() {
        Reparacion r = reparacionRepository.findAll().get(0);
        r.setDescripcion("Completa");
        r.setHoras(8);
        r.setPrecio(350.0);
        r.setFecha(LocalDate.now().minusDays(2));
        reparacionRepository.save(r);
        entityManager.flush();
        Reparacion actualizado = reparacionRepository.findById(r.getId()).orElseThrow();
        assertEquals("Completa", actualizado.getDescripcion());
        assertEquals(8, actualizado.getHoras());
        assertEquals(350.0, actualizado.getPrecio());
        assertEquals(LocalDate.now().minusDays(2), actualizado.getFecha());
    }

    @Test
void testCantidadCochesDespuesDeAdd() {
    int antes = cocheRepository.findAll().size();
    Coche c = new Coche("EXTRA123", "Ford", "Fiesta");
    cocheRepository.save(c);
    entityManager.flush();
    int despues = cocheRepository.findAll().size();
    assertEquals(antes + 1, despues);
}

@Test
void testCantidadMecanicosDespuesDeAdd() {
    int antes = mecanicoRepository.findAll().size();
    Mecanico m = new Mecanico("ExtraNombre", "ExtraApellido");
    mecanicoRepository.save(m);
    entityManager.flush();
    int despues = mecanicoRepository.findAll().size();
    assertEquals(antes + 1, despues);
}

@Test
void testCantidadReparacionesDespuesDeAdd() {
    int antes = reparacionRepository.findAll().size();
    Coche c = cocheRepository.findAll().get(0);
    Mecanico m = mecanicoRepository.findAll().get(0);
    Reparacion r = new Reparacion(c, m, LocalDate.now(), "Extra reparación", 2, 100.0);
    reparacionRepository.save(r);
    entityManager.flush();
    int despues = reparacionRepository.findAll().size();
    assertEquals(antes + 1, despues);
}
}