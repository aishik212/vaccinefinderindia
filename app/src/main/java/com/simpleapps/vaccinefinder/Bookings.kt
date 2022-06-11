package com.simpleapps.vaccinefinder


// To parse the JSON, install Klaxon and do:
//
//   val welcome10 = Welcome10.fromJson(jsonString)

import com.beust.klaxon.*

private fun <T> Klaxon.convert(
    k: kotlin.reflect.KClass<*>,
    fromJson: (JsonValue) -> T,
    toJson: (T) -> String,
    isUnion: Boolean = false,
) =
    this.converter(object : Converter {
        @Suppress("UNCHECKED_CAST")
        override fun toJson(value: Any) = toJson(value as T)
        override fun fromJson(jv: JsonValue) = fromJson(jv) as Any
        override fun canConvert(cls: Class<*>) =
            cls == k.java || (isUnion && cls.superclass == k.java)
    })

private val klaxon = Klaxon()
    .convert(JsonObject::class, { it.obj!! }, { it.toJsonString() })

class Bookings(elements: Collection<BookingsItem>) : ArrayList<BookingsItem>(elements) {
    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = Bookings(klaxon.parseArray<BookingsItem>(json)!!)
    }
}

data class BookingsItem(
    @Json(name = "_id")
    val id: String,

    val date: String,
    val userid: String,
    val doctorid: String,
    val time: String,
    val adate: Adate,
    val location: String,
)

typealias Adate = JsonObject
