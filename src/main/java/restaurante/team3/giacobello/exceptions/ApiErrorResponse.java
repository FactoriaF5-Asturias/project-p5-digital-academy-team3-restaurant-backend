package restaurante.team3.giacobello.exceptions;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp) {
}
