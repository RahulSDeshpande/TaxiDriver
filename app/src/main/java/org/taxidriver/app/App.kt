package org.taxidriver.app

import android.app.Application
import android.util.Log
import org.json.JSONObject
import org.taxidriver.api.ClientApi
import org.taxidriver.websocketclient.TaxiWebSocketClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ArrayBlockingQueue

class App: Application {

    constructor() {

    }

    override fun onCreate() {
        super.onCreate()

        websocketClient.connectToServer()

        retrofit = Retrofit.Builder()
//                .baseUrl("https://texi-server.herokuapp.com/api/driver/")
                .baseUrl("http://185.241.55.175/api/driver/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        api = retrofit?.create(ClientApi::class.java)
    }

    companion object {
        val channel = ArrayBlockingQueue<JSONObject>(100)
        val websocketClient = TaxiWebSocketClient()

        var api: ClientApi? = null
        var retrofit: Retrofit? = null
    }
}