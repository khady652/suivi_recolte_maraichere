package sn.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.user_service.entity.ChefCooperatif;

import java.util.Optional;

public interface ChefCooperatifRepo extends JpaRepository<ChefCooperatif, Integer> {
    Optional<ChefCooperatif> findByCooperativeIdCooperation(
            Integer idCooperation);
    Optional<ChefCooperatif> findByIdUtilisateur(Integer idUtilisateur);
}
