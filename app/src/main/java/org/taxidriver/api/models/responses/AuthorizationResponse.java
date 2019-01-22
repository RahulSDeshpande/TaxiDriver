package org.taxidriver.api.models.responses;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.taxidriver.api.models.simple.DriverDetail;

public class AuthorizationResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("driver_id")
    @Expose
    private Integer driverId;
    @SerializedName("driver_detail")
    @Expose
    private DriverDetail driverDetail;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public DriverDetail getDriverDetail() {
        return driverDetail;
    }

    public void setDriverDetail(DriverDetail driverDetail) {
        this.driverDetail = driverDetail;
    }

}
