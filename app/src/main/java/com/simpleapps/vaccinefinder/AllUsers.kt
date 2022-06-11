package com.simpleapps.vaccinefinder

import com.google.gson.annotations.SerializedName


class AllUsers : ArrayList<AllUsers.AllUsersItem?>() {
    data class AllUsersItem(
        @SerializedName("_id") var Id: String? = null,
        @SerializedName("date") var date: String? = null,
        @SerializedName("name") var name: String? = null,
//        @SerializedName("gender") var gender: Gender? = Gender(),
        @SerializedName("type") var type: String? = null,
        @SerializedName("email") var email: String? = null,
//        @SerializedName("number") var number: Number? = Number()
    )

}