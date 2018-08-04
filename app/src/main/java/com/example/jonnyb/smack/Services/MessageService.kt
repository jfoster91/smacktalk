package com.example.jonnyb.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.jonnyb.smack.Model.Channel
import com.example.jonnyb.smack.Utilities.URL_GET_CHANNELS
import org.json.JSONArray
import org.json.JSONException

object MessageService {
    val channels = ArrayList<Channel>()

    fun getChannels(context: Context, complete: (Boolean) -> Unit) {

        val channelsRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener {response ->

            try {

                for (x in 0 until response.length()) {
                    val channel = response.getJSONObject(x)
                    val chanName = channel.getString("name")
                    val chanDesc = channel.getString("description")
                    val chanId = channel.getString("_id")

                    val newChannel = Channel(chanName, chanDesc, chanId)

                    this.channels.add(newChannel)
                }

                complete(true)

            } catch (e: JSONException){
                Log.d("JSON", e.localizedMessage)
                complete(false)

            }


        }, Response.ErrorListener {
                Log.d("ERROR", "Could not retrieve channels")
                complete(false)
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${AuthService.authToken}")
                return headers
            }

        }

        Volley.newRequestQueue(context).add(channelsRequest)
    }

}