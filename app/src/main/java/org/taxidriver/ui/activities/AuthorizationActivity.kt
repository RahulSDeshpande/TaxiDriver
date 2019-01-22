package org.taxidriver.ui.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.taxidriver.R
import org.taxidriver.api.models.requests.AuthorizationRequest
import org.taxidriver.api.models.responses.AuthorizationResponse
import org.taxidriver.app.App
import org.taxidriver.utils.LOGIN_PASSWORD_SHARED_PREFERENCE
import org.taxidriver.utils.LOG_IN_SP
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthorizationActivity : AppCompatActivity() {

    var login: EditText? = null
    var password: EditText? = null
    var submit: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        login = findViewById(R.id.login)
        password = findViewById(R.id.password)
        submit = findViewById(R.id.submit)

        submit?.setOnClickListener {
            when {
                login?.text?.toString()?.isEmpty()!! -> Toast.makeText(this, "Неверний номер", Toast.LENGTH_SHORT).show()
                password?.text?.toString()?.isEmpty()!! -> Toast.makeText(this, "Неверний пароль", Toast.LENGTH_SHORT).show()
                else -> {
                    submit?.isActivated = false
                    App.api?.authorization(AuthorizationRequest(
                            login?.text.toString(), password?.text.toString())
                    )?.enqueue(object : Callback<AuthorizationResponse> {
                        override fun onFailure(call: Call<AuthorizationResponse>?, t: Throwable?) {
                            submit?.isActivated = true
                            Toast.makeText(this@AuthorizationActivity, call?.request()?.body()?.toString(), Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(call: Call<AuthorizationResponse>?, response: Response<AuthorizationResponse>?) {
                            if(response?.body()?.status!!){
                                val client = response.body()?.driverDetail
                                val r = response.body()
//                                Log.d("AuthorizationResponse", client?.toString())
                                with(getSharedPreferences(LOGIN_PASSWORD_SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()){
                                    putInt("driver_id", r?.driverId!!)
                                    putString("driver_phone", client?.phoneNumber)
                                    putString("surname_and_name", "${client?.surname} ${client?.name}")
                                    putString("vehicle_registration_plate", client?.vehicleRegistrationPlate)
                                    if (client != null) {
                                        putInt("money_balance", client.moneyBalance)
                                    }
                                    apply()
                                }
                                Toast.makeText(this@AuthorizationActivity, "Авторизация успешна", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@AuthorizationActivity, MainActivity::class.java))
                                finish()
                            }else{
                                password?.text?.clear()
                                Toast.makeText(this@AuthorizationActivity, "Неверный номер или пароль", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            }
        }
    }
}
