package sn.agriculture.culture_service.controller;




import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.agriculture.culture_service.dtos.response.PrevisionResponse;
import sn.agriculture.culture_service.service.AlertService;
import sn.agriculture.culture_service.service.PrevisionService;

@RestController
    @RequestMapping("/api/culture/previsions")
    @RequiredArgsConstructor
    @Slf4j

    public class PrevisionController {

        private final PrevisionService previsionService;
        private final AlertService alertService;

        @GetMapping("/{produit}")
        public ResponseEntity<PrevisionResponse> calculerPrevision(
                @PathVariable String produit) {

            PrevisionResponse prevision = previsionService
                    .calculerPrevision(produit);

            // Envoyer SMS aux décideurs si message important
            if (prevision.getMessage().contains("DEFICIT")
                    || prevision.getMessage()
                    .contains("EXCEDENT")) {
                alertService.envoyerAlerteDecideurs(
                        prevision.getMessage());
            }

            return ResponseEntity.ok(prevision);
        }
//    @GetMapping("/test-sms")
//    public ResponseEntity<String> testSms() {
//        alertService.envoyerAlerteDecideurs(
//                "Test alerte : Production prevue oignon " +
//                        "45000 tonnes sur Juin - Juillet 2026.");
//        return ResponseEntity.ok("SMS envoyé !");
//    }
@GetMapping("/ma-cooperative/{produit}")
public ResponseEntity<PrevisionResponse> calculerPrevisionCooperative(
        @PathVariable String produit,
        Authentication authentication) {
    Long userId = ((Integer) authentication.getPrincipal()).longValue();
    return ResponseEntity.ok(
            previsionService.calculerPrevisionCooperative(userId));
}

    }

