package sn.agriculture.culture_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<Culture> findByParcelle_IdParcelIn(List<Long> idParcels);

    // ── HISTORIQUE ANNUEL ─────────────────────────────────
    @Query("SELECT YEAR(c.dateSemence), SUM(c.superficiCultive) " +
            "FROM Culture c " +
            "WHERE c.parcelle.idDepartement IN :idDepartements " +
            "GROUP BY YEAR(c.dateSemence) " +
            "ORDER BY YEAR(c.dateSemence) ASC")
    List<Object[]> historiqueSurfaceParDepartements(
            @Param("idDepartements") List<Long> idDepartements);

    // ── SURFACE ANNÉE EN COURS ────────────────────────────
    @Query("SELECT SUM(c.superficiCultive) " +
            "FROM Culture c " +
            "WHERE c.parcelle.idDepartement IN :idDepartements " +
            "AND YEAR(c.dateSemence) = :annee")
    Double surfaceAnneeCourante(
            @Param("idDepartements") List<Long> idDepartements,
            @Param("annee") int annee);
}