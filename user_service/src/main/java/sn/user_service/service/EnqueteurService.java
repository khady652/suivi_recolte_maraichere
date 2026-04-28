package sn.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.user_service.dto.Requests.EnqueteurRequest;
import sn.user_service.dto.Responses.EnqueteurResponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.entity.EnqueteurMarche;
import sn.user_service.exception.UserException;
import sn.user_service.repository.EnqueteurRepo;
import sn.user_service.repository.UtilisateurRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnqueteurService {

    private final EnqueteurRepo enqueteurRepository;
    private final UtilisateurRepository utilisateurRepository;

    // ── CRÉER ─────────────────────────────────────────────
    @Transactional
    public MessageResponse creer(EnqueteurRequest request) {

        if (request.getNom() == null || request.getNom().isEmpty())
            throw new UserException("Le nom est obligatoire");

        if (request.getEmail() != null &&
                utilisateurRepository.existsByEmail(request.getEmail()))
            throw new UserException("Cet email est déjà utilisé");

        EnqueteurMarche enqueteur = new EnqueteurMarche();
        if (request.getUserId() != null) {
            enqueteur.setIdUtilisateur(request.getUserId());
        }
        enqueteur.setNom(request.getNom());
        enqueteur.setPrenom(request.getPrenom());
        enqueteur.setAdresse(request.getAdresse());
        enqueteur.setEmail(request.getEmail());
        enqueteur.setTelephone(request.getTelephone());
        enqueteur.setOrganisation(request.getOrganisation());
        enqueteur.setZoneAffectation(request.getZoneAffectation());
        enqueteur.setRole("ENQUETEUR_MARCHE");
        enqueteur.setActif(false);

        enqueteurRepository.save(enqueteur);
        log.info("Enquêteur créé : {}", request.getNom());

        return new MessageResponse(
                "Enquêteur " + request.getNom() +
                        " créé avec succès", true);
    }

    // ── LIRE TOUS ─────────────────────────────────────────
    public List<EnqueteurResponse> getAll() {
        return enqueteurRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── LIRE UN ───────────────────────────────────────────
    public EnqueteurResponse getById(Integer id) {
        EnqueteurMarche enqueteur = enqueteurRepository
                .findById(id)
                .orElseThrow(() ->
                        new UserException("Enquêteur introuvable"));
        return toResponse(enqueteur);
    }

    // ── MODIFIER ──────────────────────────────────────────
    @Transactional
    public MessageResponse update(
            Integer id, EnqueteurRequest request) {

        EnqueteurMarche enqueteur = enqueteurRepository
                .findById(id)
                .orElseThrow(() ->
                        new UserException("Enquêteur introuvable"));

        if (request.getNom() != null)
            enqueteur.setNom(request.getNom());
        if (request.getPrenom() != null)
            enqueteur.setPrenom(request.getPrenom());
        if (request.getAdresse() != null)
            enqueteur.setAdresse(request.getAdresse());
        if (request.getTelephone() != null)
            enqueteur.setTelephone(request.getTelephone());
        if (request.getOrganisation() != null)
            enqueteur.setOrganisation(request.getOrganisation());
        if (request.getZoneAffectation() != null)
            enqueteur.setZoneAffectation(request.getZoneAffectation());

        enqueteurRepository.save(enqueteur);
        return new MessageResponse(
                "Enquêteur modifié avec succès", true);
    }

    // ── SUPPRIMER ─────────────────────────────────────────
    @Transactional
    public MessageResponse delete(Integer id) {
        EnqueteurMarche enqueteur = enqueteurRepository
                .findById(id)
                .orElseThrow(() ->
                        new UserException("Enquêteur introuvable"));
        enqueteurRepository.delete(enqueteur);
        return new MessageResponse(
                "Enquêteur supprimé avec succès", true);
    }

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
    public EnqueteurResponse getMonProfil(Integer userId) {
        EnqueteurMarche enqueteur = enqueteurRepository
                .findById(userId)
                .orElseThrow(() ->
                        new UserException("Profil introuvable"));
        return toResponse(enqueteur);
    }
}
