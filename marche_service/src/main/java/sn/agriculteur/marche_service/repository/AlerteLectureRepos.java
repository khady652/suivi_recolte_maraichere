package sn.agriculteur.marche_service.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.agriculteur.marche_service.entity.AlerteLecture;

import java.util.List;

    @Repository
    public interface AlerteLectureRepos
            extends JpaRepository<AlerteLecture, Integer> {

        boolean existsByAlerteIdAndIdDecideur(
                Integer idAlerte,
                Integer idDecideur);

        List<AlerteLecture> findByIdDecideur(Integer idDecideur);
    }

