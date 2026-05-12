package sn.agriculteur.marche_service.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

    @RestControllerAdvice
    public class GlobalException {


        @ExceptionHandler(MarcheException.class)
        public ResponseEntity<Map<String, Object>> handleMarcheException(
                MarcheException e) {

            // Détecter le bon status selon le message
            HttpStatus status = e.getMessage().contains("introuvable")
                    ? HttpStatus.NOT_FOUND          // 404
                    : e.getMessage().contains("existe déjà")
                    ? HttpStatus.CONFLICT           // 409
                    : HttpStatus.BAD_REQUEST;       // 400

            return ResponseEntity
                    .status(status)
                    .body(Map.of(
                            "message", e.getMessage(),
                            "success", false
                    ));
        }

        // 2. Erreurs de validation (@Valid)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidation(
                MethodArgumentNotValidException e) {

            List<String> erreurs = e.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(f -> f.getField() + " : " + f.getDefaultMessage())
                    .toList();

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "Données invalides",
                            "erreurs", erreurs,
                            "success", false
                    ));
        }

        // 3. Le plus général en dernier
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, Object>> handleException(
                Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Une erreur interne est survenue",
                            "success", false
                    ));
        }
    }
