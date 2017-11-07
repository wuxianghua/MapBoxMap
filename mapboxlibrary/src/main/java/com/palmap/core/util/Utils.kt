package com.palmap.core.util

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


/**
 * Created by wtm on 2017/8/31.
 */
fun loadFromAsset(context: Context, filename: String): String? {
    var input: InputStream? = null
    try {
        val am = context.getAssets()
        input = am.open(filename)
        val reader = InputStreamReader(input, "UTF-8")
        val bufferedReader = BufferedReader(reader)
        return bufferedReader.readText()
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        if (input != null) {
            try {
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
    return null
}