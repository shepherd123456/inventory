package cz.technico.inventory.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

public class ResponseExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<?> handleRegisterEmailDuplication(DataIntegrityViolationException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler
    public ResponseEntity<?> handleMissingRequestPart(MissingServletRequestPartException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
