package sn.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.user_service.entity.Agriculteur;

import java.util.List;

@Repository
public interface AgriculteurRepo extends JpaRepository<Agriculteur, Integer> {

    // Trouver les agriculteurs d'une coopérative
    List<Agriculteur> findByCooperativeIdCooperation(Integer idCooperation);

    // Trouver les agriculteurs sans coopérative
    List<Agriculteur> findByCooperativeIsNull();


}
