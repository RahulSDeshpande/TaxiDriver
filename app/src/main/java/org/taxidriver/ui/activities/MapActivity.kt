package org.taxidriver.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.SupportMapFragment
import org.taxidriver.R

class MapActivity : AppCompatActivity {

    private val MY_PERMISSIONS_REQUEST_LOCATION = 1
    private val MY_PERMISSIONS_REQUEST_PHONE_CALL = 2

    private var mMap: GoogleMap? = null

    constructor() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val fragmentManager = getFragmentManager()
        // подключение карты
        (fragmentManager.findFragmentById(R.id.map) as? MapFragment)?.getMapAsync {
            mMap = it
            mMap?.getUiSettings()?.setZoomControlsEnabled(true)
            mMap?.setMapType(GoogleMap.MAP_TYPE_NORMAL)
            mMap?.setMyLocationEnabled(true)
            /*map?.setOnMapClickListener (object: GoogleMap.OnMapClickListener{
                override fun onMapClick(position: LatLng) {
                    map?.addMarker(MarkerOptions().position(position))
                }
            })*/
        }
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
        }
    }

    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {

                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mMap?.setMyLocationEnabled(true)
                } else {

                    mMap?.setMyLocationEnabled(false)
                }
                return
            }
            MY_PERMISSIONS_REQUEST_PHONE_CALL -> {

            }
            else -> return
        }
    }
}
