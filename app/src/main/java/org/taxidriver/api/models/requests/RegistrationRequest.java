package org.taxidriver.api.models.requests;

public class RegistrationRequest {

    private String login;
    private String vehicle_registration_plate;
    private Double driver_lat;
    private Double driver_lng;
    private String surname;
    private String name;
    private String phone_number;
    private String password;

    public RegistrationRequest(String login, String password,  String surname, String name,
                               String phone_number, String vehicle_registration_plate, Double driver_lat, Double  driver_lng) {
        this.login = login;
        this.password = password;
        this.surname = surname;
        this.name = name;
        this.phone_number = phone_number;
        this.vehicle_registration_plate = vehicle_registration_plate;
        this.driver_lat = driver_lat;
        this.driver_lng = driver_lng;

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

    public String getPassword() {
        return password;
    }
}
