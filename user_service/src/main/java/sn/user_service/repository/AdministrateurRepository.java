package sn.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.user_service.entity.Administrateur;

public interface AdministrateurRepository  extends JpaRepository<Administrateur, Integer> {
}
