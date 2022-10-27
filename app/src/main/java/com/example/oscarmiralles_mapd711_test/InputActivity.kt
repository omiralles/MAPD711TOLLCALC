package com.example.oscarmiralles_mapd711_test

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.content.SharedPreferences
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * MAPD711 Test
 * @Authors
 * Student Name: Oscar Miralles Fernandez
 * Student ID: 301250756
 **/

/**
 * InputActivity to create all de variables needed, the objects and store data.
 * This activity contains the toll calculation and check the user selections.
 */
class InputActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener {
    //Shared preferences to store introduced data by the user
    lateinit var sharedPreferences: SharedPreferences
    //List of toll prices by day-hour, vehicle type and direction
    var lvEB = listOf<Double>(25.29,42.04,47.83,42.04,38.47,43.62,49.56,46.81,25.29,25.29,34.63,25.29)
    var lvWB = listOf<Double>(25.29,44.86,54.93,46.58,39.07,48.61,58.48,43.62,25.29,25.29,34.63,25.29)
    var hvEB = listOf<Double>(50.58,84.08,95.66,84.08,76.94,97.22,116.96,93.62,50.58,50.58,69.26,50.58)
    var hvWB = listOf<Double>(50.58,89.72,109.86,93.16,78.14,87.24,99.12,87.24,50.58,50.58,69.26,50.58)
    var hmvEB = listOf<Double>(75.87,126.12,143.49,126.12,115.41,145.83,175.44,130.86,75.87,75.87,103.89,75.87)
    var hmvWB = listOf<Double>(75.87,134.58,164.79,139.74,117.21,130.86,148.68,140.43,75.87,75.87,103.89,78.87)

    //Override onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.input_activity)

        sharedPreferences = this.getSharedPreferences(
            "com.example.oscarmiralles_mapd711_test",
            Context.MODE_PRIVATE)

        sharedPreferences.edit().putBoolean(
            "transponder",false).apply()

        sharedPreferences.edit().putBoolean(
            "load_online_calc", false).apply()

        val distanceInput = findViewById<EditText>(R.id.distance_edittext)
        val spinnerDayHour = findViewById<Spinner>(R.id.spin_day_hour)

        //Events to store distance EditText value
        distanceInput.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null) {
                    sharedPreferences.edit().putString(
                        "distance",p0.toString()).apply()
                }
            }
            override fun afterTextChanged(p0: Editable?) {
                val inputValue: Double?

                if (p0.toString() != "") {
                   //Check the distance is between 0 and 24
                    inputValue = p0.toString().toDouble()
                    if ((inputValue < 0.0) || (inputValue > 24)) {
                        distanceInput.error = "The value must be between 0 and 24"
                    }
                    else {
                        distanceInput.error = null
                    }
                }

            }
        })

        //Create day-time spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.day_time,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDayHour.adapter = adapter
        spinnerDayHour.onItemSelectedListener = this
    }

    /**
     * Function to store the selected vehicle type
     */
    fun vehicleSelector(view: View) {
        if (view is RadioButton){
            //Saving the data
            sharedPreferences.edit().putString(
                "vehicle_type",view.text.toString()).apply()
        }
    }

    /**
     * Function to store the selected direction
     */
    fun directionSelector(view: View) {
        if (view is RadioButton){
            //Saving the data
            sharedPreferences.edit().putString(
                "direction",view.text.toString()).apply()
        }
    }

    /**
     * Function to store the selected or unselected transponder (Switch type)
     */
    fun transporterSelector(view: View) {
        if (view is Switch){
            //Saving the data
            sharedPreferences.edit().putBoolean(
                    "transponder",view.isChecked).apply()
        }
    }

    /**
     * Function to store the selected or unselected load online Calculator (Checkbox type)
     */
    fun loadOnlineCalcSelector(view: View) {
        if (view is CheckBox){
            //Saving the data
            sharedPreferences.edit().putBoolean(
                    "load_online_calc", view.isChecked
                ).apply()
        }
    }
    /**
     * Function to store the selected spinner time of day
     */
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        sharedPreferences.edit().putString(
            "time_of_day",p0?.getItemAtPosition(p2).toString()).apply()
    }

    /**
     * Error if nothing selected in the spinner
     */
    override fun onNothingSelected(p0: AdapterView<*>?) {
        Toast.makeText(applicationContext, "Nothing selected", Toast.LENGTH_LONG).show()
    }

    /**
     * Function to calculate toll based on input data. Store the toll in
     * shared preferences and call next activity ResultActivity
     */
    fun calculateToll(view: View) {
        //Needed variable to make the checks and calculation
        val lightVehicle = findViewById<RadioButton>(R.id.lightVehicle)
        val heavyVehicle = findViewById<RadioButton>(R.id.heavyVehicle)
        val heavyMultiVehicle = findViewById<RadioButton>(R.id.heavyMulti)
        val distan: Double
        val eastDir = findViewById<RadioButton>(R.id.direction_east)
        val westDir = findViewById<RadioButton>(R.id.direction_west)
        val spinTimeDay = findViewById<Spinner>(R.id.spin_day_hour)
        var timeDayValue:Double? = 0.0
        var tollResult:Double? = 0.0

        //Get the distance introduced
        if (sharedPreferences.getString("distance","") != "") {
            distan = sharedPreferences.getString("distance","")?.toDouble() ?: 0.0
        }
        else {
            distan = 0.0
        }

        //Checks to make the calculation
        if (!lightVehicle.isChecked && !heavyVehicle.isChecked && !heavyMultiVehicle.isChecked) {
            Toast.makeText(this, "Yo have to select a Vehicle Type", Toast.LENGTH_SHORT).show()
        }
        else if (distan == 0.0) {
            Toast.makeText(this, "Yo have to introduce a distance", Toast.LENGTH_SHORT).show()
        }
        else if (!eastDir.isChecked && !westDir.isChecked) {
            Toast.makeText(this, "Yo have to select a direction", Toast.LENGTH_SHORT).show()
        }
        else {
            //Select the correct toll value by vehicle and direction
            if (lightVehicle.isChecked && eastDir.isChecked) {
                timeDayValue = lvEB.elementAt(spinTimeDay.selectedItemPosition)
            }
            if (lightVehicle.isChecked && westDir.isChecked) {
                timeDayValue = lvWB.elementAt(spinTimeDay.selectedItemPosition)
            }
            if (heavyVehicle.isChecked && eastDir.isChecked) {
                timeDayValue = hvEB.elementAt(spinTimeDay.selectedItemPosition)
            }
            if (heavyVehicle.isChecked && westDir.isChecked) {
                timeDayValue = hvWB.elementAt(spinTimeDay.selectedItemPosition)
            }
            if (heavyMultiVehicle.isChecked && eastDir.isChecked) {
                timeDayValue = hmvEB.elementAt(spinTimeDay.selectedItemPosition)
            }
            if (heavyMultiVehicle.isChecked && westDir.isChecked) {
                timeDayValue = hmvWB.elementAt(spinTimeDay.selectedItemPosition)
            }

            //Calculation
            if (timeDayValue != null) {
                tollResult = (sharedPreferences.getString("distance","0.0")?.toDouble()!! * (timeDayValue / 100) + 1)
            }

            //in case that not allowed transponder add camera charges.
            if (tollResult != null) {
                if (sharedPreferences.getBoolean("transponder",false)) {
                    tollResult += 4.2
                }
            }

            //Store the result in shared preferences
            val tollResultStr = String.format("%.2f", tollResult)
            sharedPreferences.edit().putString(
                "toll_result",tollResultStr).apply()

            //Call next activity
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
        }
    }
}