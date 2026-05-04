package sn.agriculture.geo_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.agriculture.geo_service.dtos.requests.ServiceRegionaleRequest;
import sn.agriculture.geo_service.dtos.response.MessageResponse;
import sn.agriculture.geo_service.dtos.response.ServiceRegionaleResponse;
import sn.agriculture.geo_service.entity.Region;
import sn.agriculture.geo_service.entity.ServiceRegionale;
import sn.agriculture.geo_service.exception.GeoException;
import sn.agriculture.geo_service.repository.RegionRepos;
import sn.agriculture.geo_service.repository.ServiceRegRepos;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceRegionaleService {

    private final ServiceRegRepos serviceRegionaleRepository;
    private final RegionRepos regionRepository;

    // ── CRÉER
    @Transactional
    public MessageResponse creer(ServiceRegionaleRequest request) {

        if (serviceRegionaleRepository
                .existsByRegionIdRegion(request.getIdRegion()))
            throw new GeoException(
                    "Cette région a déjà un service régional");

        Region region = regionRepository
                .findById(request.getIdRegion())
                .orElseThrow(() ->
                        new GeoException("Région introuvable"));

        ServiceRegionale service = new ServiceRegionale();
        service.setNomService(request.getNomService());
        service.setTelephoneService(request.getTelephoneService());
        service.setEmailService(request.getEmailService());
        service.setLocalite(request.getLocalite());
        service.setRegion(region);

        serviceRegionaleRepository.save(service);
        log.info("Service régional créé : {}", request.getNomService());

        return new MessageResponse(
                "Service régional " + request.getNomService() +
                        " créé avec succès", true);
    }

    // ── LIRE TOUS
    public List<ServiceRegionaleResponse> getAll() {
        return serviceRegionaleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── LIRE UN
    public ServiceRegionaleResponse getById(Integer id) {
        ServiceRegionale service = serviceRegionaleRepository
                .findById(id)
                .orElseThrow(() ->
                        new GeoException("Service régional introuvable"));
        return toResponse(service);
    }

    // ── MODIFIER
    @Transactional
    public MessageResponse update(
            Integer id, ServiceRegionaleRequest request) {

        ServiceRegionale service = serviceRegionaleRepository
                .findById(id)
                .orElseThrow(() ->
                        new GeoException("Service régional introuvable"));

        if (request.getNomService() != null)
            service.setNomService(request.getNomService());
        if (request.getTelephoneService() != null)
            service.setTelephoneService(request.getTelephoneService());
        if (request.getEmailService() != null)
            service.setEmailService(request.getEmailService());
        if (request.getLocalite() != null)
            service.setLocalite(request.getLocalite());

        serviceRegionaleRepository.save(service);
        return new MessageResponse(
                "Service régional modifié avec succès", true);
    }

    // ── SUPPRIMER
    @Transactional
    public MessageResponse delete(Integer id) {
        ServiceRegionale service = serviceRegionaleRepository
                .findById(id)
                .orElseThrow(() ->
                        new GeoException("Service régional introuvable"));
        serviceRegionaleRepository.delete(service);
        return new MessageResponse(
                "Service régional supprimé avec succès", true);
    }

    // ── AFFECTER DIRECTEUR DR ✅
    @Transactional
    public MessageResponse affecterDirecteurDR(
            Integer idService, Integer idDirecteurDr) {

        ServiceRegionale service = serviceRegionaleRepository
                .findById(idService)
                .orElseThrow(() ->
                        new GeoException("Service régional introuvable"));

        service.setIdDirecteurDrdr(idDirecteurDr);
        serviceRegionaleRepository.save(service);

        log.info("Directeur DR {} affecté au service {}",
                idDirecteurDr, idService);

        return new MessageResponse(
                "Directeur DR affecté avec succès", true);
    }

    // ── MÉTHODE UTILITAIRE
    private ServiceRegionaleResponse toResponse(ServiceRegionale s) {
        ServiceRegionaleResponse response = new ServiceRegionaleResponse();
        response.setIdService(s.getIdService());
        response.setNomService(s.getNomService());
        response.setTelephoneService(s.getTelephoneService());
        response.setEmailService(s.getEmailService());
        response.setLocalite(s.getLocalite());
        response.setNomRegion(s.getRegion().getNomRegion());
        if (s.getIdDirecteurDrdr() != null) {
            // ✅ nomDirecteur sera rempli via RestClient plus tard
        }
        return response;
    }
}