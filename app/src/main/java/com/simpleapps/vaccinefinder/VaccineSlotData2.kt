package com.simpleapps.vaccinefinder


import com.google.gson.annotations.SerializedName

data class VaccineSlotData2(
    var sessions: List<Session>? = null,
) {
    data class Session(
        var address: String? = null,
        @SerializedName("allow_all_age")
        var allowAllAge: Boolean? = null,
        @SerializedName("available_capacity")
        var availableCapacity: Int? = null,
        @SerializedName("available_capacity_dose1")
        var availableCapacityDose1: Int? = null,
        @SerializedName("available_capacity_dose2")
        var availableCapacityDose2: Int? = null,
        @SerializedName("block_name")
        var blockName: String? = null,
        @SerializedName("center_id")
        var centerId: Int? = null,
        var date: String? = null,
        @SerializedName("district_name")
        var districtName: String? = null,
        var fee: String? = null,
        @SerializedName("fee_type")
        var feeType: String? = null,
        var from: String? = null,
        var lat: Int? = null,
        var long: Int? = null,
        @SerializedName("max_age_limit")
        var maxAgeLimit: Int? = null,
        @SerializedName("min_age_limit")
        var minAgeLimit: Int? = null,
        var name: String? = null,
        var pincode: Int? = null,
        @SerializedName("session_id")
        var sessionId: String? = null,
        var slots: List<Slot?>? = null,
        @SerializedName("state_name")
        var stateName: String? = null,
        var to: String? = null,
        var vaccine: String? = null,
    ) {
        data class Slot(
            var seats: Int? = null,
            var time: String? = null,
        )
    }
}