package org.taxidriver.ui.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject
import org.taxidriver.R
import org.taxidriver.app.App
import org.taxidriver.utils.*
import org.taxidriver.websocketclient.listeners.NewMessageListener

class LoginActivity : AppCompatActivity {

    private var edit_text_login: EditText? = null
    private var edit_text_password: EditText? = null
    private var button_login: Button? = null
    private var button_registration: Button? = null
    private var newMessageListener: NewMessageListener? = null


    constructor() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        newMessageListener = object: NewMessageListener {
            override fun onNewMessage() {
                startHandlingMessagesFromServer()
            }
        }
        App.websocketClient.newMessageListener = newMessageListener

        edit_text_login = findViewById(R.id.edit_text_login)
        edit_text_password = findViewById(R.id.edit_text_password)
        button_login = findViewById(R.id.button_login)
        button_registration = findViewById(R.id.button_registration)
        findViewById<Button>(R.id.auth_cancel).setOnClickListener {
            findViewById<ConstraintLayout>(R.id.content).visibility = View.VISIBLE
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
            findViewById<Button>(R.id.auth_cancel).visibility = View.INVISIBLE
        }


        val sharedPref = getSharedPreferences(LOGIN_PASSWORD_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        if (sharedPref.getBoolean(IS_LOGIN_SUCCESS, false)) {
            val driverLoginJson = JSONObject()
            driverLoginJson.put("request", "driver_validation")
            driverLoginJson.put("login", sharedPref.getString("driver_login",""))
            driverLoginJson.put("password", sharedPref.getString("driver_password",""))
            App.websocketClient.sendMessage(driverLoginJson.toString())
            findViewById<ConstraintLayout>(R.id.content).visibility = View.INVISIBLE
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            findViewById<Button>(R.id.auth_cancel).visibility = View.VISIBLE
        }

        button_login?.setOnClickListener {
            // todo: отправить логин\пароль на сервер
            val login = edit_text_login?.text.toString()
            val password = edit_text_password?.text.toString()
            val driverLoginJson = JSONObject()
            if (login != "" && password != "") {
                edit_text_login?.setError(null)
                edit_text_password?.setError(null)
                driverLoginJson.put("request", "driver_validation")
                driverLoginJson.put("login", login)
                driverLoginJson.put("password", password)
                App.websocketClient.sendMessage(driverLoginJson.toString())
            } else {
                if (login == "") {
                    edit_text_login?.setError("Введите логин")
                }
                if (password == "") {
                    edit_text_password?.setError("Введите пароль")
                }
            }
        }

        button_registration?.setOnClickListener {
            finish()
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun startHandlingMessagesFromServer() {
        Thread {
            var jsonObject: JSONObject? = null
            try {
                jsonObject = App.channel.take()
                if (jsonObject != null) {
                    val state = jsonObject.get("state")
                    when (state) {
                        STATE_NEW_MESSAGE -> {
                            val response = jsonObject.getString("response")
                            val status = jsonObject.getInt("status")
                            val description = jsonObject.getString("description")
                            val driver_id = jsonObject.getInt("driver_id")
                            when(response){
                                "server_validated_driver"->{
                                    when(status) {
                                        LOGIN_SUCCESS_STATUS -> {
                                            val sharedPref = getSharedPreferences(LOGIN_PASSWORD_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                                            with (sharedPref.edit()) {
                                                putBoolean(IS_LOGIN_SUCCESS, true)
                                                putInt("driver_id", driver_id)
                                                putString("driver_login", jsonObject.getString("login"))
                                                putString("driver_password", jsonObject.getString("password"))
                                                commit()
                                            }
                                            runOnUiThread {
                                                val toast = Toast.makeText(this, description, Toast.LENGTH_SHORT)
                                                toast.setGravity(Gravity.CENTER, 0, 0)
                                                toast.show()
                                                finish()
                                                val mainActivity = Intent(this, MainActivity::class.java)
                                                mainActivity.putExtra("driver_id", driver_id)
                                                startActivity(mainActivity)
                                            }
                                        }
                                    }
                                }
                                "driver_was_blocked"->{
                                    runOnUiThread {
                                        startActivity(Intent(this@LoginActivity, LockedAccountActivity::class.java))
                                        finish()
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

            } catch (e: InterruptedException) {

            }
        }.start()
    }

    companion object {
        const val LOGIN_SUCCESS_STATUS = 1
    }
}
