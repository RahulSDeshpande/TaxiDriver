package org.taxidriver.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
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
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import org.taxidriver.R
import org.taxidriver.app.App
import org.taxidriver.utils.*
import java.io.IOException
import java.util.ArrayList

class TaximeterActivity : AppCompatActivity, UserLocationObjectListener, DrivingSession.DrivingRouteListener {

    private var text_view_order_cost: TextView? = null
    private var button_menu: Button? = null
    private var button_finish_ride: Button? = null
    private var button_open_navigator: Button? = null
    private var starting_driver_lat: Double? = null
    private var starting_driver_lng: Double? = null
    private var driver_lat: Double? = null
    private var driver_lng: Double? = null
    private var order_id = ""
    private var order_cost = 0
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var isFinished = false
    private val COST_RUB_PER_KILOMETER = 27
    private var mMap: MapView? = null
    private var mapObjects: MapObjectCollection? = null
    private var from = ""
    private var to = ""
    private var drivingSession: DrivingSession? = null
    private var drivingRouter: DrivingRouter? = null
    private var userLocationLayer: UserLocationLayer? = null
    private var to_lat: Double? = null
    private var to_lng: Double? = null


    constructor() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey("e533b745-5bf0-4ea7-b715-d6799f3f9820")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_taximeter)
        super.onCreate(savedInstanceState)

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val intent_from_main = getIntent()

        starting_driver_lat = intent_from_main.getDoubleExtra("driver_lat", 0.0)
        starting_driver_lng = intent_from_main.getDoubleExtra("driver_lng", 0.0)
        from = intent_from_main.getStringExtra("from")
        to = intent_from_main.getStringExtra("to")
        order_id = intent_from_main.getStringExtra("order_id")
        order_cost = intent_from_main.getIntExtra("order_cost", 0)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mMap = findViewById(R.id.map)
        drivingRouter = MapKitFactory.getInstance().createDrivingRouter()
        mapObjects = mMap?.map?.mapObjects?.addCollection()
        text_view_order_cost = findViewById(R.id.order_cost)
        button_menu = findViewById(R.id.button_menu)
        button_finish_ride = findViewById(R.id.button_finish_ride)
        button_open_navigator = findViewById(R.id.button_open_navigator)
        button_menu?.setOnClickListener {}
        button_finish_ride?.setOnClickListener {
            getSharedPreferences(ORDER_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().putInt("process", ORDER_EXECUTED).apply()
            isFinished = true
            if (order_cost < MINIMAL_ORDER_COST) {
                order_cost = MINIMAL_ORDER_COST
            }
            val finishRideJson = JSONObject()
            finishRideJson.put("request", "driver_finished_order")
            finishRideJson.put("order_id", order_id)
            finishRideJson.put("order_cost", order_cost)
            App.websocketClient.sendMessage(finishRideJson.toString())
            val intent_for_finished_order_activity = Intent(this, FinishedOrderActivity::class.java)
            intent_for_finished_order_activity.putExtra("order_cost", order_cost)
            startActivity(intent_for_finished_order_activity)
            finish()
        }
        button_open_navigator?.setOnClickListener {
            try{
                val uri = Uri.parse("yandexnavi://build_route_on_map?lat_to=${to_lat}&lon_to=${to_lng}")
                var intent = Intent(Intent.ACTION_VIEW, uri)
                intent.`package` = "ru.yandex.yandexnavi"

                // Проверяет, установлено ли приложение
                val activities = packageManager.queryIntentActivities(intent, 0)
                val isIntentSafe = activities.size > 0

                if (isIntentSafe){
                    //Запускает Яндекс.Навигатор.
                    startActivity(intent)
                }
                else{
                    // Открывает страницу Яндекс.Навигатора в Google Play.
                    intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("market://details?id=ru.yandex.yandexnavi")
                    startActivity(intent)
                }
            }
            catch (e: NullPointerException){
                Toast.makeText(this, "Не удалось открыть навигатор.\nПопробуйте еще раз.", Toast.LENGTH_SHORT).show()
            }

        }
        /*Thread {
            while (!isFinished) {
                getLastLocation()
                Thread.sleep(2000)
            }
        }.start()*/

        //val geocoder = Geocoder(this)
        Thread {
            val from_address = getFromLocationName(from)
            val to_address = getFromLocationName(to)
            val from_lat = from_address.latitude
            val from_lng = from_address.longitude
            to_lat = to_address.latitude
            to_lng = to_address.longitude
            val fromPoint = Point(from_lat, from_lng)
            val toPoint = Point(to_lat!!, to_lng!!)
            runOnUiThread {
                submitRequest(fromPoint, toPoint)
                button_open_navigator?.callOnClick()
            }
        }.start()

        //Navigation
        userLocationLayer = mMap?.map?.userLocationLayer
        userLocationLayer?.isEnabled = true
        userLocationLayer?.isHeadingEnabled = true
        userLocationLayer?.setObjectListener(this)

    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mMap?.onStart()
    }

    override fun onStop() {
        mMap?.onStop()
        MapKitFactory.getInstance().onStop()
        drivingRouter?.suspend()
        drivingSession?.cancel()

        super.onStop()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun submitRequest(route_start_location: Point, route_end_location: Point) {
        val options = DrivingOptions()
        val requestPoints = ArrayList<RequestPoint>()
        requestPoints.add(RequestPoint(
                route_start_location, ArrayList(), RequestPointType.WAYPOINT))
        requestPoints.add(RequestPoint(
                route_end_location, ArrayList(), RequestPointType.WAYPOINT))
        drivingSession = drivingRouter?.requestRoutes(requestPoints, options, this)
    }

    @SuppressWarnings("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient?.getLastLocation()
                ?.addOnCompleteListener(this@TaximeterActivity, object : OnCompleteListener<Location> {
                    override fun onComplete(task: Task<Location>) {
                        if (task.isSuccessful() && task.getResult() != null) {

                            val mLastLocation = task.getResult()

                            driver_lat = mLastLocation.getLatitude()
                            driver_lng = mLastLocation.getLongitude()
                            val distance = floatArrayOf(0.0f)
                            Location.distanceBetween(starting_driver_lat!!, starting_driver_lng!!, driver_lat!!, driver_lng!!, distance)
                            order_cost = ((distance[0] / METERS_IN_KILOMETER) * COST_RUB_PER_KILOMETER).toInt()
                            runOnUiThread {
                                text_view_order_cost?.setText("${order_cost}руб.")
                            }
                            val order_costJson = JSONObject()
                            order_costJson.put("request", "order_cost")
                            order_costJson.put("order_id", order_id)
                            order_costJson.put("order_cost", order_cost)
                            App.websocketClient.sendMessage(order_costJson.toString())
                        } else {
                            //showMessage("No Location found")
                        }
                    }
                })
    }

    private fun getFromLocationName(place: String): Location {
        val url = "https://geocode-maps.yandex.ru/1.x/?format=json&geocode=$place"
        val location = Location("")
        try {
            val okHttpClient = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            val stringResponse = response.body()?.string()
            val jsonResponse = JSONObject(stringResponse)
            val respJson = jsonResponse.getJSONObject("response")
            val GeoObjectCollection = respJson.getJSONObject("GeoObjectCollection")
            val featureMember = GeoObjectCollection.getJSONArray("featureMember")
            val item = featureMember.getJSONObject(0)
            val GeoObject = item.getJSONObject("GeoObject")
            val point = GeoObject.getJSONObject("Point")
            val pos = point.getString("pos")
            val lng = pos.substring(0, pos.indexOf(" ")).toDouble()
            val lat = pos.substring(pos.indexOf(" ") + 1).toDouble()
            location.latitude = lat
            location.longitude = lng
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException){
            e.printStackTrace()
        }
        return location
    }

    override fun onObjectUpdated(userLocationView: UserLocationView?, p1: ObjectEvent?) {

    }

    override fun onObjectRemoved(p0: UserLocationView?) {

    }

    override fun onObjectAdded(userLocationView: UserLocationView?) {
        userLocationLayer?.setAnchor(
                PointF((mMap!!.width * 0.5).toFloat(), (mMap!!.height * 0.5).toFloat()),
                PointF((mMap!!.width * 0.5).toFloat(), (mMap!!.height * 0.83).toFloat()))
        userLocationView?.pin?.setIcon(ImageProvider.fromResource(
                this, R.drawable.user_arrow))
        userLocationView?.arrow?.setIcon(ImageProvider.fromResource(
                this, R.drawable.user_arrow))
        userLocationView?.accuracyCircle?.fillColor = Color.BLUE
    }

    override fun onDrivingRoutesError(p0: Error?) {
        Log.d("error", p0.toString())
    }

    override fun onDrivingRoutes(routes: List<DrivingRoute>) {
        mapObjects?.addPolyline(routes[0].geometry)
        val fromPoint = routes[0].geometry.points[0]
        val toPoint = mapObjects?.addPlacemark(routes[0].geometry.points.last())
        toPoint?.setIcon(ImageProvider.fromResource(this@TaximeterActivity, R.drawable.ic_location))
        mMap?.map?.move(
                com.yandex.mapkit.map.CameraPosition(fromPoint!!, 18.5f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 5f), null)
    }
}
