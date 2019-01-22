package org.taxidriver.api.models.responses;


import org.taxidriver.api.models.simple.Order;

public class NewOrderResponse {
    private Boolean status;
    private String error;
    private Integer order_id;
    private Order order_detail;

    public NewOrderResponse(Boolean status, String error, Integer order_id, Order order_detail) {
        this.status = status;
        this.error = error;
        this.order_id = order_id;
        this.order_detail = order_detail;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public Order getOrder_detail() {
        return order_detail;
    }

    @Override
    public String toString() {
        return "NewOrderResponse{" +
                "status=" + status +
                ", error='" + error + '\'' +
                ", order_id=" + order_id +
                ", order_detail=" + order_detail +
                '}';
    }
}
