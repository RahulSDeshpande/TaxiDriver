package org.taxidriver.api.models.simple;

public class Destination {
    private String address_to;
    private Integer count;

    public Destination(String address_to, Integer count) {
        this.address_to = address_to;
        this.count = count;
    }

    public String getAddress_to() {
        return address_to;
    }

    public Integer getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "address_to='" + address_to + '\'' +
                ", count=" + count +
                '}';
    }
}
