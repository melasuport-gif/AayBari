package com.package.aaybari.sync

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonUtil {
    private val gson = Gson()

    fun toJson(map: Map<String, Any?>): String = gson.toJson(map)

    fun fromJsonToMap(json: String): Map<String, Any?> {
        val type = object : TypeToken<Map<String, Any?>>() {}.type
        return gson.fromJson(json, type)
    }
}
