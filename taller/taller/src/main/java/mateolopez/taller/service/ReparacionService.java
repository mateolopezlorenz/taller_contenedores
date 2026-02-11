package mateolopez.taller.service;

import mateolopez.taller.model.Reparacion;
import mateolopez.taller.repository.ReparacionRepository;
import mateolopez.taller.repository.CocheRepository;
import mateolopez.taller.repository.MecanicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReparacionService {

    private final ReparacionRepository reparacionRepository;
    private final CocheRepository cocheRepository;
    private final MecanicoRepository mecanicoRepository;

    public ReparacionService(
            ReparacionRepository reparacionRepository,
            CocheRepository cocheRepository,
            MecanicoRepository mecanicoRepository) {

        this.reparacionRepository = reparacionRepository;
        this.cocheRepository = cocheRepository;
        this.mecanicoRepository = mecanicoRepository;
    }

    public List<Reparacion> findAll() {
        return reparacionRepository.findAll();
    }

    public Optional<Reparacion> findById(Long id) {
        return reparacionRepository.findById(id);
    }

    public Reparacion save(Reparacion reparacion) {

        Long cocheId = reparacion.getCoche().getId();
        Long mecanicoId = reparacion.getMecanico().getId();

        if (!cocheRepository.existsById(cocheId)) {
            throw new IllegalArgumentException("El coche no existe");
        }

        if (!mecanicoRepository.existsById(mecanicoId)) {
            throw new IllegalArgumentException("El mecánico no existe");
        }

        return reparacionRepository.save(reparacion);
    }

    public void deleteById(Long id) {
        reparacionRepository.deleteById(id);
    }

    public List<Reparacion> findByCocheId(Long cocheId) {
        return reparacionRepository.findByCocheId(cocheId);
    }

    public List<Reparacion> findByMecanicoId(Long mecanicoId) {
        return reparacionRepository.findByMecanicoId(mecanicoId);
    }
}