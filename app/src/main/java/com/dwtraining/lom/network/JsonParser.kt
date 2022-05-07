package com.dwtraining.lom.network

import com.dwtraining.lom.models.Fugitive
import org.json.JSONArray
import org.json.JSONObject

object JsonParser {
    fun parseJsonToFugitives(jsonString: String) : ArrayList<Fugitive> {
        val arrayFugitives: ArrayList<Fugitive> = arrayListOf()
        val array = JSONArray(jsonString)
        for (index in 0 until array.length()) {
            val name = array.getJSONObject(index).optString("name", "")
            arrayFugitives.add(Fugitive(0, name, 0))
        }
        return arrayFugitives
    }
    fun parseMessageFromAPI(jsonString: String) = JSONObject(jsonString).optString("mensaje", "")
}