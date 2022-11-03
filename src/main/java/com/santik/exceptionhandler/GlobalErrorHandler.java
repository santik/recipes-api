package com.santik.exceptionhandler;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {

  @ExceptionHandler(WebExchangeBindException.class)
  public ResponseEntity<String> handleRequestBodyHandler(WebExchangeBindException ex) {
    log.error("Exception {}", ex.getMessage(), ex);
    String collect = ex.getBindingResult().getAllErrors().stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .sorted()
        .collect(Collectors.joining(","));

    log.error("Error {}", collect);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(collect);
  }

}
