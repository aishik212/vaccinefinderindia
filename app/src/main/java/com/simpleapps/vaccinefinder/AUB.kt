package com.simpleapps.vaccinefinder


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
    .convert(Email::class, { Email.fromJson(it) }, { it.toJson() }, true)
    .convert(Number::class, { Number.fromJson(it) }, { it.toJson() }, true)

class AUB(elements: Collection<Users>) : ArrayList<Users>(elements) {
    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = AUB(klaxon.parseArray(json)!!)
    }
}

data class Users(
    @Json(name = "_id")
    val id: String,

    val name: String? = null,
    val date: String? = null,
    val requestBody: RequestBody? = null,
    val number: Number? = null,
    val gender: String? = null,
    val type: String? = "Patient",
    val email: String? = null,
)

sealed class Email {
    class GenderValue(val value: Gender) : Email()
    class StringValue(val value: String) : Email()

    public fun toJson(): String = klaxon.toJsonString(when (this) {
        is GenderValue -> this.value
        is StringValue -> this.value
    })

    companion object {
        public fun fromJson(jv: JsonValue): Email = when (jv.inside) {
            is JsonObject -> GenderValue(jv.obj?.let { klaxon.parseFromJsonObject<Gender>(it) }!!)
            is String -> StringValue(jv.string!!)
            else -> throw IllegalArgumentException()
        }
    }
}

typealias Gender = JsonObject

sealed class Number {
    class GenderValue(val value: Gender) : Number()
    class IntegerValue(val value: Long) : Number()

    public fun toJson(): String = klaxon.toJsonString(when (this) {
        is GenderValue -> this.value
        is IntegerValue -> this.value
    })

    companion object {
        public fun fromJson(jv: JsonValue): Number = when (jv.inside) {
            is JsonObject -> GenderValue(jv.obj?.let { klaxon.parseFromJsonObject<Gender>(it) }!!)
            is Int, is Long -> IntegerValue((jv.int?.toLong() ?: jv.longValue)!!)
            else -> throw IllegalArgumentException()
        }
    }
}

data class RequestBody(
    val name: String,
)