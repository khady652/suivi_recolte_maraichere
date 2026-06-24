package sn.agriculteur.marche_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.agriculteur.marche_service.entity.Alerte;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlerteRepos extends JpaRepository<Alerte, Integer> {

    List<Alerte> findAllByOrderByDateCreationDesc();

    boolean existsByPhaseAndNiveauAndDateCreationAfter(
            Integer phase,
            String niveau,
            LocalDateTime date);
}
