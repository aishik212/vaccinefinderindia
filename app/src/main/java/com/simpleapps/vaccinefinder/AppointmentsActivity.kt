package com.simpleapps.vaccinefinder

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.simpleapps.vaccinefinder.LoginActivity.Companion.currentUserData
import com.simpleapps.vaccinefinder.databinding.AppointmentLayoutBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.*


class AppointmentsActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var inflate: AppointmentLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflate = AppointmentLayoutBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        val currentUserData = LoginActivity.currentUserData
        val datePicker = inflate.datePicker
        val dateBtn = inflate.dateBtn
        val dateBG = inflate.hideDate
        val dd = inflate.ddTv
        val mm = inflate.mmTv
        val yy = inflate.yyTv
        setDateUI(dateBtn, dateBG, dd, mm, yy, datePicker)
        if (currentUserData != null && currentUserData.type.equals("Patient")) {
            inflate.addBookingBtn.visibility = VISIBLE
            inflate.addBookingBtn.setOnClickListener {
                inflate.bookingView.visibility = VISIBLE
            }
            inflate.add.setOnClickListener {
                val doctorName = inflate.doctorSpinner.selectedItem.toString()
                val doctorDetails = doctorData.get(doctorName)
                val userId = currentUserData.id
                val doctorId = doctorDetails?.id
                val location = inflate.cityTv.editText?.text.toString()
                val date = "${dd.text}/" + mm.text + "/" + yy.text
                Log.d("texts",
                    "onCreate: " + userId + " " + doctorId + " " +
                            location + " " + date + " ")
                val client = OkHttpClient().newBuilder()
                    .build()
                val mediaType = "application/json".toMediaTypeOrNull()
                val s =
                    "{\r\n    \"userid\": \"$userId\",\r\n    \"doctorid\": \"$doctorId\",\r\n    \"location\": \"$location\",\r\n    \"time\":\"$date\"\r\n}"
                val body: RequestBody = s.toRequestBody(mediaType)
                val request: Request = Request.Builder()
                    .url("https://ap-south-1.aws.data.mongodb-api.com/app/application-0-btquy/endpoint/add/appointment?secret=tanmoy")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build()
                Thread {
                    val response = client.newCall(request).execute()
                    if (response.code == 201) {
                        runOnUiThread {
                            Toast.makeText(applicationContext,
                                "Appointment Added",
                                Toast.LENGTH_SHORT)
                                .show()
                            inflate.bookingView.visibility = GONE
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(applicationContext,
                                "Some Error Occured",
                                Toast.LENGTH_SHORT)
                                .show()
                            inflate.bookingView.visibility = GONE
                        }
                    }
                    runOnUiThread {
                        getAppointments()
                    }
                }.start()
            }
        } else {
            inflate.addBookingBtn.visibility = GONE
        }
        getDoctors()
    }

    var doctorList = mutableListOf<String>()
    var doctorData = hashMapOf<String, Users>()
    var doctorDataV2 = hashMapOf<String, Users>()
    private fun getDoctors() {
        val client = OkHttpClient().newBuilder()
            .build()
        val request: Request = Request.Builder()
            .url("https://ap-south-1.aws.data.mongodb-api.com/app/application-0-btquy/endpoint/get/users?secret=tanmoy")
            .build()
        Thread {
            val response: Response = client.newCall(request).execute()
            Log.d("texts", "checkUser: ")
            if (response.isSuccessful) {
                val autoCompleteTextView = inflate.doctorSpinner
                val string1 = response.body?.string()
                Log.d("texts", "getDoctors: $string1")
                val aub = AUB.fromJson(string1.toString())
                aub.iterator().forEach {
                    if (it.type != null && it.type.lowercase() == "doctor") {
                        doctorList.add(it.name.toString())
                        doctorData[it.name.toString()] = it
                        doctorDataV2[it.id] = it
                        runOnUiThread {
                            val adapter =
                                ArrayAdapter(this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    doctorList)
                            autoCompleteTextView.adapter = adapter
                        }
                    }
                }
                Log.d("texts", "getDoctors:" + doctorList)
            }
            getAppointments()
        }.start()
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

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.dd_tv -> inflate.hideDate.visibility = VISIBLE
                R.id.mm_tv -> inflate.hideDate.visibility = VISIBLE
                R.id.yy_tv -> inflate.hideDate.visibility = VISIBLE
            }
        }
    }

    private fun getAppointments() {
        val client = OkHttpClient().newBuilder()
            .build()
        val mediaType: MediaType? = "text/plain".toMediaTypeOrNull()
        val body: RequestBody = "".toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url("https://ap-south-1.aws.data.mongodb-api.com/app/application-0-btquy/endpoint/get/appointment?secret=tanmoy&id=${currentUserData?.id}")
            .build()
        Thread {
            val response: Response = client.newCall(request).execute()
            Log.d("texts", "onCreate: " + response.message)
            val fromJson = Bookings.fromJson(response.body?.string().toString())
            if (fromJson.size > 0) {
                runOnUiThread {
                    inflate.nbl.visibility = GONE
                    inflate.appointmentList.removeAllViews()
                }
                fromJson.iterator().forEach {
                    val textView = TextView(applicationContext)
                    textView.background =
                        ContextCompat.getDrawable(applicationContext, R.drawable.card_bg)
                    var layoutManager = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    val i = 40
                    layoutManager.setMargins(i, i, i, i)
                    textView.layoutParams = layoutManager
                    textView.setPadding(30, 30, 30, 30)
                    textView.textSize = 26F
                    val users = doctorDataV2[it.doctorid]
                    if (currentUserData?.type?.lowercase().equals("patient")) {
                        textView.text =
                            "Dr." + users?.name.toString() + " at " + it.time + " on " + it.location + "\n\n" + "Booked on - ${it.date}"
                    } else {
                        textView.text =
                            "Booking With Mr." + users?.name.toString() + " at " + it.time + " on " + it.location
                    }
                    runOnUiThread {
                        inflate.appointmentList.addView(textView)
                    }
                }
            } else {
                runOnUiThread {
                    inflate.nbl.visibility = VISIBLE
                }
            }
            Log.d("texts", "getAppointments: " + fromJson)
            Log.d("texts", "onCreate: " + response.code)
            Log.d("texts", "onCreate: " + response.body)
        }.start()
    }
}
