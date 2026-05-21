package com.example.frontend;

import com.google.gson.annotations.SerializedName;

public class StatusResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private String error;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
}