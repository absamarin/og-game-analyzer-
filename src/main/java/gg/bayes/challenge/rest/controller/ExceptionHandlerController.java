package gg.bayes.challenge.rest.controller;

import gg.bayes.challenge.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;


@Slf4j

@RestControllerAdvice
class GlobalControllerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<HttpErrorInfo> handleNotFoundException(InvalidInputException ex) {


        return new ResponseEntity<>(createHttpErrorInfo(UNPROCESSABLE_ENTITY, ex), UNPROCESSABLE_ENTITY);
    }

    private HttpErrorInfo createHttpErrorInfo(HttpStatus httpStatus, Exception ex) {
        final String message = ex.getMessage();

        LOG.debug("Returning HTTP status: {} , message: {}", httpStatus, message);
        return new HttpErrorInfo(httpStatus, message);
    }
}

