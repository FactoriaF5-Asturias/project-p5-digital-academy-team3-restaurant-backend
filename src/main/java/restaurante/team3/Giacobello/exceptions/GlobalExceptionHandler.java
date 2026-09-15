package restaurante.team3.Giacobello.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import restaurante.team3.Giacobello.categories.exceptions.CategoryNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCategoryNotFound(CategoryNotFoundException ex) {

        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(), // 404, el número
                HttpStatus.NOT_FOUND.getReasonPhrase(), // "Not Found", el texto
                ex.getMessage(), // tu mensaje con el id
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

}
