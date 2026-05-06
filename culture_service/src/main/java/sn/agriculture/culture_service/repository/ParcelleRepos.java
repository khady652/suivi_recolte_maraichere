package sn.agriculture.culture_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.agriculture.culture_service.entity.Parcelle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ParcelleRepos extends JpaRepository<Parcelle, Long> {
    List<Parcelle> findByIdAgriculteur(Long idAgriculteur);
    List<Parcelle> findByIdDepartement(Long idDepartement);
    List<Parcelle> findByIdDepartementIn(List<Long> idDepartements);
    List<Parcelle> findByIdAgriculteurIn(List<Long> idAgriculteurs);
    List<Parcelle> findByNomParcelleContainingIgnoreCase(String nom);
}