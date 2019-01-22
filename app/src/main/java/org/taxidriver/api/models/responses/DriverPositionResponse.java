package org.taxidriver.api.models.responses;

public class DriverPositionResponse {
    private Boolean status;
    private String error;
    private Double driver_lat;
    private Double driver_lng;

    public DriverPositionResponse(Boolean status, String error, Double driver_lat, Double driver_lng) {
        this.status = status;
        this.error = error;
        this.driver_lat = driver_lat;
        this.driver_lng = driver_lng;
    }

    public String getError() {

        return error;
    }

    public Double getDriver_lat() {
        return driver_lat;
    }

    public Double getDriver_lng() {
        return driver_lng;
    }

    public Boolean getStatus() {

        return status;
    }

    @Override
    public String toString() {
        return "DriverPositionResponse{" +
                "status=" + status +
                ", error='" + error + '\'' +
                ", driver_lat=" + driver_lat +
                ", driver_lng=" + driver_lng +
                '}';
    }
}
