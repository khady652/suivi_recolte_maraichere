package sn.agriculteur.marche_service.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.agriculteur.marche_service.entity.CollecteDonnees;

import java.time.LocalDate;
import java.util.List;

    @Repository
    public interface CollecteRepos extends JpaRepository<CollecteDonnees, Integer> {

        // Par marché
        List<CollecteDonnees> findByMarcheIdMarche(Integer idMarche);

        // Par produit
        List<CollecteDonnees> findByProduit(String produit);

        // Par enquêteur
        List<CollecteDonnees> findByIdEnqueteur(Integer idEnqueteur);

        // Par date
        List<CollecteDonnees> findByDateCollecteBetween(
                LocalDate debut, LocalDate fin);

        // Par produit et marché
        List<CollecteDonnees> findByProduitAndMarcheIdMarche(
                String produit, Integer idMarche);

        // Prix moyen par produit
        @Query("SELECT c.produit, AVG(c.prixUnitaire) " +
                "FROM CollecteDonnees c " +
                "GROUP BY c.produit")
        List<Object[]> prixMoyenParProduit();

        // Stock disponible par produit
        @Query("SELECT c.produit, SUM(c.quantiteDisponible) " +
                "FROM CollecteDonnees c " +
                "WHERE c.dateCollecte = :date " +
                "GROUP BY c.produit")
        List<Object[]> stockParProduitEtDate(
                @org.springframework.data.repository.query.Param("date")
                LocalDate date);

        @Query("SELECT c FROM CollecteDonnees c " +
                "WHERE c.idCollecte = (" +
                "  SELECT MAX(c2.idCollecte) FROM CollecteDonnees c2 " +
                "  WHERE c2.produit = c.produit " +
                "  AND c2.marche = c.marche" +        // ← par produit ET par marché
                ")")
        List<CollecteDonnees> derniersPrixParProduitEtMarche();
        // Variation prix par mois
        @Query("SELECT c FROM CollecteDonnees c " +
                "WHERE LOWER(c.produit) = LOWER(:produit) " +
                "AND YEAR(c.dateCollecte) = :annee " +
                "AND (:mois IS NULL OR MONTH(c.dateCollecte) = :mois) " +
                "ORDER BY c.dateCollecte ASC")
        List<CollecteDonnees> findVariationParProduit(
                @Param("produit") String produit,
                @Param("annee") int annee,
                @Param("mois") Integer mois);
        @Query("SELECT MONTH(c.dateCollecte), " +
                "AVG(c.prixUnitaire), " +
                "SUM(c.quantiteDisponible) " +
                "FROM CollecteDonnees c " +
                "WHERE LOWER(c.produit) = LOWER(:produit) " +
                "AND YEAR(c.dateCollecte) = :annee " +
                "GROUP BY MONTH(c.dateCollecte) " +
                "ORDER BY MONTH(c.dateCollecte) ASC")
        List<Object[]> statsMenuellesParProduit(
                @Param("produit") String produit,
                @Param("annee") int annee);
    }

