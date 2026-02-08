package mateolopez.taller;

import mateolopez.taller.model.Coche;
import mateolopez.taller.repository.CocheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CocheTest {

    @Autowired
    private CocheRepository cocheRepository;

    @BeforeEach
    void setUp() {
        cocheRepository.deleteAll(); 

        //Creamos y guardamos coches para los tests
        List<Coche> coches = List.of(
                new Coche("1234ABC", "Toyota", "Corolla"),
                new Coche("5678DEF", "Honda", "Civic"),
                new Coche("9012GHI", "Ford", "Focus"),
                new Coche("3456JKL", "BMW", "320i"),
                new Coche("7890MNO", "Audi", "A3"),
                new Coche("2345PQR", "Mercedes", "C200")
        );

        cocheRepository.saveAll(coches);
    }
}
