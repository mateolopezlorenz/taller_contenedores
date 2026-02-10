package mateolopez.taller.service;

import mateolopez.taller.model.Coche;
import mateolopez.taller.repository.CocheRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CocheService {

    private final CocheRepository cocheRepository;

    public CocheService(CocheRepository cocheRepository) {
        this.cocheRepository = cocheRepository;
    }

    public List<Coche> findAll() {
        return cocheRepository.findAll();
    }

    public Optional<Coche> findById(Long id) {
        return cocheRepository.findById(id);
    }

    public Optional<Coche> findByMatricula(String matricula) {
        return cocheRepository.findByMatricula(matricula);
    }

    public List<Coche> findByMarca(String marca) {
        return cocheRepository.findByMarca(marca);
    }

    public List<Coche> findByModelo(String modelo) {
        return cocheRepository.findByModelo(modelo);
    }

    public Coche save(Coche coche) {
        return cocheRepository.save(coche);
    }

    public void deleteById(Long id) {
        cocheRepository.deleteById(id);
    }
}
