package sn.agriculteur.public_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.agriculteur.public_service.Client.CultureServiceClient;
import sn.agriculteur.public_service.Client.GeoServiceClient;
import sn.agriculteur.public_service.Client.MarcheServiceClient;
import sn.agriculteur.public_service.Response.CollecteResponse;
import sn.agriculteur.public_service.Response.RegionResponse;
import sn.agriculteur.public_service.Response.ZoneProduction;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicService {

    private final GeoServiceClient geoServiceClient;
    private final CultureServiceClient cultureServiceClient;
    private final MarcheServiceClient marcheServiceClient;

    // ── CARTE SIG — ZONES DE PRODUCTION ──────────────────
    public ZoneProduction getZonesProduction() {

        // 1. Récupérer toutes les régions avec coords
        List<RegionResponse> regions = geoServiceClient
                .getAllRegions();

        // 2. Production par région
        Map<String, Double> productionParRegion =
                cultureServiceClient.getProductionParRegion();

        // 3. Construire GeoJSON
        List<ZoneProduction.Feature> features = regions
                .stream()
                .filter(r -> r.getLatitude() != null
                        && r.getLongitude() != null)
                .map(r -> ZoneProduction.Feature.builder()
                        .type("Feature")
                        .geometry(
                                ZoneProduction.Geometry
                                        .builder()
                                        .type("Point")
                                        .coordinates(new double[]{
                                                r.getLongitude(),
                                                r.getLatitude()
                                        })
                                        .build())
                        .properties(
                                ZoneProduction.Properties
                                        .builder()
                                        .idRegion(r.getIdRegion())
                                        .nomRegion(r.getNomRegion())
                                        .production(
                                                productionParRegion
                                                        .getOrDefault(
                                                                r.getNomRegion(),
                                                                0.0))
                                        .surfaceCultivee(
                                                cultureServiceClient
                                                        .getSurfaceCultiveeRegion(
                                                                r.getIdRegion()))
                                        .superficie(r.getSuperficie())
                                        .population(r.getPopulation())
                                        .build())
                        .build())
                .collect(Collectors.toList());

        return ZoneProduction.builder()
                .type("FeatureCollection")
                .features(features)
                .build();
    }

    // ── PRIX MARCHÉS ──────────────────────────────────────
    public List<CollecteResponse> getPrixMarches() {
        return marcheServiceClient.getDerniersPrix();
    }

    // ── STOCK DU JOUR ─────────────────────────────────────
    public Map<String, Double> getStockDuJour() {
        return marcheServiceClient.getStockDuJour();
    }
}