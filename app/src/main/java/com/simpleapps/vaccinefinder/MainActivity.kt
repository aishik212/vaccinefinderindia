package com.simpleapps.vaccinefinder

import android.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setMargins
import com.google.gson.Gson
import com.simpleapps.vaccinefinder.databinding.ActivityMainBinding
import com.simpleapps.vaccinefinder.databinding.CenterRowLayoutBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), APICallback, View.OnClickListener {
    lateinit var inflate: ActivityMainBinding
    lateinit var stateSpinner: Spinner
    lateinit var districtSpinner: Spinner
    val states = mutableListOf<String>()
    val districts = mutableListOf<String>()
    val stateIDMap = hashMapOf<String, String>()
    val districtIDMap = hashMapOf<String, String>()
    lateinit var centerLists: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflate = ActivityMainBinding.inflate(layoutInflater)
        val root = inflate.root
        setContentView(root)
        stateSpinner = inflate.stateSpinner
        districtSpinner = inflate.districtSpinner
        centerLists = inflate.centerLists
        val datePicker = inflate.datePicker
        val dateBtn = inflate.dateBtn
        val dateBG = inflate.hideDate
        val dd = inflate.ddTv
        val mm = inflate.mmTv
        val yy = inflate.yyTv
        val srchBtn = inflate.srchBtn

        var start = "https://cdndemo-api.co-vin.in"
        if (!BuildConfig.DEBUG) {
            start = "https://cdn-api.co-vin.in"
        }
        val url = "$start/api/v2/admin/location/states"
        APIClass.callAPI(url, "GET", APIClass.Companion.WHICHDATA.STATES, this)
        setDateUI(dateBtn, dateBG, dd, mm, yy, datePicker)

        srchBtn.setOnClickListener {
            val dName =
                districtSpinner.getItemAtPosition(districtSpinner.selectedItemPosition).toString()
            val districtID = districtIDMap[dName]
            val url =
                "$start/api/v2/appointment/sessions/public/findByDistrict?district_id=$districtID&date=${dd.text}-${mm.text}-${yy.text}"
            APIClass.callAPI(url,
                "GET",
                APIClass.Companion.WHICHDATA.VACCINATIONS,
                this@MainActivity)
        }

        districtSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                srchBtn.performClick()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val toString = stateSpinner.selectedItem.toString()
                val stateID = stateIDMap[toString]
                val url = "$start/api/v2/admin/location/districts/$stateID"
                APIClass.callAPI(url,
                    "GET",
                    APIClass.Companion.WHICHDATA.DISTRICTS,
                    this@MainActivity)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("texts", "onNothingSelected: A")
            }
        }
    }

    private fun setDateUI(
        dateBtn: Button,
        dateBG: ConstraintLayout,
        dd: TextView,
        mm: TextView,
        yy: TextView,
        datePicker: DatePicker,
    ) {
        dd.setOnClickListener(this)
        mm.setOnClickListener(this)
        yy.setOnClickListener(this)
        dateBtn.setOnClickListener {
            dateBG.visibility = VISIBLE
        }
        dateBG.setOnClickListener {
            dateBG.visibility = GONE
        }
        val yourmilliseconds = System.currentTimeMillis()
        val resultdate = Date(yourmilliseconds)
        val dayFormat = SimpleDateFormat("dd")
        val monthFormat = SimpleDateFormat("MM")
        val yearFormat = SimpleDateFormat("yyyy")
        val day = dayFormat.format(resultdate).toInt()
        val month = monthFormat.format(resultdate).toInt()
        val year = yearFormat.format(resultdate).toInt()
        dd.text = day.toString()
        mm.text = month.toString()
        yy.text = year.toString()
        datePicker.init(year, month, day
        ) { _, yr, monthOfYear, dayOfMonth ->
            dateBG.visibility = GONE
            dd.text = dayOfMonth.toString()
            mm.text = monthOfYear.toString()
            yy.text = yr.toString()
        }
    }

    override fun getAPIResult(dataType: APIClass.Companion.WHICHDATA, data: String?) {
        Log.d("texts", "getAPIResult: $dataType")
        if (data != null) {
            when (dataType) {
                APIClass.Companion.WHICHDATA.STATES -> {
                    val json = JSONObject(data)
                    runOnUiThread {
                        states.clear()
                        val jsonArray = JSONArray(json["states"].toString())
                        for (i in 0 until jsonArray.length()) {
                            val element = jsonArray[i]
                            val element1 = JSONObject(element.toString())
                            val element2 = element1.getString("state_name")
                            stateIDMap[element2] = "${element1.get("state_id")}"
                            states.add(element2)
                        }
                        val adapter =
                            ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, states)
                        stateSpinner.adapter = adapter
                    }
                }
                APIClass.Companion.WHICHDATA.DISTRICTS -> {
                    val json = JSONObject(data)
                    runOnUiThread {
                        districts.clear()
                        val jsonArray = JSONArray(json["districts"].toString())
                        for (i in 0 until jsonArray.length()) {
                            val element = jsonArray[i]
                            val element1 = JSONObject(element.toString())
                            val element2 = element1.getString("district_name")
                            districtIDMap[element2] = "${element1.get("district_id")}"
                            districts.add(element2)
                        }
                        val adapter =
                            ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, districts)
                        districtSpinner.adapter = adapter
                        districtSpinner.setSelection(0)
                    }
                }
                APIClass.Companion.WHICHDATA.VACCINATIONS -> {
                    runOnUiThread {
                        centerLists.removeAllViews()
                    }
                    val gson = Gson()
                    val vaccineSlotData = gson.fromJson(data, VaccineSlotData::class.java)
                    Log.d("texts", "getAPIResult: " + vaccineSlotData.sessions.size)
                    runOnUiThread {
                        if (vaccineSlotData.sessions.isEmpty()) {
                            inflate.noCenterTv.visibility = VISIBLE
                        } else {
                            inflate.noCenterTv.visibility = GONE
                        }
                    }
                    vaccineSlotData.sessions.iterator().forEach { session ->
                        runOnUiThread {
                            val centresRow = CenterRowLayoutBinding.inflate(layoutInflater)
                            centresRow.nameTv.text = session.name
                            centresRow.addressTv.text =
                                session.address + "\n" + session.name + "\n" + session.blockName + "\n" + session.pincode
                            centresRow.locationBtn.setOnClickListener {
                                val gmmIntentUri =
                                    Uri.parse("google.navigation:q=" + session.lat + "," + session.long + "&mode=d")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                startActivity(mapIntent)
                            }
                            centresRow.paymentTypeTv.text = session.feeType
                            centresRow.minimumAge.text = "${session.minAgeLimit} Years+"
                            centresRow.vaccineName.text = session.vaccine
                            centresRow.capacityTv.text =
                                "Dose 1 - ${session.availableCapacityDose1}/${session.availableCapacity}\n" +
                                        "Dose 2 - ${session.availableCapacityDose2}/${session.availableCapacity}"

                            centresRow.timeTv.text = "Time - " + session.from + " to " + session.to
                            centresRow.slots.text = "Time Slots\n"
                            session.slots.iterator().forEach {
                                centresRow.slots.append(it + "\n")
                            }
                            val layoutParams =
                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT)
                            layoutParams.setMargins(10)
                            centresRow.root.layoutParams = layoutParams
                            centerLists.addView(centresRow.root)
                        }
                        Log.d("texts", "getAPIResult: $session")
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                com.simpleapps.vaccinefinder.R.id.dd_tv -> inflate.hideDate.visibility = VISIBLE
                com.simpleapps.vaccinefinder.R.id.mm_tv -> inflate.hideDate.visibility = VISIBLE
                com.simpleapps.vaccinefinder.R.id.yy_tv -> inflate.hideDate.visibility = VISIBLE
            }
        }
    }
}