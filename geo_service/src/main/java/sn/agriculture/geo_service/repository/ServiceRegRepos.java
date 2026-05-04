package sn.agriculture.geo_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import sn.agriculture.geo_service.entity.ServiceRegionale;

public interface ServiceRegRepos extends JpaRepository<ServiceRegionale, Integer> {
    boolean existsByRegionIdRegion(Integer idRegion);
}

