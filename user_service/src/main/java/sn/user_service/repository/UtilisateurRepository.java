package sn.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.user_service.entity.Utilisateur;
import java.util.Optional;

    @Repository
    public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

        Optional<Utilisateur> findByEmail(String email);
        Optional<Utilisateur> findByTelephone(String telephone);
        boolean existsByEmail(String email);
        boolean existsByTelephone(String telephone);
    }

