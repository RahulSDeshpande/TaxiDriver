package org.taxidriver.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import org.json.JSONObject
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PointF
import android.location.Location
import android.media.MediaPlayer
import android.net.Uri
import org.json.JSONException
import org.taxidriver.app.App.Companion.channel
import org.taxidriver.utils.*
import android.util.Log
import android.widget.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import org.taxidriver.app.App.Companion.websocketClient
import org.taxidriver.websocketclient.listeners.NewMessageListener
import android.support.design.widget.NavigationView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*
import org.taxidriver.R
import android.support.v7.widget.Toolbar
import android.view.*
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.driving.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import kotlinx.android.synthetic.main.activity_authorization.*
import org.taxidriver.api.models.requests.StatusOnRequest
import org.taxidriver.api.models.responses.StatusOnResponse
import org.taxidriver.app.App
import java.text.SimpleDateFormat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity, NavigationView.OnNavigationItemSelectedListener, UserLocationObjectListener {
    private val MY_PERMISSIONS_REQUEST_LOCATION = 1
    private val MY_PERMISSIONS_REQUEST_PHONE_CALL = 2
    private var newMessageListener: NewMessageListener? = null
    private var customToolbar: Toolbar? = null
    private var balance_text_view: TextView? = null
    private var button_arrived_to_client: Button? = null
    private var button_settings: ImageView? = null
    private var button_start_ride: Button? = null
    private var button_open_navigator: Button? = null
    private var button_call_to_client: Button? = null
    private var button_map: Button? = null
    private var progressbar: ProgressBar? = null
    private var order_id = ""
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var client_lat: Double? = null
    private var client_lng: Double? = null
    private var driver_lat: Double? = null
    private var driver_lng: Double? = null
    private var mMap: MapView? = null
    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null
    private var mapObjects: MapObjectCollection? = null
    private var layout_for_map: RelativeLayout? = null
    private var coordinatesList: MutableList<LatLng>? = null
    private var from = ""
    private var to = ""
    private var playedTimes = 0
    private var drivingRouteListener: DrivingSession.DrivingRouteListener? = null
    private var userLocationObjectListener: UserLocationObjectListener? = null
    private var order_cost = 0
    private var nMenu: NavigationView? = null
    private var optionsMenu: Menu? = null
    private var workSwitch: Switch? = null
    private var userLocationLayer: UserLocationLayer? = null
    private var driver_phone: String? = ""
    private var driver_name: String? = ""
    private var driver_vehicle_registration_plate: String? = ""
    private var driver_id: Int? = null
    private var money_balance_value: Int? = 0
    private var onWork: Boolean? = false
    private var driverStatus: Int? = 0


    constructor() {

    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey("e533b745-5bf0-4ea7-b715-d6799f3f9820")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)

        drivingRouteListener = object : DrivingSession.DrivingRouteListener {

            override fun onDrivingRoutes(routes: List<DrivingRoute>) {
                mapObjects?.addPolyline(routes[0].geometry)
                val fromPoint = routes[0].geometry.points[0]
                val toPoint = mapObjects?.addPlacemark(routes[0].geometry.points.last())
                toPoint?.setIcon(ImageProvider.fromResource(this@MainActivity, R.drawable.ic_location))
                mMap?.map?.move(
                        com.yandex.mapkit.map.CameraPosition(fromPoint!!, 18.5f, 0.0f, 0.0f),
                        Animation(Animation.Type.SMOOTH, 5f), null)
            }

            override fun onDrivingRoutesError(var1: Error) {
                Log.d("error", var1.toString())
            }

        }


        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        grantLocationPermission()
        grantCallPhonePermission()
        val sharedPref = getSharedPreferences(LOGIN_PASSWORD_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        driver_id = sharedPref.getInt("driver_id", 0)
        driver_phone = sharedPref.getString("driver_phone", "")
        driver_name = sharedPref.getString("surname_and_name", "")
        driver_vehicle_registration_plate = sharedPref.getString("driver_vehicle_registration_plate", "")
        money_balance_value = sharedPref.getInt("money_balance", 0)

        customToolbar = findViewById(R.id.custom_toolbar)
        setSupportActionBar(customToolbar)
        balance_text_view = findViewById(R.id.balance_value)

        button_settings = findViewById(R.id.button_settings)
        button_arrived_to_client = findViewById(R.id.button_arrived_to_client)
        button_start_ride = findViewById(R.id.button_start_ride)
        button_call_to_client = findViewById(R.id.button_call_to_client)
        button_open_navigator = findViewById(R.id.button_open_navigator)
        //progressbar = findViewById(R.id.progressbar)
        layout_for_map = findViewById(R.id.layout_for_map)
        // подключение карты
        mMap = findViewById(R.id.map)
        mapObjects = mMap?.map?.mapObjects?.addCollection()
        drivingRouter = MapKitFactory.getInstance().createDrivingRouter()

        userLocationLayer = mMap?.map?.userLocationLayer
        userLocationLayer!!.isEnabled = true
        userLocationLayer?.isHeadingEnabled = true
        userLocationLayer?.setObjectListener(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation {
            mMap?.getMap()?.move(
                    com.yandex.mapkit.map.CameraPosition(Point(driver_lat!!, driver_lng!!), 18.5f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 5f), null)
        }

        button_arrived_to_client?.setOnClickListener {
            // водитель прибыл к клиенту
            button_arrived_to_client?.visibility = View.GONE
            button_open_navigator?.visibility = View.GONE
            button_start_ride?.visibility = View.VISIBLE
            val driverArrivedToClientJson = JSONObject()
            driverArrivedToClientJson.put("request", "driver_arrived_to_client")
            driverArrivedToClientJson.put("order_id", order_id)
            websocketClient.sendMessage(driverArrivedToClientJson.toString())
            getSharedPreferences(ORDER_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().putInt("process", ORDER_ARRIVED).apply()
        }
        button_start_ride?.setOnClickListener {
            // водитель начал поездку
            mapObjects?.clear()
            val driverStartsRide = JSONObject()
            driverStartsRide.put("request", "driver_starts_ride")
            driverStartsRide.put("order_id", order_id)
            websocketClient.sendMessage(driverStartsRide.toString())
            setInformationVisibility(View.GONE)
            getSharedPreferences(ORDER_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().putInt("process", ORDER_START_RIDE).apply()
            // вызвать окно со счётчиком стоимости
            getLastLocation({
                val intent_invoke_order_activity = Intent(this@MainActivity, TaximeterActivity::class.java)
                intent_invoke_order_activity.putExtra("driver_lat", driver_lat)
                intent_invoke_order_activity.putExtra("driver_lng", driver_lng)
                intent_invoke_order_activity.putExtra("order_id", order_id)
                intent_invoke_order_activity.putExtra("from", from)
                intent_invoke_order_activity.putExtra("to", to)
                intent_invoke_order_activity.putExtra("order_cost", order_cost)
                startActivity(intent_invoke_order_activity)
                button_start_ride?.visibility = View.GONE
                button_call_to_client?.visibility = View.GONE
                workSwitch?.visibility = View.VISIBLE

            })
        }

        button_open_navigator?.setOnClickListener {
            val uri = Uri.parse("yandexnavi://build_route_on_map?lat_to=${client_lat}&lon_to=${client_lng}")
            var intent = Intent(Intent.ACTION_VIEW, uri)
            intent.`package` = "ru.yandex.yandexnavi"

            // Проверяет, установлено ли приложение
            val activities = packageManager.queryIntentActivities(intent, 0)
            val isIntentSafe = activities.size > 0

            if (isIntentSafe) {
                //Запускает Яндекс.Навигатор.
                startActivity(intent)
            } else {
                // Открывает страницу Яндекс.Навигатора в Google Play.
                intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("market://details?id=ru.yandex.yandexnavi")
                startActivity(intent)
            }
        }

        button_call_to_client?.setOnClickListener {
            val tel = getSharedPreferences(ORDER_SHARED_PREFERENCES, Context.MODE_PRIVATE).getString("login", "")
            if(tel.matches(Regex(pattern = "^\\+?[\\d\\-()]+$"))){
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$tel")))
            }
            else{
                Toast.makeText(this@MainActivity, "DriverDetail login number doesn't satisfy requirements.", Toast.LENGTH_LONG).show()
            }
        }

        button_settings?.setOnClickListener {
            val settingsActivityIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsActivityIntent)
        }

        nMenu = findViewById(R.id.main_menu)
        nMenu?.setNavigationItemSelectedListener(this)

        findViewById<ImageView>(R.id.menuBtn).setOnClickListener {
            if (nMenu?.visibility == View.VISIBLE) {
                nMenu?.visibility = View.GONE
            } else {
                nMenu?.visibility = View.VISIBLE
            }

        }

        // Управление сменой используя свич
        workSwitch = findViewById(R.id.work_status_switch)
        workSwitch?.setOnClickListener {

            driverStatus = if (workSwitch?.isChecked!!) 1 else 0

            App.api?.setOnWork(StatusOnRequest(
                    driver_id, 1)
            )?.enqueue(object : Callback<StatusOnResponse> {
                override fun onFailure(call: Call<StatusOnResponse>?, t: Throwable?) {
                    submit?.isActivated = true
                    Toast.makeText(this@MainActivity, call?.request()?.body()?.toString(), Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<StatusOnResponse>?, response: Response<StatusOnResponse>?) {
                    if(response?.body()?.status!!)
                    onWork = workSwitch?.isChecked
                    else Toast.makeText(this@MainActivity, "Ошибка сервера", Toast.LENGTH_SHORT).show()

                }
            })
        }

    }


    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mMap?.onStart()
        drivingRouter?.resume()
    }

    override fun onStop() {
        mMap?.onStop()
        MapKitFactory.getInstance().onStop()
        drivingRouter?.suspend()
        drivingSession?.cancel()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()


        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        grantLocationPermission()
        grantCallPhonePermission()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        balance_text_view!!.text = money_balance_value.toString()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        nMenu?.visibility = View.GONE
        when (item.itemId) {
            R.id.history -> {
                startActivity(Intent(this, FinishedOrdersListActivity::class.java))
            }
            R.id.button_map -> {
                startActivity(Intent(this, MapActivity::class.java))
            }
            R.id.button_exit -> {
                val sharedPreferences: SharedPreferences = getSharedPreferences(LOGIN_PASSWORD_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putBoolean(IS_LOGIN_SUCCESS, false)
                    commit()
                }
                websocketClient.close()
                finish()
                android.os.Process.killProcess(android.os.Process.myPid())
            }
            else -> return false
        }
        return true
    }

    private fun startHandlingMessagesFromServer() {
        Thread {
            var jsonObject: JSONObject? = null
            try {
                jsonObject = channel.take()
                if (jsonObject != null) {
                    val state = jsonObject.getString("state")
                    when (state) {
                        STATE_OPENED -> {

                        }
                        STATE_NEW_MESSAGE -> {
                            val response = jsonObject.getString("response")
                            when (response) {
                                "new_order" -> {
                                    val mediaPlayer = MediaPlayer.create(getApplication(), R.raw.info_voice_new_order)
                                    //todo: mediaPlayer.setVolume() задать громкость оповещения в настройках
                                    mediaPlayer.start()
                                    playedTimes++
                                    mediaPlayer.setOnCompletionListener { mp ->
                                        if (playedTimes < 3) {
                                            mp.start()
                                            playedTimes++
                                        } else {
                                            playedTimes = 0
                                        }
                                    }
                                    val client_name = jsonObject.getString("client_name")
                                    val client_phone = jsonObject.getString("client_phone")
                                    from = jsonObject.getString("from")
                                    to = jsonObject.getString("to")
                                    var taxi_must_arrive_at_time = jsonObject.getString("taxi_must_arrive_at_time")
                                    if (taxi_must_arrive_at_time == "now") {
                                        taxi_must_arrive_at_time = "сейчас"
                                    }
                                    val options = jsonObject.getJSONObject("options")
                                    val is_conditioner_needed = options.getInt("is_conditioner_needed")
                                    val is_child_seat_needed = options.getInt("is_child_seat_needed")
                                    order_id = jsonObject.getString("order_id")
                                    client_lat = jsonObject.getDouble("client_lat")
                                    client_lng = jsonObject.getDouble("client_lng")
                                    val distance_between_from_and_to = jsonObject.getInt("distance_between_from_and_to")
                                    order_cost = jsonObject.getDouble("order_cost").toInt()
                                    val payment_type = jsonObject.getInt("payment_type")
                                    runOnUiThread {
                                        val alert = Dialog(this, R.style.AppTheme)
                                        val inflater = this.layoutInflater
                                        val dialogView = inflater.inflate(R.layout.new_order, null)
                                        alert.setContentView(dialogView)
                                        val t_v_client_name = dialogView.findViewById(R.id.t_v_client_name) as TextView?
                                        t_v_client_name?.text = client_name
                                        val t_v_client_phone = dialogView.findViewById(R.id.t_v_client_phone) as TextView?
                                        t_v_client_phone?.text = client_phone
                                        val t_v_from = dialogView.findViewById(R.id.t_v_from) as TextView?
                                        t_v_from?.setText(from)
                                        val t_v_to = dialogView.findViewById(R.id.t_v_to) as TextView?
                                        t_v_to?.setText(to)
                                        dialogView.findViewById<Button>(R.id.openMapBtn).text = to
                                        val t_v_taxi_must_arrive_at_time = dialogView.findViewById(R.id.t_v_taxi_must_arrive_at_time) as TextView?
                                        t_v_taxi_must_arrive_at_time?.text = taxi_must_arrive_at_time
                                        val t_v_order_cost = dialogView.findViewById(R.id.t_v_order_cost) as TextView?
                                        t_v_order_cost?.text = "${order_cost}руб."
                                        val t_v_distance_between_from_and_to = dialogView.findViewById(R.id.t_v_distance_between_from_and_to) as TextView?
                                        t_v_distance_between_from_and_to?.text = "${distance_between_from_and_to}км."
                                        val t_v_payment_type = dialogView.findViewById(R.id.t_v_payment_type) as TextView?
                                        var payment_type_string = ""
                                        when (payment_type) {
                                            0 -> payment_type_string = "Наличные"
                                            1 -> payment_type_string = "Карта"
                                        }
                                        t_v_payment_type?.text = "$payment_type_string"
                                        dialogView.findViewById<Button>(R.id.goBtn).setOnClickListener {
                                            button_open_navigator?.visibility = View.VISIBLE
                                            button_arrived_to_client?.visibility = View.VISIBLE
                                            button_call_to_client?.visibility = View.VISIBLE
                                            workSwitch?.visibility = View.INVISIBLE
                                            // отправить на сервер сообщение, что водитель взял заказ
                                            val driver_takes_order_json = JSONObject()
                                            driver_takes_order_json.put("request", "driver_takes_order")
                                            driver_takes_order_json.put("order_id", order_id)
                                            driver_takes_order_json.put("driver_id", driver_id)
                                            websocketClient.sendMessage(driver_takes_order_json.toString())
                                            // показать карту
                                            layout_for_map?.setVisibility(View.VISIBLE)
                                            // показать на карте маршрут от водителя до клиента
                                            getLastLocation {
                                                // todo: возможно, что вместо client_lat, client_lng надо from_lang, from_lng
//                                                    val url = makeUrlForLoadDirections(driver_lat!!, driver_lng!!, client_lat!!, client_lng!!)
//                                                    loadDirections(url)
                                                val fromPoint = Point(driver_lat!!, driver_lng!!)
                                                val toPoint = Point(client_lat!!, client_lng!!)
                                                submitRequest(fromPoint, toPoint)
                                            }
                                            button_open_navigator?.callOnClick()
                                            saveOrderInformationOnAccept(order_id, client_lat!!, client_lng!!, client_name, client_phone, from, to, taxi_must_arrive_at_time, distance_between_from_and_to.toString(), order_cost.toString())
                                            alert.dismiss()
                                        }
                                        var back: ImageView? = dialogView.findViewById(R.id.menuBtn)
                                        back?.setImageResource(R.drawable.ic_arrow_back_white_24dp)
                                        back?.setOnClickListener {
                                            // отправить на сервер сообщение, что водитель отклонил заказ
                                            val driver_rejects_order_json = JSONObject()
                                            driver_rejects_order_json.put("request", "driver_rejects_order")
                                            driver_rejects_order_json.put("order_id", order_id)
                                            websocketClient.sendMessage(driver_rejects_order_json.toString())
                                            alert.dismiss()
                                        }
                                        dialogView.findViewById<ImageView>(R.id.settingsBtn).visibility = View.INVISIBLE
                                        dialogView.findViewById<TextView>(R.id.navigationTitle).setText(R.string.new_order)
                                        dialogView.findViewById<Button>(R.id.openMapBtn).setText(to)
                                        alert.setOnCancelListener { alert.findViewById<ImageView>(R.id.menuBtn)?.callOnClick() }
                                        alert.show()
                                    }
                                }
                                "driver_starts_working" -> {
                                    val description = jsonObject.getString("description")
                                    val money_balance = jsonObject.getString("money_balance")
                                    runOnUiThread {
                                        val toast = Toast.makeText(this, description, Toast.LENGTH_SHORT)
                                        toast.setGravity(Gravity.CENTER, 0, 0)
                                        toast.show()
                                        balance_text_view?.setText("$money_balance")
                                    }
                                }
                                "driver_stops_working" -> {
                                    val description = jsonObject.getString("description")
                                    runOnUiThread {
                                        val toast = Toast.makeText(this, description, Toast.LENGTH_SHORT)
                                        toast.setGravity(Gravity.CENTER, 0, 0)
                                        toast.show()
                                    }
                                }
                                "client_is_coming" -> {
                                    val mediaPlayer = MediaPlayer.create(getApplication(), R.raw.beep)
                                    //todo: mediaPlayer.setVolume() задать громкость оповещения в настройках
                                    mediaPlayer.start()
                                    runOnUiThread {
                                        val builder = AlertDialog.Builder(this)
                                        builder.setTitle("Клиент идёт!")
                                                .setCancelable(true)
                                        val alert = builder.create()
                                        alert.show()
                                        /*val toast = Toast.makeText(this, "Клиент идёт", Toast.LENGTH_SHORT)
                                        toast.setGravity(Gravity.CENTER, 0, 0)
                                        toast.show()*/
                                    }
                                }
                                "order_paid_by_card" -> {
                                    // должен прийти баланс, а не стоимость поездки
                                    val money_balance = jsonObject.getString("money_balance")
                                    runOnUiThread {
                                        val toast = Toast.makeText(this, "Баланс: $money_balance", Toast.LENGTH_SHORT)
                                        toast.setGravity(Gravity.CENTER, 0, 0)
                                        toast.show()
                                        balance_text_view?.setText("$money_balance")
                                    }
                                }
                                "active_orders" -> {
                                    val orders = jsonObject.getJSONArray("orders")
                                    if (orders.length() > 0) {
                                        val checkIsHasSomeOrders = JSONObject()
                                        checkIsHasSomeOrders.put("request", "check_order_status")
                                        checkIsHasSomeOrders.put("order_id", orders.getJSONObject(0).getInt("order_id"))
                                        websocketClient.sendMessage(checkIsHasSomeOrders.toString())
                                        with(getSharedPreferences(ORDER_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit()) {
                                            putInt("order_id", orders.getJSONObject(0).getInt("order_id"))
                                            apply()
                                        }
                                        order_id = orders.getJSONObject(0).getInt("order_id").toString()
                                        order_cost = orders.getJSONObject(0).getInt("order_cost")
                                    }
                                }
                                "client_cancels_order" -> {
                                    MediaPlayer.create(application, R.raw.beep).start()
                                    getSharedPreferences(ORDER_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().putInt("process", ORDER_EXECUTED).apply()
                                    runOnUiThread {
                                        setInformationVisibility(View.INVISIBLE)
                                        button_open_navigator?.visibility = View.GONE
                                        button_arrived_to_client?.visibility = View.GONE
                                        button_start_ride?.visibility = View.GONE
                                        button_call_to_client?.visibility = View.GONE
                                        workSwitch?.visibility = View.VISIBLE
                                        Toast.makeText(this, "Заказ отменён клиентом!", Toast.LENGTH_LONG).show()
                                    }
                                }
                                "order_status" -> {
                                    var sharedPreferences = getSharedPreferences(ORDER_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                                    when (jsonObject.getString("order_status")) {
                                        "accepted" -> {
                                            runOnUiThread {
                                                workSwitch?.visibility = View.INVISIBLE
                                                button_open_navigator?.visibility = View.VISIBLE
                                                button_arrived_to_client?.visibility = View.VISIBLE
                                                button_call_to_client?.visibility = View.VISIBLE
                                                workSwitch?.visibility = View.INVISIBLE

                                                // показать карту
                                                layout_for_map?.visibility = View.VISIBLE
                                                // показать на карте маршрут от водителя до клиента
                                                getLastLocation {
                                                    try {
                                                        client_lat = sharedPreferences.getString("client_lat", client_lat!!.toString()).toDouble()
                                                        client_lng = sharedPreferences.getString("client_lng", client_lng!!.toString()).toDouble()
                                                        val fromPoint = Point(driver_lat!!, driver_lng!!)
                                                        val toPoint = Point(client_lat!!, client_lng!!)
                                                        submitRequest(fromPoint, toPoint)
                                                    } catch (e: NullPointerException) {
                                                        Toast.makeText(this@MainActivity, "Не удалось построить маршрут", Toast.LENGTH_SHORT).show()
                                                    }

                                                }
                                                showOrderInformationOnAccept(
                                                        sharedPreferences.getString("name", ""),
                                                        sharedPreferences.getString("login", ""),
                                                        sharedPreferences.getString("from", ""),
                                                        sharedPreferences.getString("to", ""),
                                                        sharedPreferences.getString("arrived_time", ""),
                                                        sharedPreferences.getString("distance", ""),
                                                        sharedPreferences.getString("cost", "")
                                                )
                                            }
                                        }
                                        "arrived" -> {
                                            runOnUiThread {
                                                // водитель прибыл к клиенту
                                                button_arrived_to_client?.visibility = View.GONE
                                                button_open_navigator?.visibility = View.GONE
                                                button_start_ride?.visibility = View.VISIBLE
                                                button_call_to_client?.visibility = View.VISIBLE
                                                showOrderInformationOnAccept(
                                                        sharedPreferences.getString("name", ""),
                                                        sharedPreferences.getString("login", ""),
                                                        sharedPreferences.getString("from", ""),
                                                        sharedPreferences.getString("to", ""),
                                                        sharedPreferences.getString("arrived_time", ""),
                                                        sharedPreferences.getString("distance", ""),
                                                        sharedPreferences.getString("cost", "")
                                                )
                                            }
                                        }
                                        "executing" -> {
                                            runOnUiThread {
                                                // водитель начал поездку
                                                mapObjects?.clear()
                                                // вызвать окно со счётчиком стоимости
                                                getLastLocation({
                                                    val intent_invoke_order_activity = Intent(this@MainActivity, TaximeterActivity::class.java)
                                                    intent_invoke_order_activity.putExtra("driver_lat", driver_lat)
                                                    intent_invoke_order_activity.putExtra("driver_lng", driver_lng)
                                                    intent_invoke_order_activity.putExtra("order_id", sharedPreferences.getInt("order_id", 0).toString())
                                                    intent_invoke_order_activity.putExtra("from", sharedPreferences.getString("from", from))
                                                    intent_invoke_order_activity.putExtra("to", sharedPreferences.getString("to", to))
                                                    intent_invoke_order_activity.putExtra("order_cost", sharedPreferences.getString("cost", order_cost.toString()).toInt())
                                                    startActivity(intent_invoke_order_activity)
                                                })
                                                button_call_to_client?.visibility = View.GONE
                                                button_start_ride?.visibility = View.GONE
                                                workSwitch?.visibility = View.VISIBLE
                                            }
                                        }
                                        "canceled" -> {
                                            runOnUiThread {
                                                button_call_to_client?.visibility = View.GONE
                                                workSwitch?.isChecked = false
                                                Toast.makeText(this, "Заказ отменён клиентом!!!", Toast.LENGTH_LONG).show()
                                            }
                                            sharedPreferences.edit().putInt("process", ORDER_EXECUTED).apply()
                                        }
                                    }
                                }
                                "driver_was_blocked"->{
                                    val date = jsonObject.getString("block_date").split("-")
                                    val blockDate =  Calendar.getInstance()
                                    blockDate.set(date[0].toInt(),date[1].toInt(), date[2].toInt())
                                    blockDate.add(Calendar.DATE, 4)
                                    val displaing_date = SimpleDateFormat("dd-MM-yyyy").format(blockDate.time)
                                    runOnUiThread{
                                        button_arrived_to_client?.visibility = View.GONE
                                        button_open_navigator?.visibility = View.GONE
                                        button_start_ride?.visibility = View.GONE
                                        AlertDialog.Builder(this@MainActivity)
                                                .setTitle("Ваш аккаунт временно заблокирован!")
                                                .setMessage("В ваш адрес отправлено много негативных отзывов. Мы вынуждены временно заблокировать ваш аккаунт. В дальнейшем вы сможете работать с $displaing_date")
                                                .setPositiveButton("ОК", {_,_->
                                                    workSwitch?.isChecked = false
                                                })
                                                .create()
                                                .show()
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
            } catch (e: IllegalArgumentException) {

            }
        }.start()
    }

    private fun saveOrderInformationOnAccept(orderId: String, client_lat: Double, client_lng: Double, name: String, phone: String, from: String, to: String, arrived_time: String, distance: String, cost: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences(ORDER_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("order_id", orderId)
            putString("name", name)
            putString("login", phone)
            putString("from", from)
            putString("to", to)
            putString("arrived_time", arrived_time)
            putString("distance", distance)
            putString("cost", cost)
            putString("client_lat", client_lat.toString())
            putString("client_lng", client_lng.toString())
            putInt("process", ORDER_ACCEPTED)
            commit()
        }
        showOrderInformationOnAccept(name, phone, from, to, arrived_time, distance, cost)
    }


    private fun showOrderInformationOnAccept(name: String, phone: String, from: String, to: String, arrived_time: String, distance: String, cost: String) {
        setInformationVisibility(View.VISIBLE)
        findViewById<TextView>(R.id.o_name).text = "Имя: ${name}"
        findViewById<TextView>(R.id.o_phone).text = "Телефон: ${phone}"
        findViewById<TextView>(R.id.o_from).text = "Откуда: ${from}"
        findViewById<TextView>(R.id.o_to).text = "Куда: ${to}"
        findViewById<TextView>(R.id.o_arrived_time).text = "Время прибытия к клиєнту: ${arrived_time}"
        findViewById<TextView>(R.id.o_distance).text = "Расстояние: ${distance}"
        findViewById<TextView>(R.id.o_cost).text = "Стоимость: ${cost}"
        this.from = from
        this.to = to
        this.order_cost = cost.toInt()
    }

    private fun setInformationVisibility(visibility: Int) {
        findViewById<TextView>(R.id.o_name).visibility = visibility
        findViewById<TextView>(R.id.o_phone).visibility = visibility
        findViewById<TextView>(R.id.o_from).visibility = visibility
        findViewById<TextView>(R.id.o_to).visibility = visibility
        findViewById<TextView>(R.id.o_arrived_time).visibility = visibility
        findViewById<TextView>(R.id.o_distance).visibility = visibility
        findViewById<TextView>(R.id.o_cost).visibility = visibility
    }

    private fun grantLocationPermission() {
        val fineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION)
            }
        } else {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        }
    }

    private fun grantCallPhonePermission() {
        val callPhonePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
        if (callPhonePermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CALL_PHONE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        MY_PERMISSIONS_REQUEST_PHONE_CALL)
            }
        }
    }

    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {

                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //getLastLocation()
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                } else {

                }
                return
            }
            MY_PERMISSIONS_REQUEST_PHONE_CALL -> {

            }
            else -> return
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.exit) {
            finish()
            android.os.Process.killProcess(android.os.Process.myPid())
        }
        return false
    }

    @SuppressWarnings("MissingPermission")
    private fun phoneCall(phone: String) {
        val intentPhoneCall = Intent(Intent.ACTION_CALL)

        intentPhoneCall.setData(Uri.parse("tel:$phone"))
        startActivity(intentPhoneCall)
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
                            val toast = Toast.makeText(this@MainActivity, "Нет координат устройства!", Toast.LENGTH_LONG)
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                        }
                    }
                })
    }

    private fun makeUrlForLoadDirections(from: String, to: String): String {
        // нужно сделать запрос к google directions и получить координаты маршрута
        var url = "http://maps.googleapis.com/maps/api/directions/json?origin=${from}"
        val dest_addr = "&destination=${to}"
        val sensor = "&sensor=false"
        url = url + dest_addr + sensor
        return url
    }

    private fun makeUrlForLoadDirections(lat_from: Double, lng_from: Double, lat_to: Double, lng_to: Double): String {
        // нужно сделать запрос к google directions и получить координаты маршрута
        var url = "http://maps.googleapis.com/maps/api/directions/json?origin=${lat_from},${lng_from}"
        val dest_addr = "&destination=${lat_to},${lng_to}"
        val sensor = "&sensor=false"
        url = url + dest_addr + sensor
        return url
    }

//    private fun loadDirections(url: String) {
//
//        try {
//            val client = OkHttpClient()
//            val request = Request.Builder().url(url).build()
//            client.newCall(request).enqueue(object : Callback {
//                @Throws(IOException::class)
//                override fun onResponse(call: Call, server_response: Response) {
//
//                    if (server_response.code() == 200) {
//                        // обрабатываем json-ответ сервера
//                        try {
//                            val response = JSONObject(server_response.body()?.string())
//                            val status = response.getString("status")
//                            if (status == "OK") {
//                                val routes = response.getJSONArray("routes")
//                                var obj = routes.getJSONObject(0)
//                                val legs = obj.getJSONArray("legs")
//                                this@MainActivity.coordinatesList = ArrayList<LatLng>()
//                                obj = legs.getJSONObject(0)
//                                val start_location = obj.getJSONObject("start_location")
//                                (this@MainActivity).coordinatesList?.add(LatLng(start_location.getDouble("lat"), start_location.getDouble("lng")))
//                                val steps = obj.getJSONArray("steps")
//                                var end_location: JSONObject
//
//                                for (i in 0..steps.length() - 1) {
//                                    obj = steps.getJSONObject(i)
//                                    end_location = obj.getJSONObject("end_location")
//
//                                    this@MainActivity.coordinatesList?.add(LatLng(end_location.getDouble("lat"), end_location.getDouble("lng")))
//                                }
//                            }
//
//                            runOnUiThread {
//                                showResult()
//                            }
//                        } catch (e: JSONException) {
//                            e.printStackTrace()
//                        }
//
//                    }
//                }
//
//                override fun onFailure(call: Call, e: IOException) {
//                    runOnUiThread {
//                        Toast.makeText(this@MainActivity, "Проблемы подключения", Toast.LENGTH_LONG).show()
//                    }
//                }
//            })
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }

    private fun showResult() {

        //mMap?.getUiSettings()?.setZoomControlsEnabled(true)
        //mMap?.setMapType(GoogleMap.MAP_TYPE_NORMAL)

        val originPoint = coordinatesList!![0]
        val destPoint = coordinatesList!![coordinatesList!!.size - 1]

        val startPosMarkerOptions = MarkerOptions()
        startPosMarkerOptions.position(originPoint).title("")
        //val startPos = mMap?.addMarker(startPosMarkerOptions)
        //startPos?.showInfoWindow()

        val stopPosMarkerOptions = MarkerOptions()
        stopPosMarkerOptions.position(destPoint).title("")
        //val stopPos = mMap?.addMarker(stopPosMarkerOptions)
        //stopPos?.showInfoWindow()

        val cameraPosition = CameraPosition.Builder().target(originPoint).zoom(20f).build()
        //mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        //mMap?.addPolyline(PolylineOptions().addAll(coordinates_).width(5f).color(Color.BLACK))

    }

    private fun submitRequest(route_start_location: Point, route_end_location: Point) {
        val options = DrivingOptions()
        val requestPoints = ArrayList<RequestPoint>()
        requestPoints.add(RequestPoint(
                route_start_location, ArrayList(), RequestPointType.WAYPOINT))
        requestPoints.add(RequestPoint(
                route_end_location, ArrayList(), RequestPointType.WAYPOINT))
        drivingSession = drivingRouter?.requestRoutes(requestPoints, options, drivingRouteListener)
    }

    override fun onObjectUpdated(userLocationView: UserLocationView?, var2: ObjectEvent) {
        getLastLocation {
            mMap?.getMap()?.move(
                    com.yandex.mapkit.map.CameraPosition(Point(driver_lat!!, driver_lng!!), 18.5f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 5f), null)
        }
    }

    override fun onObjectRemoved(p0: UserLocationView?) {

    }


    override fun onObjectAdded(userLocationView: UserLocationView?) {
        userLocationLayer?.setAnchor(
                PointF((mMap!!.width * 0.5).toFloat(), (mMap!!.height * 0.5).toFloat()),
                PointF((mMap!!.width * 0.5).toFloat(), (mMap!!.height * 0.83).toFloat()))
        userLocationView?.getPin()?.setIcon(ImageProvider.fromResource(
                this@MainActivity, R.drawable.user_arrow))
        userLocationView?.getArrow()?.setIcon(ImageProvider.fromResource(
                this@MainActivity, R.drawable.user_arrow))
        userLocationView?.accuracyCircle?.fillColor = Color.BLUE
    }


}
