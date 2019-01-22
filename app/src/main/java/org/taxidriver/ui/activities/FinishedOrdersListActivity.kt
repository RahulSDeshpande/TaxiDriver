package org.taxidriver.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import org.taxidriver.R
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.taxidriver.app.App
import org.taxidriver.ui.FinishedOrdersAdapter
import org.taxidriver.utils.*
import org.taxidriver.websocketclient.listeners.NewMessageListener
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class FinishedOrdersListActivity : AppCompatActivity {

    private var newMessageListener: NewMessageListener? = null
    private var rvfinishedOrders: RecyclerView? = null
    private var radioGroup: RadioGroup? = null
    private var order_list: JSONArray? = null
    private var now = SimpleDateFormat("yyyy-MM-dd").format(Date())

    constructor() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finished_orders)

        newMessageListener = object: NewMessageListener {
            override fun onNewMessage() {
                startHandlingMessagesFromServer()
            }
        }
        App.websocketClient.newMessageListener = newMessageListener

        rvfinishedOrders = findViewById(R.id.rvfinishedOrders)
        rvfinishedOrders?.layoutManager = LinearLayoutManager(this@FinishedOrdersListActivity)
        // сделать запрос на сервер и получить массив выполненных заказов
        val get_finished_orders_json = JSONObject()
        get_finished_orders_json.put("request", "get_finished_orders")
        get_finished_orders_json.put("driver_id",getSharedPreferences(LOGIN_PASSWORD_SHARED_PREFERENCE, Context.MODE_PRIVATE).getInt("driver_id", 0))
        App.websocketClient.sendMessage(get_finished_orders_json.toString())

        // settings of header
        var back: ImageView? = findViewById(R.id.menuBtn)
        back?.setImageResource(R.drawable.ic_arrow_back_white_24dp)
        back?.setOnClickListener { finish() }
        findViewById<ImageView>(R.id.settingsBtn).visibility = View.INVISIBLE

        radioGroup = findViewById(R.id.time_interval)
        radioGroup?.setOnCheckedChangeListener { _, _ ->  App.websocketClient.sendMessage(get_finished_orders_json.toString())}
    }

    override fun onResume() {
        super.onResume()
        newMessageListener = object: NewMessageListener {
            override fun onNewMessage() {
                this@FinishedOrdersListActivity.startHandlingMessagesFromServer()
            }
        }
        App.websocketClient.newMessageListener = newMessageListener
        // сделать запрос на сервер и получить массив выполненных заказов
        val get_finished_orders_json = JSONObject()
        get_finished_orders_json.put("request", "get_finished_orders")
        App.websocketClient.sendMessage(get_finished_orders_json.toString())
    }


    private fun startHandlingMessagesFromServer() {
        Thread {
            var jsonObject: JSONObject? = null
            try {
                jsonObject = App.channel.take()
                if (jsonObject != null) {
                    println(jsonObject.toString())
                    val state = jsonObject.get("state")
                    when (state) {
                        STATE_OPENED -> {

                        }
                        STATE_NEW_MESSAGE -> {
                            val response = jsonObject.getString("response")
                            when(response) {
                                "finished_orders" -> {
                                    val orders = jsonObject.getJSONArray("orders")
                                    var array:ArrayList<JSONObject> = ArrayList()
                                    var total_cost:Int = 0
                                    for(i in 0..(orders.length()-1)){
                                        val order = orders.getJSONObject(i)
                                        when(radioGroup?.checkedRadioButtonId){
                                            R.id.day->{
                                                val order_date = order.getString("date_time").split(" ")[0]
                                                if(now == order_date){
                                                    array.add(orders.getJSONObject(i))
                                                    total_cost+=order.getInt("order_cost")
                                                }
                                            }
                                            R.id.week->{
                                                val n = now.split("-")
                                                val o = order.getString("date_time").split(" ")[0].split("-")

                                                if(n[0]==o[0]&&n[1]==o[1]&&((n[2].toInt()-7)<=o[2].toInt())){
                                                    array.add(orders.getJSONObject(i))
                                                    total_cost+=order.getInt("order_cost")
                                                }
                                            }
                                            R.id.month->{
                                                val n = now.split("-")
                                                val o = order.getString("date_time").split(" ")[0].split("-")

                                                if(n[0]==o[0]&&n[1]==o[1]){
                                                    array.add(orders.getJSONObject(i))
                                                    total_cost+=order.getInt("order_cost")
                                                }
                                            }
                                            R.id.all_time->{
                                                array.add(orders.getJSONObject(i))
                                                total_cost+=order.getInt("order_cost")
                                            }
                                        }

                                    }

                                    runOnUiThread {
                                        val finishedOrdersAdapter = FinishedOrdersAdapter(this@FinishedOrdersListActivity, JSONArray(array))
                                        rvfinishedOrders?.adapter = finishedOrdersAdapter
                                        findViewById<TextView>(R.id.totalCost).text = total_cost.toString()
                                    }
                                }
                            }

                        }
                        STATE_ERROR -> {

                        }
                        STATE_CLOSED -> {
                            runOnUiThread {

                            }
                        }
                    }
                }
            } catch (e: JSONException) {
                Log.d("error", e.message)
            } catch (e: InterruptedException) {
                //jsonObject = e.message?:""
            }
        }.start()
    }
}
