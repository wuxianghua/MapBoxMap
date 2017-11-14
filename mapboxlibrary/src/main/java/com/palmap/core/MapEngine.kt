package com.palmap.core

import android.content.Context
import android.text.TextUtils
import com.mapbox.mapboxsdk.Mapbox
import com.palmap.core.style.FeatureRenderer
import com.palmap.core.style.SimpleFeatureRenderer
import com.palmap.core.style.UniqueFeatureRenderer
import com.palmap.core.style.renderable.Renderable
import com.palmap.core.util.loadFromAsset
import org.json.JSONObject

/**
 * Created by wtm on 2017/8/30.
 */
object












MapEngine {

    private val mapBoxKey = "pk.eyJ1IjoiY2FtbWFjZSIsImEiOiJjaW9vbGtydnQwMDAwdmRrcWlpdDVoM3pjIn0.Oy_gHelWnV12kJxHQWV7XQ"

    private val renderer: HashMap<String, Renderable> = HashMap()

    var stylePath = "mapStyle.json"

    fun start(context: Context, key: String = mapBoxKey) {
        if (TextUtils.isEmpty(key)) {
            throw IllegalArgumentException("appKey can't null !!!")
        }
        Mapbox.getInstance(context, key)

        val styleJson = loadFromAsset(context, stylePath)
        styleJson?.let {
            initRenderer(styleJson)
        }
    }

    private fun initRenderer(json: String) {
        try {
            val jsonObj = JSONObject(json)
            jsonObj.keys().forEach { jsonKey ->
                val mapStyleJson = jsonObj.getJSONObject(jsonKey)
                val renderableBuilder = Renderable.Companion.Builder()
                val rendererTempMap = HashMap<String, FeatureRenderer>()
                mapStyleJson.keys().forEach { layerName ->
                    if ("backgroundColor" == layerName) {
                        renderableBuilder.backgroundColor(mapStyleJson.getString(layerName))
                        return@forEach
                    }
                    if ("backgroundOpacity" == layerName) {
                        renderableBuilder.backgroundOpacity(mapStyleJson.getDouble(layerName).toFloat())
                        return@forEach
                    }
                    //构建renderer
                    val layerJson = mapStyleJson.getJSONObject(layerName)

                    val rendererJson = layerJson.getJSONObject("renderer")

                    val layerType = layerJson.getString("layerType")

                    val rendererType = rendererJson.getString("type")

                    val defaultStyle = rendererJson.getJSONObject("default")

                    val defaultStyleMap = HashMap<String, String>()

                    var haveOutLine = false

                    defaultStyle.keys().forEach { defaultStyleKey ->
                        try {
                            if ("outline" == defaultStyleKey) {
                                val outLineStyle = defaultStyle.getJSONObject(defaultStyleKey)
                                outLineStyle.keys().forEach { outlineKey ->
                                    defaultStyleMap[outlineKey] = outLineStyle.getString(outlineKey)
                                }
                                haveOutLine = true
                            } else {
                                defaultStyleMap[defaultStyleKey] = defaultStyle.getString(defaultStyleKey)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    val featureRenderer = if (rendererType == "simple") {
                        SimpleFeatureRenderer(defaultStyleMap, layerType, haveOutLine)
                    } else { //unique
                        val uniqueKeys = rendererJson.getJSONArray("keys")
                        if (uniqueKeys.length() != 0) {
                            val keysArr = Array<String>(uniqueKeys.length(), {
                                uniqueKeys.getString(it)
                            })
                            val styleMap = HashMap<String, HashMap<String, String>>()
                            val uniqueStyleJson = rendererJson.getJSONObject("style")
                            uniqueStyleJson.keys().forEach { uniqueStyleKey ->
                                val tempJsonObject = uniqueStyleJson.getJSONObject(uniqueStyleKey)
                                val tempMap = HashMap<String, String>()
                                tempJsonObject.keys().forEach { tempKey ->
                                    try {
                                        if ("outline" == tempKey) {
                                            val tempOutLineStyle = tempJsonObject.getJSONObject(tempKey)
                                            tempOutLineStyle.keys().forEach { tempOutLineKey ->
                                                tempMap[tempOutLineKey] = tempOutLineStyle.getString(tempOutLineKey)
                                            }
                                            haveOutLine = true
                                        } else {
                                            tempMap[tempKey] = tempJsonObject.getString(tempKey)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                styleMap[uniqueStyleKey] = tempMap
                            }
                            UniqueFeatureRenderer(defaultStyleMap, keysArr, styleMap, layerType, haveOutLine)
                        } else {
                            SimpleFeatureRenderer(defaultStyleMap, layerType, haveOutLine)
                        }
                    }
                    rendererTempMap[layerName] = featureRenderer
                }
                renderableBuilder.renderer(rendererTempMap)
                renderer[jsonKey] = renderableBuilder.build()
            }
        } catch (e: Exception) {
            renderer.clear()
            e.printStackTrace()
        }
    }

    fun getRenderableByName(name : String):Renderable?{
        return renderer[name]
    }

}