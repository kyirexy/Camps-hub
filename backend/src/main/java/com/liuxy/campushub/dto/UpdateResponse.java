package com.liuxy.campushub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResponse {
    private boolean success;
    private String message;

    public static UpdateResponse success(String message) {
        return new UpdateResponse(true, message);
    }

    public static UpdateResponse failure(String message) {
        return new UpdateResponse(false, message);
    }
} 