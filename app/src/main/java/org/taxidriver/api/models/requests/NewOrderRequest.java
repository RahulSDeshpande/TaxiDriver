package org.taxidriver.api.models.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewOrderRequest {



    @SerializedName("driver_id")
    @Expose
    private Integer driverId;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;

    public NewOrderRequest(Integer driverId, Double lat, Double lng){
        this.driverId = driverId;
        this.lat = lat;
        this.lng = lng;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

}