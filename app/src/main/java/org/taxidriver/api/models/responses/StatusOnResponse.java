package org.taxidriver.api.models.responses;

public class StatusOnResponse {
    private Boolean status;
    private String error;

    public StatusOnResponse(Boolean status, String error){

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
