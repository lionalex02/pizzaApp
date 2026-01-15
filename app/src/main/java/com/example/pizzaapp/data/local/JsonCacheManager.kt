package com.example.pizzaapp.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File



class JsonCacheManager(private val context: Context) {
    private val gson = Gson()
    private val fileName = "calories_cache.json"
    private var cacheMap: MutableMap<String, Int> = mutableMapOf()

    init {
        loadFromFile()
    }

    fun getCalories(name: String): Int? {
        return cacheMap[name]
    }

    fun saveCalories(name: String, calories: Int) {
        if (calories > 0 && !cacheMap.containsKey(name)) {
            cacheMap[name] = calories
            saveToFile()
        }
    }

    private fun loadFromFile() {
        try {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                val jsonString = file.readText()
                val type = object : TypeToken<Map<String, Int>>() {}.type
                cacheMap = gson.fromJson(jsonString, type) ?: mutableMapOf()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveToFile() {
        try {
            val file = File(context.filesDir, fileName)
            val jsonString = gson.toJson(cacheMap)
            file.writeText(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}