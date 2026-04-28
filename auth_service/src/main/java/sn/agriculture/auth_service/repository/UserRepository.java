package sn.agriculture.auth_service.repository;


import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.agriculture.auth_service.entity.User;

import java.util.Optional;

    @Repository
    public interface UserRepository extends JpaRepository<User, Integer> {

        // Login par email
        Optional<User> findByEmail(String email);

        // Login par téléphone
        Optional<User> findByTelephone(String telephone);

        // Vérifier si email existe déjà
        boolean existsByEmail(String email);

        // Vérifier si téléphone existe déjà
        boolean existsByTelephone(String telephone);
    }

