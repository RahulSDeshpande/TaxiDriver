package org.taxidriver.api.models.requests;

public class NewOrderRequest {
    private Integer client_id;
    private String address_from;
    private String address_to;
    private Boolean is_child_seat_needed;
    private Boolean is_conditioner_needed;
    private Short payment_type;
    private Double distance;
    private Double price;
    private String planed_date;
    private String planed_time;

    public NewOrderRequest(Integer client_id, String address_from, String address_to, Boolean is_child_seat_needed, Boolean is_conditioner_needed, Short payment_type, Double distance, Double price, String planed_date, String planed_time) {
        this.client_id = client_id;
        this.address_from = address_from;
        this.address_to = address_to;
        this.is_child_seat_needed = is_child_seat_needed;
        this.is_conditioner_needed = is_conditioner_needed;
        this.payment_type = payment_type;
        this.distance = distance;
        this.price = price;
        this.planed_date = planed_date;
        this.planed_time = planed_time;
    }
}
