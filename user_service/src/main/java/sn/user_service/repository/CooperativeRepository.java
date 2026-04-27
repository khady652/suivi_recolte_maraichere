package sn.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.user_service.entity.Cooperative;
import java.util.Optional;

@Repository
public interface CooperativeRepository extends JpaRepository<Cooperative, Integer> {

    // Chercher par nom
    Optional<Cooperative> findByNomCooperative(String nomCooperative);

    // Vérifier si le nom existe
    boolean existsByNomCooperative(String nomCooperative);
}