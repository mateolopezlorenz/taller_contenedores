package mateolopez.taller;

import mateolopez.taller.model.Coche;
import mateolopez.taller.model.Mecanico;
import mateolopez.taller.model.Reparacion;
import mateolopez.taller.repository.MecanicoRepository;
import mateolopez.taller.repository.ReparacionRepository;
import mateolopez.taller.repository.CocheRepository;
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

    // ------------------------
    // Tests unitarios de repositorio (solo 5 primeros)
    // ------------------------

    @Test
    void testCantidadCoches() {
        // Deben haber 10 coches cargados por DataFixtures
        assertEquals(10, cocheRepository.findAll().size());
    }

    @Test
    void testCocheNoNull() {
        // Ningún coche debe ser null
        cocheRepository.findAll().forEach(c -> assertNotNull(c));
    }

    @Test
    void testIdsCoche() {
        // Todos los coches deben tener un ID asignado
        cocheRepository.findAll().forEach(c -> assertNotNull(c.getId()));
    }

    @Test
    void testMatriculasUnicas() {
        // Todas las matrículas deben ser únicas
        List<Coche> coches = cocheRepository.findAll();
        long uniqueCount = coches.stream().map(Coche::getMatricula).distinct().count();
        assertEquals(coches.size(), uniqueCount);
    }

    @Test
    void testDatosCoche1() {
        // El primer coche debe tener marca, modelo y matrícula
        Coche c = cocheRepository.findAll().get(0);
        assertNotNull(c.getMarca());
        assertNotNull(c.getModelo());
        assertNotNull(c.getMatricula());
    }

    // ------------------------
// Tests de integración (bloque siguiente 5)
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

    @Test void testCrearVariasReparaciones() {
    Coche c = cocheRepository.findAll().get(1);
    Mecanico m = mecanicoRepository.findAll().get(1);
    for (int i = 0; i < 5; i++) {
        reparacionRepository.save(
                new Reparacion(c, m, LocalDate.now(), "Multi " + i, 1, 30.0 + i));
    }
    long count = reparacionRepository.findAll().stream()
            .filter(r -> r.getCoche().getId().equals(c.getId()) &&
                         r.getMecanico().getId().equals(m.getId()))
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

// ------------------------
// Siguientes 5 tests de aceptación
// ------------------------

@Test void testActualizarCoche() {
    Coche c = cocheRepository.findAll().get(0);
    c.setMarca("UpdatedBrand");
    cocheRepository.save(c);
    entityManager.flush(); // forzar sincronización con BD
    assertEquals("UpdatedBrand",
            cocheRepository.findById(c.getId()).orElseThrow().getMarca());
}

@Test void testActualizarMecanico() {
    Mecanico m = mecanicoRepository.findAll().get(0);
    m.setNombre("UpdatedName");
    mecanicoRepository.save(m);
    entityManager.flush();
    assertEquals("UpdatedName",
            mecanicoRepository.findById(m.getId()).orElseThrow().getNombre());
}

@Test void testActualizarReparacion() {
    Reparacion r = reparacionRepository.findAll().get(0);
    r.setDescripcion("Actualizado");
    reparacionRepository.save(r);
    entityManager.flush();
    assertEquals("Actualizado",
            reparacionRepository.findById(r.getId()).orElseThrow().getDescripcion());
}

@Test void testEliminarCoche() {
    Coche c = cocheRepository.save(new Coche("ZZZZ999", "Renault", "Clio"));
    Long id = c.getId();
    cocheRepository.delete(c);
    entityManager.flush();
    assertFalse(cocheRepository.findById(id).isPresent());
}

@Test void testEliminarMecanico() {
    Mecanico m = mecanicoRepository.save(new Mecanico("Pedro", "Garcia"));
    Long id = m.getId();
    mecanicoRepository.delete(m);
    entityManager.flush();
    assertFalse(mecanicoRepository.findById(id).isPresent());
}

// ------------------------
// Siguientes 10 tests de aceptación
// ------------------------

@Test void testEliminarReparacion() {
    Coche c = cocheRepository.findAll().get(0);
    Mecanico m = mecanicoRepository.findAll().get(0);
    Reparacion r = reparacionRepository.save(new Reparacion(c, m, LocalDate.now(), "Eliminar prueba", 1, 50.0));
    Long id = r.getId();
    reparacionRepository.delete(r);
    entityManager.flush();
    assertFalse(reparacionRepository.findById(id).isPresent());
}

@Test void testCrearVariasReparaciones1() {
    Coche c = cocheRepository.findAll().get(1);
    Mecanico m = mecanicoRepository.findAll().get(1);
    for (int i = 0; i < 5; i++) {
        reparacionRepository.save(new Reparacion(c, m, LocalDate.now(), "Multi " + i, 1, 30.0 + i));
    }
    entityManager.flush();
    long count = reparacionRepository.findAll().stream()
            .filter(r -> r.getCoche().getId().equals(c.getId()))
            .count();
    assertTrue(count >= 5);
}

@Test void testActualizarHorasYPrecioReparacion1() {
    Reparacion r = reparacionRepository.findAll().get(0);
    r.setHoras(10);
    r.setPrecio(500.0);
    reparacionRepository.save(r);
    entityManager.flush();
    Reparacion actualizado = reparacionRepository.findById(r.getId()).orElseThrow();
    assertEquals(10, actualizado.getHoras());
    assertEquals(500.0, actualizado.getPrecio());
}

@Test void testCrearCocheDuplicadoMatricula1() {
    assertThrows(Exception.class, () -> {
        cocheRepository.save(new Coche("1111AAA", "Duplicado", "Test"));
        entityManager.flush();
    });
}

@Test void testCrearMecanicoDuplicadoNombre() {
    Mecanico m = new Mecanico("Juan", "Pérez");
    Mecanico guardado = mecanicoRepository.save(m);
    entityManager.flush();
    assertNotNull(guardado.getId());
}

@Test void testCrearReparacionSinCoche1() {
    Mecanico m = mecanicoRepository.findAll().get(0);
    assertThrows(Exception.class, () -> {
        reparacionRepository.save(new Reparacion(null, m, LocalDate.now(), "Sin coche", 1, 50.0));
        entityManager.flush();
    });
}

@Test void testCrearReparacionSinMecanico1() {
    Coche c = cocheRepository.findAll().get(0);
    assertThrows(Exception.class, () -> {
        reparacionRepository.save(new Reparacion(c, null, LocalDate.now(), "Sin mecanico", 1, 50.0));
        entityManager.flush();
    });
}

@Test void testActualizarMatriculaCoche() {
    Coche c = cocheRepository.findAll().get(0);
    String old = c.getMatricula();
    c.setMatricula("NEW1234");
    cocheRepository.save(c);
    entityManager.flush();
    assertEquals("NEW1234", cocheRepository.findById(c.getId()).orElseThrow().getMatricula());
    // revertir
    c.setMatricula(old);
    cocheRepository.save(c);
    entityManager.flush();
}

@Test void testActualizarMarcaYModeloCoche() {
    Coche c = cocheRepository.findAll().get(0);
    c.setMarca("MarcaTest");
    c.setModelo("ModeloTest");
    cocheRepository.save(c);
    entityManager.flush();
    Coche actualizado = cocheRepository.findById(c.getId()).orElseThrow();
    assertEquals("MarcaTest", actualizado.getMarca());
    assertEquals("ModeloTest", actualizado.getModelo());
}

@Test void testActualizarNombreYApellidoMecanico() {
    Mecanico m = mecanicoRepository.findAll().get(0);
    m.setNombre("NombreTest");
    mecanicoRepository.save(m);
    entityManager.flush();
    Mecanico actualizado = mecanicoRepository.findById(m.getId()).orElseThrow();
    assertEquals("NombreTest", actualizado.getNombre());
}

// ------------------------
// Últimos 10 tests de aceptación
// ------------------------

@Test void testEliminarCocheConReparaciones() {
    Coche c = cocheRepository.findAll().get(0);
    // eliminar sus reparaciones primero
    reparacionRepository.findAll().stream()
            .filter(r -> r.getCoche().getId().equals(c.getId()))
            .forEach(reparacionRepository::delete);
    entityManager.flush();
    cocheRepository.delete(c);
    entityManager.flush();
    assertFalse(cocheRepository.findById(c.getId()).isPresent());
}

@Test void testEliminarMecanicoConReparaciones() {
    Mecanico m = mecanicoRepository.findAll().get(0);
    reparacionRepository.findAll().stream()
            .filter(r -> r.getMecanico().getId().equals(m.getId()))
            .forEach(reparacionRepository::delete);
    entityManager.flush();
    mecanicoRepository.delete(m);
    entityManager.flush();
    assertFalse(mecanicoRepository.findById(m.getId()).isPresent());
}

@Test void testCrearReparacionVariasHorasYPrecio() {
    Coche c = cocheRepository.findAll().get(0);
    Mecanico m = mecanicoRepository.findAll().get(0);
    Reparacion r = new Reparacion(c, m, LocalDate.now(), "Varias horas y precio", 5, 200.0);
    reparacionRepository.save(r);
    entityManager.flush();
    Reparacion guardada = reparacionRepository.findById(r.getId()).orElseThrow();
    assertEquals(5, guardada.getHoras());
    assertEquals(200.0, guardada.getPrecio());
}

@Test void testActualizarDescripcionReparacion() {
    Reparacion r = reparacionRepository.findAll().get(0);
    r.setDescripcion("Descripcion Actualizada");
    reparacionRepository.save(r);
    entityManager.flush();
    assertEquals("Descripcion Actualizada",
            reparacionRepository.findById(r.getId()).orElseThrow().getDescripcion());
}

@Test void testActualizarFechaReparacion() {
    Reparacion r = reparacionRepository.findAll().get(0);
    LocalDate nuevaFecha = LocalDate.now().minusDays(5);
    r.setFecha(nuevaFecha);
    reparacionRepository.save(r);
    entityManager.flush();
    assertEquals(nuevaFecha,
            reparacionRepository.findById(r.getId()).orElseThrow().getFecha());
}

@Test void testNoPermitirCocheNullEnReparacion() {
    Mecanico m = mecanicoRepository.findAll().get(0);
    assertThrows(Exception.class, () -> {
        reparacionRepository.save(new Reparacion(null, m, LocalDate.now(), "Null coche", 1, 50.0));
        entityManager.flush();
    });
}

@Test void testNoPermitirMecanicoNullEnReparacion() {
    Coche c = cocheRepository.findAll().get(0);
    assertThrows(Exception.class, () -> {
        reparacionRepository.save(new Reparacion(c, null, LocalDate.now(), "Null mecanico", 1, 50.0));
        entityManager.flush();
    });
}

@Test void testCrearCocheConMatriculaUnica() {
    Coche c = new Coche("UNIQUE123", "MarcaTest", "ModeloTest");
    cocheRepository.save(c);
    entityManager.flush();
    assertNotNull(c.getId());
}

@Test void testCrearMecanicoNuevo() {
    Mecanico m = new Mecanico("NuevoNombre", "NuevoApellido");
    mecanicoRepository.save(m);
    entityManager.flush();
    assertNotNull(m.getId());
}

@Test void testActualizarTodosLosCamposReparacion() {
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

}
