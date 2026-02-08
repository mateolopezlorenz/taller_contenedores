package mateolopez.taller.controller;

import mateolopez.taller.model.Mecanico;
import mateolopez.taller.repository.MecanicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mecanicos")
public class MecanicoController {

    @Autowired
    private MecanicoRepository mecanicoRepository;

    @GetMapping
    public List<Mecanico> getAllMecanicos() {
        return mecanicoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mecanico> getMecanicoById(@PathVariable Long id) {
        return mecanicoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
