package sn.user_service.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.user_service.dto.Requests.DirecteurRequest;
import sn.user_service.dto.Responses.DirecteurResponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.entity.DirecteurDRDR;
import sn.user_service.entity.DirecteurSDDR;
import sn.user_service.exception.UserException;
import sn.user_service.repository.DirecteurDrdrRepo;
import sn.user_service.repository.DirecteurSddrRepo;
import sn.user_service.repository.UtilisateurRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirecteurService {

    private final DirecteurDrdrRepo directeurDRRepository;
    private final DirecteurSddrRepo directeurSDDRRepository;
    private final UtilisateurRepository utilisateurRepository;

    // ══════════════════════════════════════════════════════
    //  DIRECTEUR DR
    // ══════════════════════════════════════════════════════

    // ── CRÉER DR ──────────────────────────────────────────
    @Transactional
    public MessageResponse creerDR(DirecteurRequest request) {

        if (request.getNom() == null || request.getNom().isEmpty())
            throw new UserException("Le nom est obligatoire");

        if (request.getEmail() != null &&
                utilisateurRepository.existsByEmail(request.getEmail()))
            throw new UserException("Cet email est déjà utilisé");

        DirecteurDRDR directeur = new DirecteurDRDR();
        if (request.getUserId() != null) {
            directeur.setIdUtilisateur(request.getUserId());
        }
        directeur.setNom(request.getNom());
        directeur.setPrenom(request.getPrenom());
        directeur.setAdresse(request.getAdresse());
        directeur.setEmail(request.getEmail());
        directeur.setTelephone(request.getTelephone());
        directeur.setSpecialite(request.getSpecialite());
        directeur.setRole("DIRECTEUR_DR");
        directeur.setActif(false);

        directeurDRRepository.save(directeur);
        log.info("Directeur DR créé : {}", request.getNom());

        return new MessageResponse(
                "Directeur DR " + request.getNom() +
                        " créé avec succès", true);
    }

    // ── LIRE TOUS DR ──────────────────────────────────────
    public List<DirecteurResponse> getAllDR() {
        return directeurDRRepository.findAll()
                .stream()
                .map(this::toDRResponse)
                .collect(Collectors.toList());
    }

    // ── LIRE UN DR ────────────────────────────────────────
    public DirecteurResponse getDRById(Integer id) {
        DirecteurDRDR directeur = directeurDRRepository
                .findById(id)
                .orElseThrow(() ->
                        new UserException("Directeur DR introuvable"));
        return toDRResponse(directeur);
    }

    // ── MODIFIER DR ───────────────────────────────────────
    @Transactional
    public MessageResponse updateDR(
            Integer id, DirecteurRequest request) {

        DirecteurDRDR directeur = directeurDRRepository
                .findById(id)
                .orElseThrow(() ->
                        new UserException("Directeur DR introuvable"));

        if (request.getNom() != null)
            directeur.setNom(request.getNom());
        if (request.getPrenom() != null)
            directeur.setPrenom(request.getPrenom());
        if (request.getAdresse() != null)
            directeur.setAdresse(request.getAdresse());
        if (request.getTelephone() != null)
            directeur.setTelephone(request.getTelephone());
        if (request.getSpecialite() != null)
            directeur.setSpecialite(request.getSpecialite());

        directeurDRRepository.save(directeur);
        return new MessageResponse(
                "Directeur DR modifié avec succès", true);
    }

    // ── SUPPRIMER DR ──────────────────────────────────────
    @Transactional
    public MessageResponse deleteDR(Integer id) {
        DirecteurDRDR directeur = directeurDRRepository
                .findById(id)
                .orElseThrow(() ->
                        new UserException("Directeur DR introuvable"));
        directeurDRRepository.delete(directeur);
        return new MessageResponse(
                "Directeur DR supprimé avec succès", true);
    }

    // ══════════════════════════════════════════════════════
    //  DIRECTEUR SDDR
    // ══════════════════════════════════════════════════════

    // ── CRÉER SDDR ────────────────────────────────────────
    @Transactional
    public MessageResponse creerSDDR(DirecteurRequest request) {

        if (request.getNom() == null || request.getNom().isEmpty())
            throw new UserException("Le nom est obligatoire");

        if (request.getEmail() != null &&
                utilisateurRepository.existsByEmail(request.getEmail()))
            throw new UserException("Cet email est déjà utilisé");

        DirecteurSDDR directeur = new DirecteurSDDR();
        directeur.setNom(request.getNom());
        directeur.setPrenom(request.getPrenom());
        directeur.setAdresse(request.getAdresse());
        directeur.setEmail(request.getEmail());
        directeur.setTelephone(request.getTelephone());
        directeur.setSpecialite(request.getSpecialite());
        directeur.setRole("DIRECTEUR_SDDR");
        directeur.setActif(false);

        if (request.getIdService() != null)
            directeur.setIdServiceSDDR(request.getIdService());

        directeurSDDRRepository.save(directeur);
        log.info("Directeur SDDR créé : {}", request.getNom());

        return new MessageResponse(
                "Directeur SDDR " + request.getNom() +
                        " créé avec succès", true);
    }

    // ── LIRE TOUS SDDR ────────────────────────────────────
    public List<DirecteurResponse> getAllSDDR() {
        return directeurSDDRRepository.findAll()
                .stream()
                .map(this::toSDDRResponse)
                .collect(Collectors.toList());
    }

    // ── LIRE UN SDDR ──────────────────────────────────────
    public DirecteurResponse getSDDRById(Integer id) {
        DirecteurSDDR directeur = directeurSDDRRepository
                .findById(id)
                .orElseThrow(() ->
                        new UserException("Directeur SDDR introuvable"));
        return toSDDRResponse(directeur);
    }

    // ── MODIFIER SDDR ─────────────────────────────────────
    @Transactional
    public MessageResponse updateSDDR(
            Integer id, DirecteurRequest request) {

        DirecteurSDDR directeur = directeurSDDRRepository
                .findById(id)
                .orElseThrow(() ->
                        new UserException("Directeur SDDR introuvable"));

        if (request.getNom() != null)
            directeur.setNom(request.getNom());
        if (request.getPrenom() != null)
            directeur.setPrenom(request.getPrenom());
        if (request.getAdresse() != null)
            directeur.setAdresse(request.getAdresse());
        if (request.getTelephone() != null)
            directeur.setTelephone(request.getTelephone());
        if (request.getSpecialite() != null)
            directeur.setSpecialite(request.getSpecialite());
        if (request.getIdService() != null)
            directeur.setIdServiceSDDR(request.getIdService());

        directeurSDDRRepository.save(directeur);
        return new MessageResponse(
                "Directeur SDDR modifié avec succès", true);
    }

    // ── SUPPRIMER SDDR ────────────────────────────────────
    @Transactional
    public MessageResponse deleteSDDR(Integer id) {
        DirecteurSDDR directeur = directeurSDDRRepository
                .findById(id)
                .orElseThrow(() ->
                        new UserException("Directeur SDDR introuvable"));
        directeurSDDRRepository.delete(directeur);
        return new MessageResponse(
                "Directeur SDDR supprimé avec succès", true);
    }

    // ══════════════════════════════════════════════════════
    //  MÉTHODES UTILITAIRES
    // ══════════════════════════════════════════════════════

    private DirecteurResponse toDRResponse(DirecteurDRDR d) {
        DirecteurResponse response = new DirecteurResponse();
        response.setIdUtilisateur(d.getIdUtilisateur());
        response.setNom(d.getNom());
        response.setPrenom(d.getPrenom());
        response.setAdresse(d.getAdresse());
        response.setEmail(d.getEmail());
        response.setTelephone(d.getTelephone());
        response.setSpecialite(d.getSpecialite());
        response.setActif(d.getActif());
        return response;
    }

    private DirecteurResponse toSDDRResponse(DirecteurSDDR d) {
        DirecteurResponse response = new DirecteurResponse();
        response.setIdUtilisateur(d.getIdUtilisateur());
        response.setNom(d.getNom());
        response.setPrenom(d.getPrenom());
        response.setAdresse(d.getAdresse());
        response.setEmail(d.getEmail());
        response.setTelephone(d.getTelephone());
        response.setSpecialite(d.getSpecialite());
        response.setActif(d.getActif());
        return response;
    }
    public DirecteurResponse getMonProfilDR(Integer userId) {
        DirecteurDRDR directeur = directeurDRRepository
                .findById(userId)
                .orElseThrow(() ->
                        new UserException("Profil introuvable"));
        return toDRResponse(directeur);
    }

    public DirecteurResponse getMonProfilSDDR(Integer userId) {
        DirecteurSDDR directeur = directeurSDDRRepository
                .findById(userId)
                .orElseThrow(() ->
                        new UserException("Profil introuvable"));
        return toSDDRResponse(directeur);
    }
}
