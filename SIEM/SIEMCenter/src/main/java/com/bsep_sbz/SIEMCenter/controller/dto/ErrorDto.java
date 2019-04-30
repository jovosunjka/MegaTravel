package com.bsep_sbz.SIEMCenter.controller.dto;

public class ErrorDto
{
    private String message;

    public ErrorDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
