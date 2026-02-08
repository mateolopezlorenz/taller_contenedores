package mateolopez.taller;

import mateolopez.taller.model.Mecanico;
import mateolopez.taller.repository.MecanicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MecanicoTest {

    @Autowired
    private MecanicoRepository mecanicoRepository;

    @BeforeEach
    void setUp() {
        mecanicoRepository.deleteAll(); 

        //Creamos y guardamos mecánicos para poder realizar el test
        List<Mecanico> mecanicos = List.of(
                new Mecanico("Juan Pérez", "Motor"),
                new Mecanico("Ana López", "Carrocería"),
                new Mecanico("Carlos Ruiz", "Electricidad"),
                new Mecanico("Lucía Gómez", "Motor"),
                new Mecanico("Pedro Sánchez", "Suspensión"),
                new Mecanico("Marta Díaz", "Carrocería")
        );

        mecanicoRepository.saveAll(mecanicos);
    }
}
