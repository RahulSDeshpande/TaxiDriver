package org.taxidriver.ui.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.taxidriver.R
import org.taxidriver.utils.LOGIN_PASSWORD_SHARED_PREFERENCE
import org.taxidriver.utils.LOG_IN_SP

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        findViewById<TextView>(R.id.sign_in)?.setOnClickListener {
            startActivity(Intent(this, AuthorizationActivity::class.java))
        }
        findViewById<TextView>(R.id.sign_up)?.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        if(getSharedPreferences(LOGIN_PASSWORD_SHARED_PREFERENCE, Context.MODE_PRIVATE).getInt("driver_id", 0) != 0){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
