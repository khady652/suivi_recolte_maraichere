package sn.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.user_service.entity.DecideurARM;

import java.util.Optional;

public interface DecideurRepo extends JpaRepository<DecideurARM, Integer> {
    Optional<DecideurARM> findByIdUtilisateur(Integer idUtilisateur);
}
