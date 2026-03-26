package com.usermanagement.dto;

public class ApiResponse<T> {

    private T data;
    private Meta meta;
    private ErrorResponse error;

    public ApiResponse() {
    }

    public ApiResponse(T data, Meta meta) {
        this.data = data;
        this.meta = meta;
    }

    public ApiResponse(ErrorResponse error) {
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> success(T data, Meta meta) {
        return new ApiResponse<>(data, meta);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(new ErrorResponse(code, message));
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }

    public static class Meta {
        private int page;
        private int size;
        private long total;

        public Meta() {
        }

        public Meta(int page, int size, long total) {
            this.page = page;
            this.size = size;
            this.total = total;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }
    }

    public static class ErrorResponse {
        private String code;
        private String message;

        public ErrorResponse() {
        }

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
