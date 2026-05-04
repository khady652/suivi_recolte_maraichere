package sn.agriculture.geo_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.agriculture.geo_service.client.UserServiceClient;
import sn.agriculture.geo_service.dtos.requests.ServiceDepartementaleRequest;
import sn.agriculture.geo_service.dtos.response.MessageResponse;
import sn.agriculture.geo_service.dtos.response.ServiceDepartementaleResponse;
import sn.agriculture.geo_service.entity.Departement;
import sn.agriculture.geo_service.entity.ServiceDepartementale;
import sn.agriculture.geo_service.exception.GeoException;
import sn.agriculture.geo_service.repository.DepartementRepos;
import sn.agriculture.geo_service.repository.ServiceDepRepos;
import sn.agriculture.geo_service.entity.ServiceRegionale;
import sn.agriculture.geo_service.repository.ServiceRegRepos;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceDepartementaleService {

    private final ServiceDepRepos serviceDepartementaleRepository;
    private final DepartementRepos departementRepository;
    private final UserServiceClient usersServiceClient;
    private final ServiceRegRepos serviceRegionaleRepository;

    // ── CRÉER ─────────────────────────────────────────────
    @Transactional
    public MessageResponse creer(ServiceDepartementaleRequest request) {

        if (serviceDepartementaleRepository
                .existsByDepartementIdDepartement(request.getIdDepartement()))
            throw new GeoException(
                    "Ce département a déjà un service départemental");

        Departement departement = departementRepository
                .findById(request.getIdDepartement())
                .orElseThrow(() ->
                        new GeoException("Département introuvable"));

        ServiceDepartementale service = new ServiceDepartementale();
        service.setNomService(request.getNomService());
        service.setTelephoneService(request.getTelephoneService());
        service.setEmailService(request.getEmailService());
        service.setLocalite(request.getLocalite());
        service.setDepartement(departement);

        serviceDepartementaleRepository.save(service);
        log.info("Service départemental créé : {}",
                request.getNomService());

        return new MessageResponse(
                "Service départemental " + request.getNomService() +
                        " créé avec succès", true);
    }

    // ── LIRE TOUS ─────────────────────────────────────────
    public List<ServiceDepartementaleResponse> getAll() {
        return serviceDepartementaleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── LIRE UN ───────────────────────────────────────────
    public ServiceDepartementaleResponse getById(Integer id) {
        ServiceDepartementale service = serviceDepartementaleRepository
                .findById(id)
                .orElseThrow(() ->
                        new GeoException("Service départemental introuvable"));
        return toResponse(service);
    }

    // ── MODIFIER ──────────────────────────────────────────
    @Transactional
    public MessageResponse update(
            Integer id, ServiceDepartementaleRequest request) {

        ServiceDepartementale service = serviceDepartementaleRepository
                .findById(id)
                .orElseThrow(() ->
                        new GeoException("Service départemental introuvable"));

        if (request.getNomService() != null)
            service.setNomService(request.getNomService());
        if (request.getTelephoneService() != null)
            service.setTelephoneService(request.getTelephoneService());
        if (request.getEmailService() != null)
            service.setEmailService(request.getEmailService());
        if (request.getLocalite() != null)
            service.setLocalite(request.getLocalite());

        serviceDepartementaleRepository.save(service);
        return new MessageResponse(
                "Service départemental modifié avec succès", true);
    }

    // ── SUPPRIMER ─────────────────────────────────────────
    @Transactional
    public MessageResponse delete(Integer id) {
        ServiceDepartementale service = serviceDepartementaleRepository
                .findById(id)
                .orElseThrow(() ->
                        new GeoException("Service départemental introuvable"));
        serviceDepartementaleRepository.delete(service);
        return new MessageResponse(
                "Service départemental supprimé avec succès", true);
    }

    // ── AFFECTER DIRECTEUR SDDR ✅
    @Transactional
    public MessageResponse affecterDirecteurSDDR(
            Integer idService, Integer idDirecteurSddr) {

        ServiceDepartementale service = serviceDepartementaleRepository
                .findById(idService)
                .orElseThrow(() ->
                        new GeoException("Service départemental introuvable"));

        service.setIdDirecteurSddr(idDirecteurSddr);
        serviceDepartementaleRepository.save(service);

        log.info("Directeur SDDR {} affecté au service {}",
                idDirecteurSddr, idService);

        return new MessageResponse(
                "Directeur SDDR affecté avec succès", true);
    }

    // ── MÉTHODE UTILITAIRE ────────────────────────────────
    private ServiceDepartementaleResponse toResponse(
            ServiceDepartementale s) {
        ServiceDepartementaleResponse response =
                new ServiceDepartementaleResponse();
        response.setIdService(s.getIdService());
        response.setNomService(s.getNomService());
        response.setTelephoneService(s.getTelephoneService());
        response.setEmailService(s.getEmailService());
        response.setLocalite(s.getLocalite());
        response.setNomDepartement(
                s.getDepartement().getNomDepartement());

        // ✅ Récupérer nom et prénom du directeur SDDR
        if (s.getIdDirecteurSddr() != null) {
            String[] nomPrenom = usersServiceClient
                    .getNomPrenomDirecteurSDDR(s.getIdDirecteurSddr());
            response.setNomDirecteur(nomPrenom[0]);
            response.setPrenomDirecteur(nomPrenom[1]);
        }
        return response;
    }
    // ── LIRE PAR RÉGION ───────────────────────────────────
    public List<ServiceDepartementaleResponse> getByRegion(Integer idRegion) {
        return serviceDepartementaleRepository
                .findByDepartementRegionIdRegion(idRegion)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    public List<ServiceDepartementaleResponse> getByDirecteurDR(
            Integer userId) {

        // 1. Récupérer idServiceRegional du directeur DR
        Integer idServiceRegional = usersServiceClient
                .getIdServiceRegionalByDirecteurDR(userId);

        if (idServiceRegional == null)
            throw new GeoException("Directeur DR sans service régional !");

        // 2. Récupérer la région du service régional
        ServiceRegionale serviceRegional = serviceRegionaleRepository
                .findById(idServiceRegional)
                .orElseThrow(() ->
                        new GeoException("Service régional introuvable"));

        // 3. Retourner les services départementaux de cette région
        return serviceDepartementaleRepository
                .findByDepartementRegionIdRegion(
                        serviceRegional.getRegion().getIdRegion())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}