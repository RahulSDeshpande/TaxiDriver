package org.taxidriver.api.models.requests;

// todo: Class that represent response about driver request
public class RToDriverRequest {
    private Integer order_id;
    private String test;
    private Boolean response_type;

    public RToDriverRequest(Integer order_id, String test, Boolean response_type) {
        this.order_id = order_id;
        this.test = test;
        this.response_type = response_type;
    }
}
