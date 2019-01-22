package org.taxidriver.api.models.requests;

public class CancelOrderRequest {
    private Integer order_id;

    public CancelOrderRequest(Integer order_id) {
        this.order_id = order_id;
    }
}
