package org.taxidriver.api.models.simple;

public class Driver {
    private Integer id;
    private String surname;
    private String name;
    private String phone_number;
    private String vehicle_registration_plate;
    private Double driver_lat;
    private Double driver_lng;

    public Driver(Integer id, String surname, String name, String phone_number, String vehicle_registration_plate, Double driver_lat, Double driver_lng) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.phone_number = phone_number;
        this.vehicle_registration_plate = vehicle_registration_plate;
        this.driver_lat = driver_lat;
        this.driver_lng = driver_lng;
    }

    public Integer getId() {
        return id;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getVehicle_registration_plate() {
        return vehicle_registration_plate;
    }

    public Double getDriver_lat() {
        return driver_lat;
    }

    public Double getDriver_lng() {
        return driver_lng;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", surname='" + surname + '\'' +
                ", name='" + name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", vehicle_registration_plate='" + vehicle_registration_plate + '\'' +
                ", driver_lat=" + driver_lat +
                ", driver_lng=" + driver_lng +
                '}';
    }
}
