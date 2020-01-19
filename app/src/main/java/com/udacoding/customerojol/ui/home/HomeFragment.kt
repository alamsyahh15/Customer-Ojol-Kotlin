package com.udacoding.customerojol.ui.home

import GPSTracker
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udacoding.customerojol.R
import com.udacoding.customerojol.network.NetworkConfig
import com.udacoding.customerojol.network.RequestNotification
import com.udacoding.customerojol.ui.home.model.Booking
import com.udacoding.customerojol.ui.home.model.ResultRoute
import com.udacoding.customerojol.ui.home.model.RoutesItem
import com.udacoding.ojolfirebasekotlin.utils.DirectionMaps
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.ResponseBody
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

class HomeFragment : Fragment(), OnMapReadyCallback {
    var latAwal : Double? = null
    var lonAwal : Double? = null
    var latAkhir : Double? = null
    var lonAkhir : Double? = null
    val key : String = "AIzaSyAWZwhBpTgU2IPSou0T37LZnPR3Y6C6MEI"
    var keyy : String? = null
    var map : GoogleMap? = null
    var jarak : String? = null
    var dialog : Dialog? = null
    private var auth : FirebaseAuth? = null
    var notif : String = " {\n" +
            "     \"body\" : \"Body of Your Notification\",\n" +
            "     \"title\": \"Title of Your Notification\"\n" +
            " },"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        auth = FirebaseAuth.getInstance()

        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapsView.onCreate(savedInstanceState)
        mapsView.getMapAsync(this)
        showPermission()


        keyy?.let { bookingHistoryUser(it) }

        homeAwal.onClick{
            takeLocation(1)
        }

        homeAkhir.onClick {
            takeLocation(2)
        }

        homeButton.onClick {
            if (homeAwal.text.isNotEmpty() && homeAkhir.text.isNotEmpty()){
                insertServer()
            } else {
                Snackbar.make(view,"Home Awal dan Akhir Tidak Boleh Kosong", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun insertServer(){
        val currentDate = Calendar.getInstance().time
        insertRequest(
            currentDate.toString(),
            auth?.uid.toString(),
            homeAwal.text.toString(),
            homeAkhir.text.toString(),
            latAwal ?: 0.0,
            lonAwal ?: 0.0,
            latAkhir ?: 0.0,
            lonAkhir ?: 0.0,
            homePrice.text.toString(),
            jarak.toString()
        )
    }

    fun insertRequest(
        tanggal : String,
        uid : String,
        lokasiAwal : String,
        lokasiAkhir : String,
        latAwal : Double,
        lonAwal : Double,
        latAkhir : Double,
        lonAkhir : Double,
        harga : String,
        jarak : String
    ){
        val booking = Booking()
        booking.tanggal = tanggal
        booking.uid = uid
        booking.latAwal = latAwal
        booking.lonAwal = lonAwal
        booking.latAkhir = latAkhir
        booking.lonAkhir = lonAkhir
        booking.lokasiAwal = lokasiAwal
        booking.lokasiAkhir = lokasiAkhir
        booking.harga = harga
        booking.jarak = jarak
        booking.status = 1
        booking.driver = ""

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Booking")
        keyy = database.reference.push().key
        myRef.child(keyy ?: "").setValue(booking)

        pushNotif(booking)

    }


    private fun pushNotif(booking: Booking){
        val database  = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Driver")
        myRef.addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (myToken in p0.children){
                    val token = myToken.child("token").getValue(String::class.java)
                    Log.d("Tokenku", token.toString())
                    val request = RequestNotification()
                    request.token = token
                    request.sendNotification = booking

                    NetworkConfig.getServiceFcm().sendNotification(request)
                        .enqueue(object : Callback<ResponseBody>{
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.d("network Failed",t.message)
                            }

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                response.body()
                                if(response.isSuccessful){
                                    Log.d("Response", response.message().toString())
                                    Log.d("Response Body", response.body().toString())
                                } else {
                                    Log.d("Gagal", response.message())
                                }
                            }
                        })
                }
            }

        })
    }


    private fun bookingHistoryUser(key : String){
        showDialog(true)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Booking")

        myRef.child(key).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                val booking = p0.getValue(Booking::class.java)

                if(booking?.driver != ""){
                    showDialog(false)
                }
            }

        })
    }



    private fun showDialog(status : Boolean){
        dialog = Dialog(activity!!)
        dialog?.setContentView(R.layout.dialog_waitingdriver)

        if(status){
            dialog?.show()
        } else {
            dialog?.dismiss()
        }
    }


    private fun takeLocation(status: Int){
        try {
            activity?.applicationContext?.let { Places.initialize(it, "AIzaSyAWZwhBpTgU2IPSou0T37LZnPR3Y6C6MEI") }
            val fields = arrayListOf(
                Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS
            )

            val intent = activity?.applicationContext?.let {
                Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(it)
            }
            startActivityForResult(intent,status)
        } catch (e : GooglePlayServicesRepairableException){

        } catch (e: GooglePlayServicesNotAvailableException){

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1){
            if(resultCode == RESULT_OK){
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
                latAwal = place?.latLng?.latitude
                lonAwal = place?.latLng?.longitude

                homeAwal.text = place?.address.toString()
                showMainMarker(latAwal ?: 0.0, lonAwal ?: 0.0, place?.address.toString())
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = data?.let { Autocomplete.getStatusFromIntent(it) }
                // TODO: Handle the error.
                Log.i("locatios", status?.statusMessage)

            } else if(resultCode == Activity.RESULT_CANCELED) {

            }
        } else {
            if(resultCode == RESULT_OK){
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
                latAkhir = place?.latLng?.latitude
                lonAkhir = place?.latLng?.longitude
                homeAkhir.text = place?.address.toString()
                showMarker(latAkhir ?: 0.0, lonAkhir ?: 0.0, place?.address.toString())
                route()
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = data?.let { Autocomplete.getStatusFromIntent(it) }
                // TODO: Handle the error.
                Log.i("locatios", status?.statusCode.toString())

            } else if(resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }


    @SuppressLint("CheckResult")
    fun route(){

        val origin = latAwal.toString() + "," + lonAwal.toString()
        val dest = latAkhir.toString() + "," + lonAkhir.toString()

        NetworkConfig.getService()
            .actionRouter(origin,dest,key)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { t: ResultRoute? ->
                showData(t?.routes)
            }
    }

    private fun showData(route: List<RoutesItem?>?){
        if(route != null){
            val point = route[0]?.overviewPolyline?.points

            jarak = route[0]?.legs?.get(0)?.distance?.text
            val waktu = route[0]?.legs?.get(0)?.duration?.text
            homeTimeDistance.text = jarak + " / " + waktu

            val jarakValue = route[0]?.legs?.get(0)?.distance?.value
            val price = jarakValue?.div(1000)?.times(3500)
            homePrice.text = "Rp. " + price.toString()



            DirectionMaps.gambarRoute(map!!, point!!)
        } else {
            alert{
                title = "Attention Data Kosong"
                message = "Data Route Null"
            }.show()
        }
    }

    private fun showPermission(){
        if (activity?.let { ContextCompat
                .checkSelfPermission(it,android.Manifest.permission.ACCESS_FINE_LOCATION)}
            != PackageManager.PERMISSION_GRANTED){

            if(activity?.let { ActivityCompat
                    .shouldShowRequestPermissionRationale(it,android.Manifest.permission.ACCESS_FINE_LOCATION) }!!){
                showGPS()
            } else {
                requestPermissions(arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),1)
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1){
            showGPS()
        } else {
            showGPS()
        }
    }

    private fun showGPS(){
        val gps = activity?.applicationContext?.let { GPSTracker(it) }
        if (gps?.canGetLocation()!!){
            latAwal = gps.latitude
            lonAwal = gps.longitude
            val name: String = showName(latAwal ?: 0.0, lonAwal ?: 0.0)
            homeAwal.text = name

            val coordinate = LatLng(latAwal ?: 0.0,lonAwal ?: 0.0)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate,16f))

            showMainMarker(latAwal ?: 0.0, lonAwal ?: 0.0, "My Location")
        } else {
            gps.showSettingGps()
        }
    }

    private fun showName(lat: Double , lon: Double) : String{
        var name = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val address = geocoder
                .getFromLocation(lat,lon,1)

            if(address.size > 0){
                val fetchAddress = address[0]
                val strAddress = StringBuilder()
                for (i in 0..fetchAddress.maxAddressLineIndex){
                    name = strAddress
                        .append(fetchAddress.getAddressLine(i))
                        .append("").toString()
                }
            }

        } catch (e: Exception){

        }

        return name
    }


    private fun showMainMarker(lat: Double, lon: Double, address: String) {

        val smallmarker = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context?.resources,R.drawable.ic_pin1),
            80, 120,false)

        val coordinate = LatLng(lat,lon)
        map?.addMarker(MarkerOptions().position(coordinate).title(address).icon(
            BitmapDescriptorFactory.fromBitmap(smallmarker)
        ))
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate,16f))
        map?.moveCamera(CameraUpdateFactory.newLatLng(coordinate))
    }

    private fun showMarker(lat: Double, lon: Double, address: String) {
        val coordinate = LatLng(lat,lon)
        map?.addMarker(MarkerOptions().position(coordinate).title(address))
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate,16f))
        map?.moveCamera(CameraUpdateFactory.newLatLng(coordinate))
    }


    override fun onResume() {
        keyy?.let { bookingHistoryUser(it) }
        mapsView?.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapsView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapsView?.onDestroy()
    }
    override fun onLowMemory() {
        mapsView?.onLowMemory()
        super.onLowMemory()

    }

    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        map?.uiSettings?.isZoomControlsEnabled = true
        map?.getUiSettings()?.setMyLocationButtonEnabled(false)
        //  map?.setMyLocationEnabled(true)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(PackageManager.PERMISSION_GRANTED == activity?.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)){
                showGPS()
            } else {
                requestPermissions(arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),12)
            }
        } else {
            showGPS()
        }

    }

}
