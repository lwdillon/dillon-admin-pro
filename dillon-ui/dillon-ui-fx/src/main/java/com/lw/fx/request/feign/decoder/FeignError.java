package com.lw.fx.request.feign.decoder;

public class FeignError extends RuntimeException {
    private String message; // parsed from json

    @Override
    public String getMessage() {
        return message;
    }
}