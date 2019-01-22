package org.taxidriver.api.models.responses;

public class LikeToDriverResponse {
    private Boolean status;
    private String error;

    public LikeToDriverResponse(Boolean status, String error) {
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
        return "LikeToDriverResponse{" +
                "status=" + status +
                ", error='" + error + '\'' +
                '}';
    }
}
