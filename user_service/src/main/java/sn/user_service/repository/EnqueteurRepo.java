package sn.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.user_service.entity.DirecteurSDDR;
import sn.user_service.entity.EnqueteurMarche;

import java.util.List;
import java.util.Optional;

public interface EnqueteurRepo extends JpaRepository<EnqueteurMarche, Integer> {
    // Trouver par zone d'affectation
    List<EnqueteurMarche> findByZoneAffectation(String zone);
    Optional<EnqueteurMarche> findByIdUtilisateur(Integer idUtilisateur);
}
