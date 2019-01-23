package org.taxidriver.api.models.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.taxidriver.api.models.simple.Order;

import java.util.List;

public class RequestOrdersResponse {

    @SerializedName("orders")
    @Expose
    private List<Order> orders = null;
    @SerializedName("status")
    @Expose
    private Boolean status;

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}