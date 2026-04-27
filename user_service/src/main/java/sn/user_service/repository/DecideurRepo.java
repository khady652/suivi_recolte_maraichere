package sn.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.user_service.entity.DecideurARM;

public interface DecideurRepo extends JpaRepository<DecideurARM, Integer> {

}
