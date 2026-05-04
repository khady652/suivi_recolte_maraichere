package sn.agriculture.geo_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.agriculture.geo_service.entity.Region;

public interface RegionRepos extends JpaRepository<Region, Integer> {
    boolean existsByNomRegion(String nomRegion);
}
