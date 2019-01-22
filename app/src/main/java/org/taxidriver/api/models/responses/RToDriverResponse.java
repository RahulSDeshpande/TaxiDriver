package org.taxidriver.api.models.responses;

//todo: Class that represent response from server on response about driver request;
public class RToDriverResponse {
    private Boolean status;
    private String error;

    public RToDriverResponse(Boolean status, String error) {
        this.status = status;
        this.error = error;
    }

    public Boolean getStatus() {

        return status;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "RToDriverResponse{" +
                "status=" + status +
                ", error='" + error + '\'' +
                '}';
    }
}
