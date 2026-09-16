package restaurante.team3.Giacobello.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import restaurante.team3.Giacobello.categories.exceptions.CategoryAlreadyExistsException;
import restaurante.team3.Giacobello.categories.exceptions.CategoryHasProductsException;
import restaurante.team3.Giacobello.categories.exceptions.CategoryNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCategoryNotFound(CategoryNotFoundException ex) {

        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleCategoryAlreadyExists(CategoryAlreadyExistsException ex) {

        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(CategoryHasProductsException.class)
    public ResponseEntity<ApiErrorResponse> handleCategoryHasProducts(CategoryHasProductsException ex) {

        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

}
