package team3.dev.restaurante.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        String error,
        PageMeta meta) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static <T> ApiResponse<T> ok(T data, PageMeta meta) {
        return new ApiResponse<>(true, data, null, meta);
    }

    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(false, null, message, null);
    }

    public record PageMeta(int page, int size, long totalElements, int totalPages) {
    }
}
