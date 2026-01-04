package com.sep.psp.controller;

import com.sep.psp.service.UnauthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> unauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(401).body(Map.of(
                "error", "UNAUTHORIZED",
                "message", ex.getMessage()
        ));
    }
}
