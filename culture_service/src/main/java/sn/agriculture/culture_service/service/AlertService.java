package sn.agriculture.culture_service.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.agriculture.culture_service.client.UserServiceClient;
import sn.agriculture.culture_service.dtos.response.PrevisionResponse;
import sn.agriculture.culture_service.entity.Culture;
import sn.agriculture.culture_service.exception.CultureException;
import sn.agriculture.culture_service.repository.CultureRepos;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {

    private final CultureRepos cultureRepos;
    private final UserServiceClient userServiceClient;
    private final PrevisionService  previsionService;
    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
        log.info("✅ Twilio initialisé !");
    }

    // ── Envoyer SMS ───────────────────────────────────────
    public void envoyerSms(String telephone, String messageBody) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(telephone),
                    new PhoneNumber(twilioPhoneNumber),
                    messageBody
            ).create();
            log.info("✅ SMS envoyé à {} - SID: {}",
                    telephone, message.getSid());
        } catch (Exception e) {
            log.error("❌ Erreur envoi SMS à {} : {}",
                    telephone, e.getMessage());
        }
    }

    // ── Test manuel ───────────────────────────────────────
    public void testerAlerte(Long idCulture) {
        Culture culture = cultureRepos
                .findById(idCulture)
                .orElseThrow(() -> new CultureException(
                        "Culture introuvable !"));
        envoyerAlertes(culture, 30);
    }

    // ── Envoyer alertes agriculteur + chef ────────────────
    public void envoyerAlertes(Culture culture,
                               long joursRestants) {
        String message = String.format(
                "AgriSuivi - Alerte recolte ! " +
                        "Votre culture de %s (%s) " +
                        "sur la parcelle %s " +
                        "sera prete dans %d jours. " +
                        "Date prevue : %s",
                culture.getType(),
                culture.getVariete(),
                culture.getParcelle().getNomParcelle(),
                joursRestants,
                culture.getDatePremierRecoltePrevu()
        );

        // ✅ SMS à l'agriculteur
        try {
            Map<String, String> info = userServiceClient
                    .getAgriculteurInfo(
                            culture.getParcelle()
                                    .getIdAgriculteur().intValue());
            if (info != null && info.get("telephone") != null
                    && !info.get("telephone").isEmpty()) {
                envoyerSms("+221" + info.get("telephone"), message);
            }
        } catch (Exception e) {
            log.error(" Erreur SMS agriculteur : {}",
                    e.getMessage());
        }

        // ✅ SMS au chef coopératif
        try {
            Map<String, String> chefInfo = userServiceClient
                    .getChefCooperatifByAgriculteur(
                            culture.getParcelle()
                                    .getIdAgriculteur().intValue());
            if (chefInfo != null &&
                    chefInfo.get("telephone") != null &&
                    !chefInfo.get("telephone").isEmpty()) {
                envoyerSms("+221" + chefInfo.get("telephone"),
                        message);
            }
        } catch (Exception e) {
            log.error(" Erreur SMS chef : {}",
                    e.getMessage());
        }
    }
    @Scheduled(cron = "0 0 8 * * *")
    public void verifierAlertesRecolte() {
        log.info("🔔 Vérification alertes récolte...");

        LocalDate aujourd_hui = LocalDate.now();

        List<Culture> cultures = cultureRepos.findAll();

        for (Culture culture : cultures) {

            // Ignorer si pas de date prévue
            if (culture.getDatePremierRecoltePrevu() == null)
                continue;

            // Ignorer si déjà récoltée
            if (!culture.getRecoltes().isEmpty())
                continue;

            long joursRestants = ChronoUnit.DAYS.between(
                    aujourd_hui,
                    culture.getDatePremierRecoltePrevu());

            //  Alerte si récolte dans 30 jours
            if (joursRestants >= 0 && joursRestants <= 30) {
                log.info("🌾 Alerte pour culture {} - {} jours restants",
                        culture.getIdCulture(), joursRestants);
                envoyerAlertes(culture, joursRestants);
            }
        }

        log.info(" Vérification terminée !");
    }

    // ── ALERTE DÉCIDEURS ARM ──────────────────────────────
    public void envoyerAlerteDecideurs(String message) {
        try {
            List<Map<String, String>> decideurs =
                    userServiceClient.getAllDecideurs();

            if (decideurs == null || decideurs.isEmpty()) {
                log.warn("Aucun décideur trouvé !");
                return;
            }

            decideurs.forEach(d -> {
                String telephone = d.get("telephone");
                if (telephone != null
                        && !telephone.isEmpty()) {
                    envoyerSms("+221" + telephone, message);
                    log.info("Alerte envoyée au décideur : {}",
                            telephone);
                }
            });
        } catch (Exception e) {
            log.error("Erreur envoi alerte décideurs : {}",
                    e.getMessage());
        }
    }

    // ── VÉRIFICATION MENSUELLE AUTOMATIQUE ───────────────
    @Scheduled(cron = "0 */2 * * * *") // toutes les 2 min pour tester
    public void verifierPrevisionsMensuelles() {
        log.info("Vérification prévisions mensuelles...");

        PrevisionResponse prevision = previsionService
                .calculerPrevision("oignon");

        envoyerAlerteDecideurs(prevision.getMessage());

        log.info("Vérification terminée !");
    }
}