package org.taxidriver.ui.activities

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import org.json.JSONObject
import org.taxidriver.R
import org.taxidriver.app.App

class ConnectingToServerActivity : AppCompatActivity {


    constructor() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connecting_to_server)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Подключение к серверу!")
                .setMessage("Подключение к серверу!")
                .setCancelable(false)
        val alert = builder.create()
        alert.show()
        Handler().postDelayed({
            alert.dismiss()
            if (!App.websocketClient.isOpen()) {
                var toast = Toast.makeText(this, "Не удалось подключиться к серверу", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            } else {
                var toast = Toast.makeText(this, "Удалось подключиться к серверу", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                val json = JSONObject()
                json.put("request", "driver_connected")
                App.websocketClient.sendMessage(json.toString())
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 4000)
    }
}
