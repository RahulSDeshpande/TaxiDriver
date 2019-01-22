package org.taxidriver.api.models.responses;


import org.taxidriver.api.models.simple.Destination;

import java.util.ArrayList;



public class DestinationsResponse {
    private Boolean status;
    private ArrayList<Destination> list;

    public DestinationsResponse(Boolean status, ArrayList<Destination> list) {
        this.status = status;
        this.list = list;
    }

    public Boolean getStatus() {
        return status;
    }

    public ArrayList<Destination> getList() {
        return list;
    }

    @Override
    public String toString() {
        return "DestinationsResponse{" +
                "status=" + status +
                ", list=" + list +
                '}';
    }
}
