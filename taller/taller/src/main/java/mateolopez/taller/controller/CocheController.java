package mateolopez.taller.controller;

import mateolopez.taller.model.Coche;
import mateolopez.taller.repository.CocheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coches")
public class CocheController {

    @Autowired
    private CocheRepository cocheRepository;

    @GetMapping
    public List<Coche> getAllCoches() {
        return cocheRepository.findAll();
    }

    @PostMapping
    public Coche crearCoche(@RequestBody Coche coche) {
        return cocheRepository.save(coche);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coche> getCocheById(@PathVariable Long id) {
        return cocheRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<Coche> getCocheByMatricula(@PathVariable String matricula) {
        return cocheRepository.findByMatricula(matricula).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
