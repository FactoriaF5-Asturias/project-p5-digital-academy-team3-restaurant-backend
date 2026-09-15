package restaurante.team3.Giacobello.exceptions;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp) {
}
