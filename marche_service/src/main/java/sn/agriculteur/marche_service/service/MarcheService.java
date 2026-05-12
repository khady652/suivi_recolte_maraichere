package sn.agriculteur.marche_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.agriculteur.marche_service.dto.request.MarcheRequest;
import sn.agriculteur.marche_service.dto.response.MarcheResponse;
import sn.agriculteur.marche_service.entity.Marche;
import sn.agriculteur.marche_service.exception.MarcheException;
import sn.agriculteur.marche_service.repository.MarcheRepos;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarcheService {

    private final MarcheRepos marcheRepos;

    // ── CRÉER ─────────────────────────────────────────────
    @Transactional
    public MarcheResponse creer(MarcheRequest request) {

        if (marcheRepos.existsByNomMarche(
                request.getNomMarche()))
            throw new MarcheException(
                    "Ce marché existe déjà !");

        Marche marche = Marche.builder()
                .nomMarche(request.getNomMarche())
                .type(request.getType())
                .lieu(request.getLieu())
                .build();

        marcheRepos.save(marche);
        log.info("Marché créé : {}", request.getNomMarche());

        return toResponse(marche);
    }

    // ── LIRE TOUS ─────────────────────────────────────────
    public List<MarcheResponse> getAll() {
        return marcheRepos.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── LIRE UN ───────────────────────────────────────────
    public MarcheResponse getById(Integer id) {
        Marche marche = marcheRepos
                .findById(id)
                .orElseThrow(() -> new MarcheException(
                        "Marché introuvable !"));
        return toResponse(marche);
    }

    // ── LIRE PAR TYPE ─────────────────────────────────────
    public List<MarcheResponse> getByType(String type) {
        return marcheRepos.findByType(type)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── LIRE PAR LIEU ─────────────────────────────────────
    public List<MarcheResponse> getByLieu(String lieu) {
        return marcheRepos.findByLieu(lieu)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── MODIFIER ──────────────────────────────────────────
    @Transactional
    public MarcheResponse modifier(Integer id,
                                   MarcheRequest request) {
        Marche marche = marcheRepos
                .findById(id)
                .orElseThrow(() -> new MarcheException(
                        "Marché introuvable !"));

        if (request.getNomMarche() != null)
            marche.setNomMarche(request.getNomMarche());
        if (request.getType() != null)
            marche.setType(request.getType());
        if (request.getLieu() != null)
            marche.setLieu(request.getLieu());

        marcheRepos.save(marche);
        return toResponse(marche);
    }

    // ── SUPPRIMER ─────────────────────────────────────────
    @Transactional
    public void supprimer(Integer id) {
        marcheRepos.findById(id)
                .orElseThrow(() -> new MarcheException(
                        "Marché introuvable !"));
        marcheRepos.deleteById(id);
    }

    // ── MAPPER ────────────────────────────────────────────
    private MarcheResponse toResponse(Marche m) {
        return MarcheResponse.builder()
                .idMarche(m.getIdMarche())
                .nomMarche(m.getNomMarche())
                .type(m.getType())
                .lieu(m.getLieu())
                .build();
    }
}