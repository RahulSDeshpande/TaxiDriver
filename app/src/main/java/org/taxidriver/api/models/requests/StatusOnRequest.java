package org.taxidriver.api.models.requests;

public class StatusOnRequest {

    public Integer driver_id;
    public Integer driver_status;

    public StatusOnRequest(Integer driver_id, Integer driver_status) {
        this.driver_id = driver_id;
        this.driver_status = driver_status;
    }
}
