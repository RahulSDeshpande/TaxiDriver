package org.taxidriver.api;

import org.taxidriver.api.models.requests.*;
import org.taxidriver.api.models.responses.*;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;



public interface ClientApi {
    @POST("registration/")
    Call<RegistrationResponse> registration(@Body RegistrationRequest request);

    @POST("authorization/")
    Call<AuthorizationResponse> authorization(@Body AuthorizationRequest request);

    @POST("status/change/")
    Call<StatusOnResponse> setOnWork(@Body StatusOnRequest request);

    @POST("order/create/")
    Call<NewOrderResponse> createNewOrder(@Body NewOrderRequest request);

    @POST("order/cancel/")
    Call<CancelOrderResponse> cancelOrder(@Body CancelOrderRequest request);

    @POST("request/destinations/")
    Call<DestinationsResponse> requestFavouriteDestinations(@Body DestinationsRequest request);

    @POST("request/driver/position/")
    Call<DriverPositionResponse> requestDriverPosition(@Body DriverPositionRequest request);

    @POST("driver/response/")
    Call<RToDriverResponse> newResponseAboutDriver(@Body RToDriverRequest request);

    @POST("driver/like/")
    Call<LikeToDriverResponse> likeDriver(@Body LikeToDriverRequest request);
}
