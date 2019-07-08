package com.clearmind.animeland.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.clearmind.animeland.core.di.AppDatabase
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class LocationWorker(var context: Context, params: WorkerParameters) : Worker(context, params) {

    private val DEFAULT_START_TIME = "00:00"
    private val DEFAULT_END_TIME = "23:00"


    companion object {
        const val EXTRA_OUTPUT_LATITUDE = "latitude"
        const val EXTRA_OUTPUT_LONGITUDE = "longitude"
    }

    public val TAG = "MyWorker"

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 60000

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    /**
     * The current location.
     */
    private var mLocation: Location? = null

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Callback for changes in location.
     */
    private var mLocationCallback: LocationCallback? = null


    private var latitude: Double = 0.0
    private var longitude: Double =0.0

    @SuppressLint("MissingPermission")
    override fun doWork(): ListenableWorker.Result {
        Log.d(TAG, "doWork: Done")

        Log.d(TAG, "onStartJob: STARTING JOB..")

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val c = Calendar.getInstance()
        val date = c.time
        val formattedDate = dateFormat.format(date)

        try {
            val currentDate = dateFormat.parse(formattedDate)
            val startDate = dateFormat.parse(DEFAULT_START_TIME)
            val endDate = dateFormat.parse(DEFAULT_END_TIME)

            if (currentDate.after(startDate) && currentDate.before(endDate)) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                mLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        Log.w(TAG, "Location "+locationResult.lastLocation.latitude+" change "+locationResult.lastLocation.longitude)
                    }
                }

                val mLocationRequest = LocationRequest()
                mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

                try {
                    mFusedLocationClient!!.lastLocation.addOnCompleteListener(object : OnCompleteListener<Location> {
                            override fun onComplete(task: Task<Location>) {
                                Log.w(TAG, "onComplete")
                                if (task.isSuccessful() && task.getResult() != null) {
                                    mLocation = task.getResult()
                                    Log.d(TAG, "Location : " + mLocation!!)

                                    Toast.makeText(context, "Location: " + mLocation!!.getLatitude() +" and "+mLocation!!.getLongitude(), Toast.LENGTH_SHORT)
                                        .show()
                                     latitude = mLocation!!.latitude
                                     longitude= mLocation!!.longitude

                                    mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
                                } else {
                                    Log.w(TAG, "Failed to get location.")
                                }
                            }
                        })
                } catch (unlikely: SecurityException) {
                    Log.e(TAG, "Lost location permission.$unlikely")
                }

                try {
                    mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, null)
                } catch (unlikely: SecurityException) {
                    //Utils.setRequestingLocationUpdates(this, false);
                    Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
                }

            } else {
                Log.d(TAG, "Time up to get location. Your time is : $DEFAULT_START_TIME to $DEFAULT_END_TIME")
            }
        } catch (ignored: java.lang.Exception) {
            ignored.printStackTrace()
        }
        val output = Data.Builder()
            .putDouble(EXTRA_OUTPUT_LATITUDE, latitude )
            .putDouble(EXTRA_OUTPUT_LONGITUDE, longitude)
            .build()

        return ListenableWorker.Result.success(output)
    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder()

                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return strAdd
    }
}