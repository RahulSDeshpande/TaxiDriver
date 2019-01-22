package org.taxidriver.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.taxidriver.R

class FinishedOrderActivity : AppCompatActivity {

    private var text_view_cost: TextView? = null
    private var button_close: Button? = null


    constructor() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finished_order)

        val intent_for_finished_taximeter_activity = getIntent()
        val order_cost = intent_for_finished_taximeter_activity.getIntExtra("order_cost", 0)
        text_view_cost = findViewById(R.id.text_view_cost)
        button_close = findViewById(R.id.button_close)
        button_close?.setOnClickListener {
            finish()
        }
        text_view_cost?.setText("$order_cost")
    }
}
