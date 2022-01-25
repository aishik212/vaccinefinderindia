package com.simpleapps.vaccinefinder


import com.google.gson.annotations.SerializedName

data class VaccineSlotData(
    var sessions: List<Session> = listOf(),
) {
    data class Session(
        var address: String = "",
        @SerializedName("allow_all_age")
        var allowAllAge: Boolean = false,
        @SerializedName("available_capacity")
        var availableCapacity: Int = 0,
        @SerializedName("available_capacity_dose1")
        var availableCapacityDose1: Int = 0,
        @SerializedName("available_capacity_dose2")
        var availableCapacityDose2: Int = 0,
        @SerializedName("block_name")
        var blockName: String = "",
        @SerializedName("center_id")
        var centerId: Int = 0,
        var date: String = "",
        @SerializedName("district_name")
        var districtName: String = "",
        var fee: String = "",
        @SerializedName("fee_type")
        var feeType: String = "",
        var from: String = "",
        var lat: Int = 0,
        var long: Int = 0,
        @SerializedName("min_age_limit")
        var minAgeLimit: Int = 0,
        var name: String = "",
        var pincode: Int = 0,
        @SerializedName("session_id")
        var sessionId: String = "",
        var slots: List<String> = listOf(),
        @SerializedName("state_name")
        var stateName: String = "",
        var to: String = "",
        var vaccine: String = "",
    )
}