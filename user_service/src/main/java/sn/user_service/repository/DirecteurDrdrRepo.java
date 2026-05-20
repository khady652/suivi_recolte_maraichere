package sn.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.user_service.entity.Agriculteur;
import sn.user_service.entity.DecideurARM;
import sn.user_service.entity.DirecteurDRDR;

import java.util.Optional;

public interface DirecteurDrdrRepo extends JpaRepository<DirecteurDRDR, Integer> {
    boolean existsByIdServiceRegional(Integer idServiceRegional);
    Optional<DirecteurDRDR> findByIdUtilisateur(Integer idUtilisateur);
}
