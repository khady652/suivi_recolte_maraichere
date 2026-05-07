package sn.agriculture.culture_service.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.agriculture.culture_service.dtos.request.RecolteRequest;
import sn.agriculture.culture_service.dtos.response.RecolteResponse;
import sn.agriculture.culture_service.entity.Culture;
import sn.agriculture.culture_service.entity.Recolte;
import sn.agriculture.culture_service.exception.CultureException;
import sn.agriculture.culture_service.repository.CultureRepos;
import sn.agriculture.culture_service.repository.ParcelleRepos;
import sn.agriculture.culture_service.repository.RecoltRepos;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class RecolteService {

        private final RecoltRepos recolteRepository;
        private final CultureRepos cultureRepository;
        private final ParcelleRepos parcelleRepository;
        // ── ENREGISTRER UNE RÉCOLTE ───────────────────────────
        // ── ENREGISTRER UNE RÉCOLTE ───────────────────────────
        @Transactional
        public RecolteResponse enregistrer(RecolteRequest request,
                                           Long userId, String role) {

            Culture culture = cultureRepository
                    .findById(request.getIdCulture())
                    .orElseThrow(() -> new CultureException(
                            "Culture introuvable !"));

            // ✅ Vérifier les droits selon le rôle
            if (role.equals("AGRICULTEUR")) {
                // Vérifier que la culture appartient à l'agriculteur
                if (!culture.getParcelle().getIdAgriculteur()
                        .equals(userId)) {
                    throw new CultureException(
                            "Cette culture ne vous appartient pas !");
                }
            }

            Recolte recolte = Recolte.builder()
                    .dateRecolte(request.getDateRecolte())
                    .quantiteRecolte(request.getQuantiteRecolte())
                    .culture(culture)
                    .build();

            recolteRepository.save(recolte);
            log.info("Récolte enregistrée pour culture {} par {}",
                    request.getIdCulture(), role);

            return toResponse(recolte);
        }
        // ── LIRE PAR CULTURE ──────────────────────────────────
        public List<RecolteResponse> getByCulture(Long idCulture) {
            return recolteRepository
                    .findByCulture_IdCulture(idCulture)
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── LIRE TOUTES ───────────────────────────────────────
        public List<RecolteResponse> getAll() {
            return recolteRepository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── LIRE UNE ──────────────────────────────────────────
        public RecolteResponse getById(Long id) {
            Recolte recolte = recolteRepository
                    .findById(id)
                    .orElseThrow(() -> new CultureException(
                            "Récolte introuvable !"));
            return toResponse(recolte);
        }

        // ── SUPPRIMER ─────────────────────────────────────────
        @Transactional
        public void supprimer(Long id) {
            recolteRepository.findById(id)
                    .orElseThrow(() -> new CultureException(
                            "Récolte introuvable !"));
            recolteRepository.deleteById(id);
        }

        // ── STATISTIQUES ──────────────────────────────────────
        public List<Object[]> getStatistiquesParVariete() {
            return recolteRepository.cumulQuantiteParVariete();
        }

        // ── MAPPER ────────────────────────────────────────────
        private RecolteResponse toResponse(Recolte r) {
            return RecolteResponse.builder()
                    .idRecolte(r.getIdRecolte())
                    .dateRecolte(r.getDateRecolte())
                    .quantiteRecolte(r.getQuantiteRecolte())
                    .idCulture(r.getCulture().getIdCulture())
                    .typeCulture(r.getCulture().getType())
                    .varieteCulture(r.getCulture().getVariete())
                    .nomParcelle(r.getCulture().getParcelle()
                            .getNomParcelle())
                    .quantiteRecoltePrevu(r.getCulture()
                            .getQuantiteRecoltePrevu())
                    .idDepartement(r.getCulture().getParcelle()
                            .getIdDepartement())
                    .build();
        }
        // ── RÉCOLTES PAR AGRICULTEUR ──────────────────────────
        public List<RecolteResponse> getByAgriculteur(Long idAgriculteur) {
            return cultureRepository
                    .findByParcelle_IdParcelIn(
                            parcelleRepository
                                    .findByIdAgriculteur(idAgriculteur)
                                    .stream()
                                    .map(p -> p.getIdParcel())
                                    .toList())
                    .stream()
                    .flatMap(c -> recolteRepository
                            .findByCulture_IdCulture(c.getIdCulture())
                            .stream())
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── RÉCOLTES PAR DÉPARTEMENT ──────────────────────────
        public List<RecolteResponse> getByDepartement(Long idDepartement) {
            return parcelleRepository
                    .findByIdDepartement(idDepartement)
                    .stream()
                    .flatMap(p -> cultureRepository
                            .findByParcelle_IdParcel(p.getIdParcel())
                            .stream())
                    .flatMap(c -> recolteRepository
                            .findByCulture_IdCulture(c.getIdCulture())
                            .stream())
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── RÉCOLTES PAR RÉGION ───────────────────────────────
        public List<RecolteResponse> getByRegion(List<Long> idDepartements) {
            return parcelleRepository
                    .findByIdDepartementIn(idDepartements)
                    .stream()
                    .flatMap(p -> cultureRepository
                            .findByParcelle_IdParcel(p.getIdParcel())
                            .stream())
                    .flatMap(c -> recolteRepository
                            .findByCulture_IdCulture(c.getIdCulture())
                            .stream())
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── RÉCOLTES PAR CHEF COOPERATIF ──────────────────────
        public List<RecolteResponse> getByChef(List<Long> idAgriculteurs) {
            return parcelleRepository
                    .findByIdAgriculteurIn(idAgriculteurs)
                    .stream()
                    .flatMap(p -> cultureRepository
                            .findByParcelle_IdParcel(p.getIdParcel())
                            .stream())
                    .flatMap(c -> recolteRepository
                            .findByCulture_IdCulture(c.getIdCulture())
                            .stream())
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── STATISTIQUES PAR VARIÉTÉ ──────────────────────────
        public Map<String, Double> getStatistiquesParVariete(
                List<RecolteResponse> recoltes) {
            return recoltes.stream()
                    .collect(Collectors.groupingBy(
                            RecolteResponse::getVarieteCulture,
                            Collectors.summingDouble(
                                    RecolteResponse::getQuantiteRecolte)));
        }

        // ── STATISTIQUES PAR TYPE ─────────────────────────────
        public Map<String, Double> getStatistiquesParType(
                List<RecolteResponse> recoltes) {
            return recoltes.stream()
                    .collect(Collectors.groupingBy(
                            RecolteResponse::getTypeCulture,
                            Collectors.summingDouble(
                                    RecolteResponse::getQuantiteRecolte)));
        }

        // ── COMPARER PRÉVU VS RÉEL ────────────────────────────
        public Map<String, Object> comparerPrevuReel(
                List<RecolteResponse> recoltes) {
            double totalPrevu = recoltes.stream()
                    .mapToDouble(r -> r.getQuantiteRecoltePrevu() != null
                            ? r.getQuantiteRecoltePrevu() : 0)
                    .sum();
            double totalReel = recoltes.stream()
                    .mapToDouble(RecolteResponse::getQuantiteRecolte)
                    .sum();
            double taux = totalPrevu > 0
                    ? (totalReel / totalPrevu) * 100 : 0;

            return Map.of(
                    "totalPrevu", totalPrevu,
                    "totalReel", totalReel,
                    "tauxRealisation", Math.round(taux) + "%"
            );
        }
    }

