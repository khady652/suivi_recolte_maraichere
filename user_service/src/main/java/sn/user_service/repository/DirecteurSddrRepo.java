package sn.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.user_service.entity.DirecteurSDDR;

public interface DirecteurSddrRepo extends JpaRepository<DirecteurSDDR, Integer> {
}
