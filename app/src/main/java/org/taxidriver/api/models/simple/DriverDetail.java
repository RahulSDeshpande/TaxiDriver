package org.taxidriver.api.models.simple;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverDetail {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("vehicle_registration_plate")
    @Expose
    private String vehicleRegistrationPlate;
    @SerializedName("money_balance")
    @Expose
    private Integer moneyBalance;
    @SerializedName("driver_lat")
    @Expose
    private Double driverLat;
    @SerializedName("driver_lng")
    @Expose
    private Double driverLng;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVehicleRegistrationPlate() {
        return vehicleRegistrationPlate;
    }

    public void setVehicleRegistrationPlate(String vehicleRegistrationPlate) {
        this.vehicleRegistrationPlate = vehicleRegistrationPlate;
    }

    public Integer getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(Integer moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    public Double getDriverLat() {
        return driverLat;
    }

    public void setDriverLat(Double driverLat) {
        this.driverLat = driverLat;
    }

    public Double getDriverLng() {
        return driverLng;
    }

    public void setDriverLng(Double driverLng) {
        this.driverLng = driverLng;
    }

}

