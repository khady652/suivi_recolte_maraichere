package sn.agriculture.geo_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.agriculture.geo_service.entity.ServiceDepartementale;

import java.util.List;

public interface ServiceDepRepos extends JpaRepository<ServiceDepartementale, Integer> {
    boolean existsByDepartementIdDepartement(Integer idDepartement);
    List<ServiceDepartementale> findByDepartementRegionIdRegion(
            Integer idRegion);
}
