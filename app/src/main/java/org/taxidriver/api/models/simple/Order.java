package org.taxidriver.api.models.simple;

public class Order {
    private DriverDetail driverDetail;
    private Driver driver;
    private String address_from;
    private String address_to;
    private Double distance;
    private Boolean is_child_seat_needed;
    private Boolean is_conditioner_needed;
    private String order_status;
    private Short payment_type;
    private Double price;
    private String planed_date;
    private String planed_time;

    public Order(DriverDetail driverDetail, Driver driver, String address_from, String address_to, Double distance, Boolean is_child_seat_needed, Boolean is_conditioner_needed, String order_status, Short payment_type, Double price, String planed_date, String planed_time) {
        this.driverDetail = driverDetail;
        this.driver = driver;
        this.address_from = address_from;
        this.address_to = address_to;
        this.distance = distance;
        this.is_child_seat_needed = is_child_seat_needed;
        this.is_conditioner_needed = is_conditioner_needed;
        this.order_status = order_status;
        this.payment_type = payment_type;
        this.price = price;
        this.planed_date = planed_date;
        this.planed_time = planed_time;
    }

    public DriverDetail getDriverDetail() {
        return driverDetail;
    }

    public Driver getDriver() {
        return driver;
    }

    public String getAddress_from() {
        return address_from;
    }

    public String getAddress_to() {
        return address_to;
    }

    public Double getDistance() {
        return distance;
    }

    public Boolean getIs_child_seat_needed() {
        return is_child_seat_needed;
    }

    public Boolean getIs_conditioner_needed() {
        return is_conditioner_needed;
    }

    public String getOrder_status() {
        return order_status;
    }

    public Short getPayment_type() {
        return payment_type;
    }

    public Double getPrice() {
        return price;
    }

    public String getPlaned_date() {
        return planed_date;
    }

    public String getPlaned_time() {
        return planed_time;
    }

    @Override
    public String toString() {
        return "Order{" +
                "driverDetail=" + driverDetail +
                ", driver=" + driver +
                ", address_from='" + address_from + '\'' +
                ", address_to='" + address_to + '\'' +
                ", distance=" + distance +
                ", is_child_seat_needed=" + is_child_seat_needed +
                ", is_conditioner_needed=" + is_conditioner_needed +
                ", order_status='" + order_status + '\'' +
                ", payment_type=" + payment_type +
                ", price=" + price +
                ", planed_date='" + planed_date + '\'' +
                ", planed_time='" + planed_time + '\'' +
                '}';
    }
}
