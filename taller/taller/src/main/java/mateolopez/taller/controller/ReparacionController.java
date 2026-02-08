package mateolopez.taller.controller;

import mateolopez.taller.model.Reparacion;
import mateolopez.taller.repository.ReparacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reparaciones")
public class ReparacionController {

    @Autowired
    private ReparacionRepository reparacionRepository;

    @GetMapping
    public List<Reparacion> getAllReparaciones() {
        return reparacionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reparacion> getReparacionById(@PathVariable Long id) {
        return reparacionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/coche/{id}")
    public List<Reparacion> getReparacionesByCoche(@PathVariable Long id) {
        return reparacionRepository.findByCocheId(id);
    }

    @GetMapping("/mecanico/{id}")
    public List<Reparacion> getReparacionesByMecanico(@PathVariable Long id) {
        return reparacionRepository.findByMecanicoId(id);
    }
}
