package com.simpleapps.vaccinefinder

interface APICallback {
    fun getAPIResult(WHICHDATA: APIClass.Companion.WHICHDATA, data: String?)
}