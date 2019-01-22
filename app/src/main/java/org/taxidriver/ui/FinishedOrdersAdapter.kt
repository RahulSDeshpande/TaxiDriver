package org.taxidriver.ui

import android.app.Activity
import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import org.taxidriver.R


class FinishedOrdersAdapter : RecyclerView.Adapter<FinishedOrdersAdapter.FinishedOrderViewHolder> {

    private var activity: Activity
    private var finishedOrdersJsonArray: JSONArray


    internal constructor(activity: Activity, finishedOrdersJsonArray: JSONArray) : super() {
        this.activity = activity
        this.finishedOrdersJsonArray = finishedOrdersJsonArray
    }

    inner class FinishedOrderViewHolder : RecyclerView.ViewHolder {

        internal var layoutItemView: View?
        internal var t_v_data: TextView?
        internal var t_v_time: TextView?
        internal var t_v_from: TextView?
        internal var t_v_to: TextView?
        internal var t_v_order_cost: TextView?


        internal constructor(layoutItemView: View?) : super(layoutItemView)  {
            this.layoutItemView = layoutItemView
            t_v_data = layoutItemView?.findViewById(R.id.t_v_date) as TextView?
            t_v_time = layoutItemView?.findViewById(R.id.t_v_time)
            t_v_from = layoutItemView?.findViewById(R.id.t_v_from) as TextView?
            t_v_to = layoutItemView?.findViewById(R.id.t_v_to) as TextView?
            t_v_order_cost = layoutItemView?.findViewById(R.id.t_v_order_cost) as TextView?
            this.layoutItemView?.setOnClickListener {
                val id = this.layoutItemView?.getTag() as Int
                val finishedOrder = JSONObject(finishedOrdersJsonArray.get(id).toString())
                val client_name: String = finishedOrder.getString("client_name")
                val client_phone: String = finishedOrder.getString("client_phone")
                val from: String = finishedOrder.getString("from")
                val to: String = finishedOrder.getString("to")
                val taxi_must_arrive_at_time: String = finishedOrder.getString("taxi_must_arrive_at_time")
                val order_cost: String = finishedOrder.getString("order_cost")
                //val distance_between_from_and_to: String = finishedOrder.getString("distance_between_from_and_to")
                val payment_type: Int = finishedOrder.getInt("payment_type")
                createFinishedOrderDetailsDialog(client_name, client_phone, from, to, taxi_must_arrive_at_time, order_cost, payment_type)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): FinishedOrderViewHolder {
//        val v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.finished_orders, viewGroup, false)
        val v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.finished_order_item, viewGroup, false)
        val pvh = FinishedOrderViewHolder(v)
        return pvh
    }

    override fun onBindViewHolder(personViewHolder: FinishedOrderViewHolder, i: Int) {
        personViewHolder.layoutItemView?.setTag(i)
        val finishedOrder = JSONObject(finishedOrdersJsonArray.get(i).toString())
        val dt = finishedOrder.getString("date_time").split(' ')
        personViewHolder.t_v_data?.text = dt[0]
        personViewHolder.t_v_time?.text = dt[1]
        personViewHolder.t_v_from?.text = finishedOrder.getString("from")
        personViewHolder.t_v_to?.text = finishedOrder.getString("to")
        personViewHolder.t_v_order_cost?.text = finishedOrder.getString("order_cost")
    }

    override fun getItemCount(): Int {
        return finishedOrdersJsonArray.length()
    }

    private fun createFinishedOrderDetailsDialog
    (client_name: String, client_phone: String, from: String, to: String, taxi_must_arrive_at_time: String, order_cost: String, payment_type: Int)
    {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.getLayoutInflater()
        val dialogView = inflater.inflate(R.layout.alert_dialog_new_order, null)
        builder.setView(dialogView)
        val t_v_client_name = dialogView.findViewById(R.id.t_v_client_name) as TextView?
        t_v_client_name?.setText(client_name)
        val t_v_client_phone = dialogView.findViewById(R.id.t_v_client_phone) as TextView?
        t_v_client_phone?.setText(client_phone)
        val t_v_from = dialogView.findViewById(R.id.t_v_from) as TextView?
        t_v_from?.setText(from)
        val t_v_to = dialogView.findViewById(R.id.t_v_to) as TextView?
        t_v_to?.setText(to)
        val t_v_taxi_must_arrive_at_time = dialogView.findViewById(R.id.t_v_taxi_must_arrive_at_time) as TextView?
        t_v_taxi_must_arrive_at_time?.setText(taxi_must_arrive_at_time)
        val t_v_order_cost = dialogView.findViewById(R.id.t_v_order_cost) as TextView?
        t_v_order_cost?.setText("${order_cost}руб.")
        //val t_v_distance_between_from_and_to = dialogView.findViewById(R.id.t_v_distance_between_from_and_to) as TextView?
        //t_v_distance_between_from_and_to?.setText("${distance_between_from_and_to}км.")
        val t_v_payment_type = dialogView.findViewById(R.id.t_v_payment_type) as TextView?
        var payment_type_string = ""
        when(payment_type) {
            0 -> payment_type_string = "Наличные"
            1 -> payment_type_string = "Карта"
        }
        t_v_payment_type?.setText("${payment_type_string}")
        builder.setTitle("Детали заказа")
                .setCancelable(true)
        val alert = builder.create()
        alert.show()
    }
}
