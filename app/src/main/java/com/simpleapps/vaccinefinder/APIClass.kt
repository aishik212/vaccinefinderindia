package com.simpleapps.vaccinefinder

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class APIClass {
    companion object {
        enum class WHICHDATA {
            STATES,
            DISTRICTS,
            VACCINATIONS
        }

        fun callAPI(url: String, method: String, states: WHICHDATA, apiCallback: APICallback) {
            Thread {
                val client: OkHttpClient = OkHttpClient().newBuilder()
                    .build()
                val request: Request = Request.Builder()
                    .url(url)
                    .method(method, null)
                    .build()
                val response: Response = client.newCall(request).execute()
                apiCallback.getAPIResult(states, response.body?.string())
            }.start()

        }
    }
}