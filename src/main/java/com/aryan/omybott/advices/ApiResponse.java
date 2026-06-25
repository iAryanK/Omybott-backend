package com.aryan.omybott.advices;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;

@Data
public class ApiResponse<T> {

    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy", timezone = "IST")
    private Instant timestamp;
    private T data;
    private ApiError error;

    public ApiResponse() {
        this.timestamp = Instant.now();
    }

    public ApiResponse(T data) {
        this();
        this.data = data;
    }

    public ApiResponse(ApiError error) {
        this();
        this.error = error;
    }

}
