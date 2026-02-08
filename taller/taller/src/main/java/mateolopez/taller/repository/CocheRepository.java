package mateolopez.taller.repository;

import mateolopez.taller.model.Coche;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CocheRepository extends JpaRepository<Coche, Long> {
    Optional<Coche> findByMatricula(String matricula);
    List<Coche> findByMarca(String marca);
    List<Coche> findByModelo(String modelo);
}
