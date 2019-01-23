package org.taxidriver.api.models.simple;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order_ {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("client")
    @Expose
    private Client client;
    @SerializedName("driver")
    @Expose
    private Object driver;
    @SerializedName("address_from")
    @Expose
    private String addressFrom;
    @SerializedName("address_to")
    @Expose
    private String addressTo;
    @SerializedName("distance")
    @Expose
    private Integer distance;
    @SerializedName("is_child_seat_needed")
    @Expose
    private Boolean isChildSeatNeeded;
    @SerializedName("is_conditioner_needed")
    @Expose
    private Boolean isConditionerNeeded;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("payment_type")
    @Expose
    private Integer paymentType;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("planed_date")
    @Expose
    private String planedDate;
    @SerializedName("planed_time")
    @Expose
    private String planedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Object getDriver() {
        return driver;
    }

    public void setDriver(Object driver) {
        this.driver = driver;
    }

    public String getAddressFrom() {
        return addressFrom;
    }

    public void setAddressFrom(String addressFrom) {
        this.addressFrom = addressFrom;
    }

    public String getAddressTo() {
        return addressTo;
    }

    public void setAddressTo(String addressTo) {
        this.addressTo = addressTo;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Boolean getIsChildSeatNeeded() {
        return isChildSeatNeeded;
    }

    public void setIsChildSeatNeeded(Boolean isChildSeatNeeded) {
        this.isChildSeatNeeded = isChildSeatNeeded;
    }

    public Boolean getIsConditionerNeeded() {
        return isConditionerNeeded;
    }

    public void setIsConditionerNeeded(Boolean isConditionerNeeded) {
        this.isConditionerNeeded = isConditionerNeeded;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getPlanedDate() {
        return planedDate;
    }

    public void setPlanedDate(String planedDate) {
        this.planedDate = planedDate;
    }

    public String getPlanedTime() {
        return planedTime;
    }

    public void setPlanedTime(String planedTime) {
        this.planedTime = planedTime;
    }

}