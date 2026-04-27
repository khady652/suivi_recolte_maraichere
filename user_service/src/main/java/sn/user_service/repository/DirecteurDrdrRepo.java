package sn.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.user_service.entity.DirecteurDRDR;

public interface DirecteurDrdrRepo extends JpaRepository<DirecteurDRDR, Integer> {
}
