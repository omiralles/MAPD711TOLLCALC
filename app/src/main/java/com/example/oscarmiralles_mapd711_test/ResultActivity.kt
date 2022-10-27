package com.example.oscarmiralles_mapd711_test

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebChromeClient
import android.widget.TextView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

/**
 * Activity to show the InputActivity data, the toll calculation result
 * and the map if is necessary
 */
class ResultActivity: AppCompatActivity() {
    //Create variable to retrieve data stored in shared preferences
    lateinit var sharedPreferences: SharedPreferences
    // Create variables to retrieve data from shared preferences
    var vehicleType: String? = null
    var distance: String? = null
    var timeOfDay: String? = null
    var direction: String? = null
    var transponder: String? = null
    var loadOnlineCalc: Boolean? = false
    var resultToll: String? = null

    // Override OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)

        //Retrieve all the data needed
        sharedPreferences = this.getSharedPreferences(
            "com.example.oscarmiralles_mapd711_test",
            Context.MODE_PRIVATE)
        vehicleType = sharedPreferences.getString("vehicle_type","")
        distance = sharedPreferences.getString("distance","")
        timeOfDay = sharedPreferences.getString("time_of_day","")
        direction = sharedPreferences.getString("direction","")
        if (sharedPreferences.getBoolean("transponder",false)) {
            transponder = "Yes"
        } else { transponder = "No" }
        loadOnlineCalc = sharedPreferences.getBoolean("load_online_calc",false)
        resultToll = "$" + sharedPreferences.getString("toll_result","")

        assignData()
    }

    /**
     * Function to assign all the data to the result TextViews
     * Make the WebView visible if the choice is selected
     */
    fun assignData () {
        val vs = findViewById<TextView>(R.id.result_text_vehicle)
        vs.text = vehicleType

        val dist = findViewById<TextView>(R.id.result_text_distance)
        dist.text = distance

        val tod = findViewById<TextView>(R.id.result_text_timeofday)
        tod.text = timeOfDay

        val dir = findViewById<TextView>(R.id.result_text_direction)
        dir.text = direction

        val trans = findViewById<TextView>(R.id.result_text_transponder)
        trans.text = transponder.toString()

        val tResult = findViewById<TextView>(R.id.result_text_toll)
        tResult.text = resultToll

        if (loadOnlineCalc as Boolean) {
            val wv = findViewById<View>(R.id.web_map) as WebView
            wv.visibility = View.VISIBLE
            //Set scale to view the page
            wv.setInitialScale(200)
            //Allow JavaScript in the WebView
            wv.getSettings().setJavaScriptEnabled(true);

            wv.loadUrl("https://www.407etr.com/en/tolls/tolls/toll-calculator.html")
        }
    }
}
