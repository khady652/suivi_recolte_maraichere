package sn.agriculture.culture_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import sn.agriculture.culture_service.util.DepartementResponse;
import sn.agriculture.culture_service.util.ServiceRegionalResponse;
import sn.agriculture.culture_service.util.ServiceRegionalResponse;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GeoServiceClient {

    private final RestClient restClient;

    public GeoServiceClient(
            @Value("${geo.service.base-url}") String geoServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(geoServiceUrl)
                .build();
    }

    public DepartementResponse getDepartementById(Integer id) {
        try {
            return restClient.get()
                    .uri("/api/geo/departements/{id}", id)
                    .retrieve()
                    .body(DepartementResponse.class);
        } catch (Exception e) {
            log.error("Erreur récupération département {} : {}",
                    id, e.getMessage());
            return null;
        }
    }

    public List<DepartementResponse> getDepartementsByRegion(Integer idRegion) {
        try {
            return restClient.get()
                    .uri("/api/geo/departements/region/{idRegion}", idRegion)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.error("Erreur récupération départements région {} : {}",
                    idRegion, e.getMessage());
            return List.of();
        }
    }

    public List<Long> getIdDepartementsByRegion(Integer idRegion) {
        return getDepartementsByRegion(idRegion)
                .stream()
                .map(d -> d.getIdDepartement().longValue())
                .toList();
    }

    public DepartementResponse getDepartementByServiceId(Integer idService) {
        try {
            Map<String, Object> service = restClient.get()
                    .uri("/api/geo/services-departementaux/{id}", idService)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (service == null) return null;

            Integer idDepartement = (Integer) service.get("idDepartement");
            return getDepartementById(idDepartement);

        } catch (Exception e) {
            log.error("Erreur récupération département par service {} : {}",
                    idService, e.getMessage());
            return null;
        }
    }

    // ✅ Ajout
    public Integer getIdRegionByServiceId(Integer idServiceRegional) {
        try {
            ServiceRegionalResponse service = restClient.get()
                    .uri("/api/geo/services-regionaux/{id}",
                            idServiceRegional)
                    .retrieve()
                    .body(ServiceRegionalResponse.class);

            if (service == null) return null;
            return service.getIdRegion();

        } catch (Exception e) {
            log.error("Erreur récupération région par service {} : {}",
                    idServiceRegional, e.getMessage());
            return null;
        }
    }
}