package org.taxidriver.ui.activities

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import org.json.JSONException
import org.json.JSONObject
import org.taxidriver.R
import org.taxidriver.app.App.Companion.websocketClient

class SettingsActivity : AppCompatActivity {

    private var list_of_finished_orders: Button? = null
    private var button_terms_of_work: Button? = null
    private var button_set_radius: Button? = null
    private var radio_group_order_radius: RadioGroup? = null
    private var radius_in_meters = 500


    constructor() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<TextView>(R.id.navigationTitle).text = "Настройки"
        findViewById<ImageView>(R.id.settingsBtn).visibility = View.INVISIBLE
        var back: ImageView? = findViewById(R.id.menuBtn)
        back?.setImageResource(R.drawable.ic_arrow_back_white_24dp)
        back?.setOnClickListener { finish() }

        list_of_finished_orders = findViewById(R.id.list_of_finished_orders)
        button_terms_of_work = findViewById(R.id.button_terms_of_work)
        button_set_radius = findViewById(R.id.button_set_radius)

        list_of_finished_orders?.setOnClickListener {
            val finished_orders_intent = Intent(this, FinishedOrdersListActivity::class.java)
            startActivity(finished_orders_intent)
        }
        button_terms_of_work?.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Условия работы")
                    .setMessage(getString(R.string.terms_of_work))
                    .setCancelable(true)
					.setPositiveButton("OK", {_, _ ->})
            val alert = builder.create()
            alert.show()
        }
        button_set_radius?.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = this.getLayoutInflater()
            val dialogView = inflater.inflate(R.layout.alert_dialog_set_order_radius, null)
            builder.setView(dialogView)
            radio_group_order_radius = dialogView.findViewById(R.id.radio_group_order_radius)
            radio_group_order_radius?.setOnCheckedChangeListener {
                _, checked_id ->
                when(checked_id) {
                    R.id.radius_500m -> {
                        radius_in_meters = 500
                    }
                    R.id.radius_1km -> {
                        radius_in_meters = 1000
                    }
                    R.id.radius_3km -> {
                        radius_in_meters = 3000
                    }
                    R.id.radius_5km -> {
                        radius_in_meters = 5000
                    }
                    R.id.radius_10km -> {
                        radius_in_meters = 10000
                    }
                    R.id.radius_15km -> {
                        radius_in_meters = 15000
                    }
                }
            }
            val radioButtonID = radio_group_order_radius?.getCheckedRadioButtonId()
            when(radioButtonID) {
                R.id.radius_500m -> {
                    radius_in_meters = 500
                }
                R.id.radius_1km -> {
                    radius_in_meters = 1000
                }
                R.id.radius_3km -> {
                    radius_in_meters = 3000
                }
                R.id.radius_5km -> {
                    radius_in_meters = 5000
                }
                R.id.radius_10km -> {
                    radius_in_meters = 10000
                }
                R.id.radius_15km -> {
                    radius_in_meters = 15000
                }
            }
            builder.setTitle("Радиус заказов")
                    .setCancelable(true)
                    .setPositiveButton("OK", {_, _ ->
                        val order_radius_request = JSONObject()
                        try {
                            order_radius_request.put("request", "order_radius_to_driver")
                            order_radius_request.put("order_radius_to_driver", radius_in_meters)
                            websocketClient.sendMessage(order_radius_request.toString())
                        } catch (e: JSONException) {

                        }
                    })
            val alert = builder.create()
            alert.show()
        }
    }
}
