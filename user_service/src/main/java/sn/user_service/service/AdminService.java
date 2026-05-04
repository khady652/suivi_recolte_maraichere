package sn.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.user_service.Client.AuthServiceClient;
import sn.user_service.dto.Requests.AdminRequest;
import sn.user_service.dto.Responses.AdminResponse;
import sn.user_service.dto.Responses.MessageResponse;
import sn.user_service.entity.Administrateur;
import sn.user_service.exception.UserException;
import sn.user_service.repository.AdministrateurRepository;
import sn.user_service.repository.UtilisateurRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final AdministrateurRepository administrateurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AuthServiceClient authServiceClient; // ✅ Ajout

    // ── CRÉER ─────────────────────────────────────────────
    @Transactional
    public MessageResponse creer(AdminRequest request) {

        if (request.getNom() == null || request.getNom().isEmpty())
            throw new UserException("Le nom est obligatoire");

        if (request.getEmail() != null &&
                utilisateurRepository.existsByEmail(request.getEmail()))
            throw new UserException("Cet email est déjà utilisé");

        // ✅ Créer le compte dans auth-service
        Integer userId = authServiceClient.createAccount(
                request.getEmail(),
                request.getTelephone(),
                "ADMINISTRATEUR"
        );

        Administrateur admin = new Administrateur();
        admin.setIdUtilisateur(userId); // ✅ userId depuis auth-service
        admin.setNom(request.getNom());
        admin.setPrenom(request.getPrenom());
        admin.setAdresse(request.getAdresse());
        admin.setEmail(request.getEmail());
        admin.setTelephone(request.getTelephone());
        admin.setRole("ADMINISTRATEUR");
        admin.setActif(true); // ✅ true par défaut

        administrateurRepository.save(admin);
        log.info("Administrateur créé : {}", request.getNom());

        return new MessageResponse(
                "Administrateur " + request.getNom() +
                        " créé avec succès", true);
    }

    // ── LIRE TOUS ─────────────────────────────────────────
    public List<AdminResponse> getAll() {
        return administrateurRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── LIRE UN ───────────────────────────────────────────
    public AdminResponse getById(Integer id) {
        Administrateur admin = administrateurRepository
                .findById(id)
                .orElseThrow(() ->
                        new UserException("Administrateur introuvable"));
        return toResponse(admin);
    }

    // ── MODIFIER ──────────────────────────────────────────
    @Transactional
    public MessageResponse update(
            Integer id, AdminRequest request) {

        Administrateur admin = administrateurRepository
                .findById(id)
                .orElseThrow(() ->
                        new UserException("Administrateur introuvable"));

        if (request.getNom() != null)
            admin.setNom(request.getNom());
        if (request.getPrenom() != null)
            admin.setPrenom(request.getPrenom());
        if (request.getAdresse() != null)
            admin.setAdresse(request.getAdresse());
        if (request.getTelephone() != null)
            admin.setTelephone(request.getTelephone());

        administrateurRepository.save(admin);
        return new MessageResponse(
                "Administrateur modifié avec succès", true);
    }

    // ── ACTIVER COMPTE ────────────────────────────────────
    @Transactional
    public MessageResponse activerCompte(Integer id) {
        Administrateur admin = administrateurRepository
                .findById(id)
                .orElseThrow(() ->
                        new UserException("Administrateur introuvable"));
        admin.setActif(true);
        administrateurRepository.save(admin);
        return new MessageResponse("Compte activé avec succès", true);
    }

    // ── DÉSACTIVER COMPTE ─────────────────────────────────
    @Transactional
    public MessageResponse desactiverCompte(Integer id) {
        Administrateur admin = administrateurRepository
                .findById(id)
                .orElseThrow(() ->
                        new UserException("Administrateur introuvable"));
        admin.setActif(false);
        administrateurRepository.save(admin);
        return new MessageResponse(
                "Compte désactivé avec succès", true);
    }

    // ── SUPPRIMER ─────────────────────────────────────────
    @Transactional
    public MessageResponse delete(Integer id) {
        Administrateur admin = administrateurRepository
                .findById(id)
                .orElseThrow(() ->
                        new UserException("Administrateur introuvable"));
        administrateurRepository.delete(admin);
        return new MessageResponse(
                "Administrateur supprimé avec succès", true);
    }

    // ── MÉTHODE UTILITAIRE ────────────────────────────────
    private AdminResponse toResponse(Administrateur a) {
        AdminResponse response = new AdminResponse();
        response.setIdUtilisateur(a.getIdUtilisateur());
        response.setNom(a.getNom());
        response.setPrenom(a.getPrenom());
        response.setEmail(a.getEmail());
        response.setTelephone(a.getTelephone());
        response.setActif(a.getActif());
        return response;
    }

    public AdminResponse getMonProfil(Integer userId) {
        Administrateur admin = administrateurRepository
                .findById(userId)
                .orElseThrow(() ->
                        new UserException("Profil introuvable"));
        return toResponse(admin);
    }
}