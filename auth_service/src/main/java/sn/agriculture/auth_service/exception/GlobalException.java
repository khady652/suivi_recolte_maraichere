package sn.agriculture.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sn.agriculture.auth_service.dto.AuthDto.MessageResponse;

import java.util.HashMap;
import java.util.Map;

    @RestControllerAdvice
    public class GlobalException {

        // Erreur d'authentification → 401
        @ExceptionHandler(AuthException.class)
        public ResponseEntity<MessageResponse> handleAuthException(AuthException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(e.getMessage(), false,null));
        }

        // Erreur de validation → 400
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidation(
                MethodArgumentNotValidException e) {
            Map<String, String> errors = new HashMap<>();
            e.getBindingResult().getAllErrors().forEach(error -> {
                String field   = ((FieldError) error).getField();
                String message = error.getDefaultMessage();
                errors.put(field, message);
            });
            return ResponseEntity.badRequest().body(errors);
        }

        // Toute autre erreur → 500
        @ExceptionHandler(Exception.class)
        public ResponseEntity<MessageResponse> handleGeneral(Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Une erreur interne est survenue", false,null));
        }
    }

