package sn.agriculteur.marche_service.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sn.agriculteur.marche_service.Client.UserServiceClient;
import sn.agriculteur.marche_service.dto.request.CollecteRequest;
import sn.agriculteur.marche_service.dto.response.CollecteResponse;
import sn.agriculteur.marche_service.entity.CollecteDonnees;
import sn.agriculteur.marche_service.entity.Marche;
import sn.agriculteur.marche_service.exception.MarcheException;
import sn.agriculteur.marche_service.repository.CollecteRepos;
import sn.agriculteur.marche_service.repository.MarcheRepos;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class CollecteService {

        private final CollecteRepos collecteRepository;
        private final MarcheRepos marcheRepository;
        private final UserServiceClient userServiceClient;

        // ── ENREGISTRER ───────────────────────────────────────
        @Transactional
        public CollecteResponse enregistrer(CollecteRequest request,
                                            Integer idEnqueteur) {

            Marche marche = marcheRepository
                    .findById(request.getIdMarche())
                    .orElseThrow(() -> new MarcheException(
                            "Marché introuvable !"));

            // ── Vérifier zone affectation ─────────────────────
            String zoneEnqueteur = userServiceClient
                    .getZoneAffectation(idEnqueteur);

            if (zoneEnqueteur == null)
                throw new MarcheException(
                        "Impossible de vérifier la zone de l'enquêteur !");

            if (!zoneEnqueteur.equalsIgnoreCase(marche.getLieu()))
                throw new MarcheException(
                        "Vous n'êtes pas affecté à la zone de ce marché !");

            // ── Récupérer ancien prix pour alerte ─────────────
            Double ancienPrix = collecteRepository
                    .findByProduitAndMarcheIdMarche(
                            request.getProduit(),
                            request.getIdMarche())
                    .stream()
                    .max(Comparator.comparing(
                            CollecteDonnees::getDateCollecte))
                    .map(CollecteDonnees::getPrixUnitaire)
                    .orElse(null);

            // ── Créer la collecte ─────────────────────────────
            CollecteDonnees collecte = CollecteDonnees.builder()
                    .dateCollecte(request.getDateCollecte())
                    .produit(request.getProduit())
                    .prixUnitaire(request.getPrixUnitaire())
                    .quantiteDisponible(request.getQuantiteDisponible())
                    .idEnqueteur(idEnqueteur)
                    .marche(marche)
                    .build();

            collecteRepository.save(collecte);
            log.info("Collecte enregistrée par enquêteur {} sur marché {}",
                    idEnqueteur, marche.getNomMarche());

            return toResponse(collecte);
        }

        // ── MODIFIER ──────────────────────────────────────────
        @Transactional
        public CollecteResponse modifier(Integer id,
                                         CollecteRequest request) {
            CollecteDonnees collecte = collecteRepository
                    .findById(id)
                    .orElseThrow(() -> new MarcheException(
                            "Collecte introuvable !"));

            if (request.getPrixUnitaire() != null)
                collecte.setPrixUnitaire(request.getPrixUnitaire());
            if (request.getQuantiteDisponible() != null)
                collecte.setQuantiteDisponible(
                        request.getQuantiteDisponible());
            if (request.getProduit() != null)
                collecte.setProduit(request.getProduit());
            if (request.getDateCollecte() != null)
                collecte.setDateCollecte(request.getDateCollecte());

            collecteRepository.save(collecte);
            return toResponse(collecte);
        }

        // ── SUPPRIMER ─────────────────────────────────────────
        @Transactional
        public void supprimer(Integer id) {
            collecteRepository.findById(id)
                    .orElseThrow(() -> new MarcheException(
                            "Collecte introuvable !"));
            collecteRepository.deleteById(id);
        }

        // ── MES COLLECTES (Enquêteur) ─────────────────────────
        public List<CollecteResponse> getMesCollectes(
                Integer idEnqueteur) {
            return collecteRepository
                    .findByIdEnqueteur(idEnqueteur)
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── PAR MARCHÉ ────────────────────────────────────────
        public List<CollecteResponse> getByMarche(Integer idMarche) {
            return collecteRepository
                    .findByMarcheIdMarche(idMarche)
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── PAR PRODUIT ───────────────────────────────────────
        public List<CollecteResponse> getByProduit(String produit) {
            return collecteRepository
                    .findByProduit(produit)
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── TOUTES ────────────────────────────────────────────
        public List<CollecteResponse> getAll() {
            return collecteRepository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── PRIX MOYEN PAR PRODUIT ────────────────────────────
        public Map<String, Double> getPrixMoyenParProduit() {
            return collecteRepository.prixMoyenParProduit()
                    .stream()
                    .collect(Collectors.toMap(
                            o -> (String) o[0],
                            o -> (Double) o[1]));
        }

        // ── STOCK DU JOUR ─────────────────────────────────────
        public Map<String, Double> getStockDuJour() {
            return collecteRepository
                    .stockParProduitEtDate(LocalDate.now())
                    .stream()
                    .collect(Collectors.toMap(
                            o -> (String) o[0],
                            o -> (Double) o[1]));
        }

        // ── DERNIERS PRIX PAR PRODUIT ─────────────────────────
        public List<CollecteResponse> getDerniersPrix() {
            return collecteRepository.derniersPrixParProduitEtMarche()
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        // ── MAPPER ────────────────────────────────────────────
        private CollecteResponse toResponse(CollecteDonnees c) {

            String nomEnqueteur    = null;
            String prenomEnqueteur = null;
            String organisation    = null;
            String zoneAffectation = null;

            try {
                Map<String, String> info = userServiceClient
                        .getEnqueteurInfo(c.getIdEnqueteur());
                if (info != null) {
                    nomEnqueteur    = info.get("nom");
                    prenomEnqueteur = info.get("prenom");
                    organisation    = info.get("organisation");
                    zoneAffectation = info.get("zoneAffectation"); // ✅
                }
            } catch (Exception e) {
                log.warn("Enquêteur introuvable : {}",
                        c.getIdEnqueteur());
            }

            return CollecteResponse.builder()
                    .idCollecte(c.getIdCollecte())
                    .dateCollecte(c.getDateCollecte())
                    .produit(c.getProduit())
                    .prixUnitaire(c.getPrixUnitaire())
                    .quantiteDisponible(c.getQuantiteDisponible())
                    .idMarche(c.getMarche().getIdMarche())
                    .nomMarche(c.getMarche().getNomMarche())
                    .lieuMarche(c.getMarche().getLieu())
                    .idEnqueteur(c.getIdEnqueteur())
                    .nomEnqueteur(nomEnqueteur)
                    .prenomEnqueteur(prenomEnqueteur)
                    .organisation(organisation)
                    .zoneAffectation(zoneAffectation) // ✅
                    .build();
        }
    }