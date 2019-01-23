package org.taxidriver.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import org.taxidriver.api.models.requests.NewOrderRequest
import org.taxidriver.api.models.responses.RequestOrdersResponse
import org.taxidriver.app.App
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReceiverOnWork: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val bundle = p1?.getExtras()
        var driver_id: Int? = bundle?.getInt("driver_id")
        var lat: Double? = bundle?.getDouble("lat")
        var lng: Double? = bundle?.getDouble("lng")


        App.api?.requestOrders(NewOrderRequest(
                driver_id, lat, lng
        ))?.enqueue(object : Callback<RequestOrdersResponse> {
            override fun onFailure(call: Call<RequestOrdersResponse>?, t: Throwable?) {
                Toast.makeText(p0, "NEEETT", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<RequestOrdersResponse>?, response: Response<RequestOrdersResponse>?) {
                Toast.makeText(p0, "DAAAAAAA", Toast.LENGTH_LONG).show()
            }
        })




//        handleEvent(snatchId, onlineTime, isOnline)
    }

    fun handleEvent(snatchId: String, time: Long?, isOnline: Boolean?) {}
}