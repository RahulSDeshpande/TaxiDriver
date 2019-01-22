package org.taxidriver.api.models.responses;

public class RegistrationResponse {
    private Boolean status;
    private String error;
    private Integer driver_id;

    public RegistrationResponse(Boolean status, String error, Integer driver_id) {
        this.status = status;
        this.error = error;
        this.driver_id = driver_id;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public Integer getDriver_id() {
        return driver_id;
    }

    @Override
    public String toString() {
        return "RegistrationResponse{" +
                "status=" + status +
                ", error='" + error + '\'' +
                ", driver_id=" + driver_id +
                '}';
    }
}
