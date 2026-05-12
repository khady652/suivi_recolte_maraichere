package sn.agriculteur.marche_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.agriculteur.marche_service.entity.Marche;

import java.util.List;

public interface MarcheRepos extends JpaRepository<Marche, Integer> {
    List<Marche> findByType(String type);
    List<Marche> findByLieu(String lieu);
    boolean existsByNomMarche(String nomMarche);
}

