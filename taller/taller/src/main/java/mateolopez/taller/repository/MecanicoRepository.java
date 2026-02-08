package mateolopez.taller.repository;

import mateolopez.taller.model.Mecanico;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MecanicoRepository extends JpaRepository<Mecanico, Long> {
    Optional<Mecanico> findById(Long id);
}
