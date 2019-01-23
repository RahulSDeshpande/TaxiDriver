package org.taxidriver.api.models.simple;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("order")
    @Expose
    private Order_ order;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Order_ getOrder() {
        return order;
    }

    public void setOrder(Order_ order) {
        this.order = order;
    }
}