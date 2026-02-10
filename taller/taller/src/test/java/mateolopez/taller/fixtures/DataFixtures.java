package mateolopez.taller.fixtures;

import mateolopez.taller.model.Coche;
import mateolopez.taller.model.Mecanico;
import mateolopez.taller.model.Reparacion;
import mateolopez.taller.repository.CocheRepository;
import mateolopez.taller.repository.MecanicoRepository;
import mateolopez.taller.repository.ReparacionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataFixtures {

    @Bean
    CommandLineRunner initData(CocheRepository cocheRepository,
                               MecanicoRepository mecanicoRepository,
                               ReparacionRepository reparacionRepository) {
        return args -> {
            List<Mecanico> mecanicos = new ArrayList<>();
            mecanicos.add(mecanicoRepository.save(new Mecanico("Juan", "Pérez")));
            mecanicos.add(mecanicoRepository.save(new Mecanico("Ana", "Gómez")));
            mecanicos.add(mecanicoRepository.save(new Mecanico("Luis", "Martínez")));
            mecanicos.add(mecanicoRepository.save(new Mecanico("María", "López")));
            mecanicos.add(mecanicoRepository.save(new Mecanico("Carlos", "Fernández")));

            List<Coche> coches = new ArrayList<>();
            coches.add(cocheRepository.save(new Coche("1111AAA", "Toyota", "Corolla")));
            coches.add(cocheRepository.save(new Coche("2222BBB", "Ford", "Fiesta")));
            coches.add(cocheRepository.save(new Coche("3333CCC", "Honda", "Civic")));
            coches.add(cocheRepository.save(new Coche("4444DDD", "BMW", "Serie 3")));
            coches.add(cocheRepository.save(new Coche("5555EEE", "Audi", "A4")));
            coches.add(cocheRepository.save(new Coche("6666FFF", "Mercedes", "C200")));
            coches.add(cocheRepository.save(new Coche("7777GGG", "Kia", "Ceed")));
            coches.add(cocheRepository.save(new Coche("8888HHH", "Nissan", "Leaf")));
            coches.add(cocheRepository.save(new Coche("9999III", "Seat", "Ibiza")));
            coches.add(cocheRepository.save(new Coche("0000JJJ", "Volkswagen", "Golf")));

            for (int i = 0; i < 10; i++) {
                reparacionRepository.save(new Reparacion(
                        coches.get(i),
                        mecanicos.get(i % mecanicos.size()),
                        "Reparación ejemplo " + (i + 1)
                ));
            }
        };
    }
}
