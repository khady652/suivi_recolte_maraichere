package sn.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.user_service.entity.DirecteurDRDR;
import sn.user_service.entity.DirecteurSDDR;

import java.util.Optional;

public interface DirecteurSddrRepo extends JpaRepository<DirecteurSDDR, Integer> {
    boolean existsByIdServiceDepartementale(Integer idServiceDepartementale);
    Optional<DirecteurSDDR> findByIdUtilisateur(Integer idUtilisateur);
}
