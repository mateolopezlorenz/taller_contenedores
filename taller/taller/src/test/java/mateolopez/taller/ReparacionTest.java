package mateolopez.taller;

import mateolopez.taller.model.Coche;
import mateolopez.taller.model.Mecanico;
import mateolopez.taller.model.Reparacion;
import mateolopez.taller.repository.CocheRepository;
import mateolopez.taller.repository.MecanicoRepository;
import mateolopez.taller.repository.ReparacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class ReparacionTest {

    @Autowired
    private ReparacionRepository reparacionRepository;

    @Autowired
    private CocheRepository cocheRepository;

    @Autowired
    private MecanicoRepository mecanicoRepository;

    @BeforeEach
    void setUp() {
        reparacionRepository.deleteAll(); 

        List<Coche> coches = cocheRepository.findAll();
        List<Mecanico> mecanicos = mecanicoRepository.findAll();

        List<Reparacion> reparaciones = List.of(
                new Reparacion(coches.get(0), mecanicos.get(0), LocalDate.now(), "Cambio de aceite", 2, 50.0),
                new Reparacion(coches.get(1), mecanicos.get(1), LocalDate.now(), "Reparación de frenos", 3, 120.0),
                new Reparacion(coches.get(2), mecanicos.get(2), LocalDate.now(), "Revisión eléctrica", 4, 200.0),
                new Reparacion(coches.get(0), mecanicos.get(1), LocalDate.now(), "Cambio de ruedas", 1, 80.0),
                new Reparacion(coches.get(1), mecanicos.get(0), LocalDate.now(), "Alineación", 2, 60.0),
                new Reparacion(coches.get(3), mecanicos.get(3), LocalDate.now(), "Pintura", 5, 300.0)
        );

        reparacionRepository.saveAll(reparaciones);
    }
}
