package sn.agriculteur.marche_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlerteScheduler {

    private final AlerteService alerteService;

    @Scheduled(cron = "0 0 8 1 * *")
    public void verifierAlertesMensuel() {
        log.info("Génération alertes mensuelle...");
        alerteService.genererAlertes("oignon");
        log.info("Alertes générées !");
    }
}
