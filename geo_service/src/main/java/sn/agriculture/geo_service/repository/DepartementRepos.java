package sn.agriculture.geo_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.agriculture.geo_service.entity.Departement;

import java.util.List;

public interface DepartementRepos extends JpaRepository<Departement, Integer> {
    boolean existsByNomDepartement(String nomDepartement);
    List<Departement> findByRegionIdRegion(Integer idRegion);
}
