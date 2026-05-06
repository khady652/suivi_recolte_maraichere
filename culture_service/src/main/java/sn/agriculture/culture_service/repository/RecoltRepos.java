package sn.agriculture.culture_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.agriculture.culture_service.entity.Recolte;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
    public interface RecoltRepos extends JpaRepository<Recolte,Integer> {

    List<Recolte> findByCulture_IdCulture(Long idCulture);
    List<Recolte> findByDateRecolteBetween(LocalDate debut, LocalDate fin);

    @Query("SELECT c.variete, SUM(r.quantiteRecolte) " +
            "FROM Recolte r JOIN r.culture c " +
            "GROUP BY c.variete")
    List<Object[]> cumulQuantiteParVariete();

    // ✅ Ajout pour le réentraînement ML
    @Query("SELECT r FROM Recolte r WHERE r.dateRecolte >= :depuis")
    List<Recolte> findRecentRecoltes(
            @org.springframework.data.repository.query.Param("depuis")
            LocalDate depuis);

    // Compter les nouvelles récoltes pour déclencher réentraînement
    Long countByDateRecolteAfter(LocalDate date);
}
