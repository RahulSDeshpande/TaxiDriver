package org.taxidriver.ui.activities

import android.content.Context
import android.content.Intent
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import org.json.JSONException
import org.json.JSONObject
import org.taxidriver.R
import org.taxidriver.api.models.requests.RegistrationRequest
import org.taxidriver.api.models.responses.RegistrationResponse
import org.taxidriver.app.App
import org.taxidriver.utils.*
import org.taxidriver.websocketclient.listeners.NewMessageListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity {

    private var edit_text_driver_name: EditText? = null
    private var edit_text_driver_surname: EditText? = null
    private var edit_text_driver_phone: EditText? = null
    private var edit_text_driver_vehicle_registration_plate: EditText? = null
    private var edit_text_driver_login: EditText? = null
    private var edit_text_driver_password: EditText? = null
    private var button_registration: Button? = null
    private var newMessageListener: NewMessageListener? = null
    private var driver_phone = ""
    private var driver_name = ""
    private var driver_vehicle_registration_plate = ""
    private var driver_surname = ""
    private var driver_login = ""
    private var driver_password = ""

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var driver_lat: Double? = 0.0
    private var driver_lng: Double? = 0.0


    constructor() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        edit_text_driver_name = findViewById(R.id.edit_text_driver_name)
        edit_text_driver_surname = findViewById(R.id.edit_text_driver_surname)
        edit_text_driver_phone = findViewById(R.id.edit_text_driver_phone)
        edit_text_driver_vehicle_registration_plate = findViewById(R.id.edit_text_driver_vehicle_registration_plate)
        edit_text_driver_login = findViewById(R.id.edit_text_driver_login)
        edit_text_driver_password = findViewById(R.id.edit_text_driver_password)
        button_registration = findViewById(R.id.button_registration)

        getLastLocation{}

        button_registration?.setOnClickListener {

            driver_name = edit_text_driver_name?.text.toString()
            driver_surname = edit_text_driver_surname?.text.toString()
            driver_phone = edit_text_driver_phone?.text.toString()
            driver_vehicle_registration_plate = edit_text_driver_vehicle_registration_plate?.text.toString()
            driver_login = edit_text_driver_login?.text.toString()
            driver_password = edit_text_driver_password?.text.toString()


            when {
                edit_text_driver_name?.text?.isEmpty()!! -> {
                    Toast.makeText(this@RegistrationActivity, "Введите имя", Toast.LENGTH_LONG).show()
                }
                edit_text_driver_phone?.text?.isEmpty()!! -> {
                    Toast.makeText(this@RegistrationActivity, "Введите номер телефона", Toast.LENGTH_LONG).show()
                }
                edit_text_driver_password?.text?.isEmpty()!! -> {
                    Toast.makeText(this@RegistrationActivity, "Введите пароль", Toast.LENGTH_LONG).show()
                }
                else -> {
                    App.api?.registration(RegistrationRequest(
                            driver_login, driver_password, driver_surname, driver_name,
                            driver_phone, driver_vehicle_registration_plate,
                            driver_lat, driver_lng
                            )
                    )?.enqueue(object: Callback<RegistrationResponse> {
                        override fun onFailure(call: Call<RegistrationResponse>?, t: Throwable?) {
                            Toast.makeText(this@RegistrationActivity, "Попытка зарегистрироваться не удалась.", Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<RegistrationResponse>?, response: Response<RegistrationResponse>?) {
                            val r = response?.body()
                            if(r?.status!!){
                                Log.d("AuthorizationResponse", r.toString())
                                with(getSharedPreferences(LOGIN_PASSWORD_SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()){
                                    putInt("driver_id", r.driver_id)
                                    putString("driver_phone", driver_phone)
                                    putString("surname_and_name", "$driver_surname $driver_name}")
                                    putString("driver_vehicle_registration_plate", driver_vehicle_registration_plate)
                                    apply()
                                }
                                Toast.makeText(this@RegistrationActivity, "Вы успешно зарегистрировались", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
                                finish()
                            }
                            else{
                                Toast.makeText(this@RegistrationActivity, "Попытка зарегистрироваться не удалась.\nВозможно, этот номер телефона уже используется.", Toast.LENGTH_LONG).show()
                            }
                        }
                    })
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
                            when(status) {
                                REGISTRATION_SUCCESS_STATUS -> {
                                    val sharedPref = getSharedPreferences(LOGIN_PASSWORD_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                                    with (sharedPref.edit()) {
                                        putBoolean(IS_LOGIN_SUCCESS, true)
                                        putString("driver_login", driver_login)
                                        putString("driver_password", driver_password)
                                        commit()
                                    }
                                    runOnUiThread {
                                        val toast = Toast.makeText(this, description, Toast.LENGTH_SHORT)
                                        toast.setGravity(Gravity.CENTER, 0, 0)
                                        toast.show()
                                        finish()
                                        startActivity(Intent(this, MainActivity::class.java))
                                    }
                                }
                                REGISTRATION_FAILED_DRIVER_EXISTS_STATUS -> {
                                    runOnUiThread {
                                        val toast = Toast.makeText(this, description, Toast.LENGTH_SHORT)
                                        toast.setGravity(Gravity.CENTER, 0, 0)
                                        toast.show()
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

    @SuppressWarnings("MissingPermission")
    private fun getLastLocation(callback: () -> Unit) {
        mFusedLocationClient?.getLastLocation()
                ?.addOnCompleteListener(this, object : OnCompleteListener<Location> {
                    override fun onComplete(task: Task<Location>) {
                        if (task.isSuccessful() && task.getResult() != null) {

                            val mLastLocation = task.getResult()

                            driver_lat = mLastLocation.getLatitude()
                            driver_lng = mLastLocation.getLongitude()
                            callback()
                        } else {
                            val toast = Toast.makeText(this@RegistrationActivity, "Нет координат устройства!", Toast.LENGTH_LONG)
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                        }
                    }
                })
    }


    companion object {
        const val REGISTRATION_SUCCESS_STATUS = 1
        const val REGISTRATION_FAILED_DRIVER_EXISTS_STATUS = -1
    }
}
