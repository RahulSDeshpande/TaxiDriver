package org.taxidriver.api.models.requests;

public class LikeToDriverRequest {
    private Integer order_id;
    private Boolean like;

    public LikeToDriverRequest(Integer order_id, Boolean like) {
        this.order_id = order_id;
        this.like = like;
    }
}
