package org.taxidriver.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import org.taxidriver.R

class LockedAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locked_account)
        findViewById<Button>(R.id.button_exit)?.setOnClickListener {
            finish()
        }
    }
}
