package sn.agriculture.culture_service.exception;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

    @RestControllerAdvice
    public class GlobalException {


        @ExceptionHandler(CultureException.class)
        public ResponseEntity<Map<String, Object>> handleCultureException(
                CultureException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", e.getMessage(),
                            "success", false
                    ));
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<Map<String, Object>> handleRuntimeException(
                RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Une erreur interne est survenue",
                            "success", false
                    ));
        }
    }

