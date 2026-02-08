package mateolopez.taller.fixtures;

import mateolopez.taller.model.Coche;
import mateolopez.taller.model.Mecanico;
import mateolopez.taller.model.Reparacion;
import mateolopez.taller.repository.CocheRepository;
import mateolopez.taller.repository.MecanicoRepository;
import mateolopez.taller.repository.ReparacionRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@TestConfiguration
public class DataFixtures {

    @Bean
    public List<Mecanico> mecanicos(MecanicoRepository mecanicoRepository) {
        List<Mecanico> lista = new ArrayList<>();
        lista.add(mecanicoRepository.save(new Mecanico("Juan", "Pérez")));
        lista.add(mecanicoRepository.save(new Mecanico("Ana", "Gómez")));
        lista.add(mecanicoRepository.save(new Mecanico("Luis", "Martínez")));
        lista.add(mecanicoRepository.save(new Mecanico("María", "López")));
        lista.add(mecanicoRepository.save(new Mecanico("Carlos", "Fernández")));
        return lista;
    }

    @Bean
    public List<Coche> coches(CocheRepository cocheRepository) {
        List<Coche> lista = new ArrayList<>();
        lista.add(cocheRepository.save(new Coche("1111AAA", "Toyota", "Corolla")));
        lista.add(cocheRepository.save(new Coche("2222BBB", "Ford", "Fiesta")));
        lista.add(cocheRepository.save(new Coche("3333CCC", "Honda", "Civic")));
        lista.add(cocheRepository.save(new Coche("4444DDD", "BMW", "Serie 3")));
        lista.add(cocheRepository.save(new Coche("5555EEE", "Audi", "A4")));
        lista.add(cocheRepository.save(new Coche("6666FFF", "Mercedes", "C200")));
        lista.add(cocheRepository.save(new Coche("7777GGG", "Kia", "Ceed")));
        lista.add(cocheRepository.save(new Coche("8888HHH", "Nissan", "Leaf")));
        lista.add(cocheRepository.save(new Coche("9999III", "Seat", "Ibiza")));
        lista.add(cocheRepository.save(new Coche("0000JJJ", "Volkswagen", "Golf")));
        return lista;
    }

    @Bean
    public List<Reparacion> reparaciones(ReparacionRepository reparacionRepository, List<Coche> coches, List<Mecanico> mecanicos) {
        List<Reparacion> lista = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            lista.add(reparacionRepository.save(new Reparacion(
                coches.get(i),
                mecanicos.get(i % mecanicos.size()),
                "Reparación ejemplo " + (i + 1)
            )));
        }
        return lista;
    }
}
