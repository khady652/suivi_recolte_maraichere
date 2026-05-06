package sn.agriculture.culture_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.agriculture.culture_service.entity.Culture;

import java.util.List;

@Repository
public interface CultureRepos extends JpaRepository<Culture, Long> {
    List<Culture> findByParcelle_IdParcel(Long idParcel);
    List<Culture> findByVariete(String variete);
    List<Culture> findBySaison(String saison);
    List<Culture> findByVarieteAndSaison(String variete, String saison);
    List<Culture> findByType(String type);
}