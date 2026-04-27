package sn.user_service.dto.Responses;


 import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 import sn.user_service.entity.EnqueteurMarche;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class EnqueteurResponse {
        private Integer idUtilisateur;
        private String nom;
        private String prenom;
        private String email;
        private String telephone;
        private String organisation;
        private String zoneAffectation;
        private Boolean actif;


        // ── MÉTHODE UTILITAIRE ────────────────────────────────
        private EnqueteurResponse toResponse(EnqueteurMarche e) {
            EnqueteurResponse response = new EnqueteurResponse();
            response.setIdUtilisateur(e.getIdUtilisateur());
            response.setNom(e.getNom());
            response.setPrenom(e.getPrenom());
            response.setEmail(e.getEmail());
            response.setTelephone(e.getTelephone());
            response.setOrganisation(e.getOrganisation());
            response.setZoneAffectation(e.getZoneAffectation());
            response.setActif(e.getActif());
            return response;
        }
    }