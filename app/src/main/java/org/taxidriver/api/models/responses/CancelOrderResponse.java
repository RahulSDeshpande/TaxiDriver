package org.taxidriver.api.models.responses;

public class CancelOrderResponse {
    private Boolean status;
    private String error;

    public CancelOrderResponse(Boolean status, String error) {
        this.status = status;
        this.error = error;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
