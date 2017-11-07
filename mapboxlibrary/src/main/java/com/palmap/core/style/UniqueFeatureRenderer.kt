package com.palmap.core.style

import com.google.gson.JsonPrimitive
import com.mapbox.mapboxsdk.style.functions.Function
import com.mapbox.mapboxsdk.style.functions.stops.IdentityStops
import com.mapbox.mapboxsdk.style.layers.*
import com.mapbox.services.commons.geojson.Feature
import java.util.regex.Pattern

/**
 * Created by wtm on 2017/8/31.
 */
class UniqueFeatureRenderer(
        private val defaultStyle: HashMap<String, String>,
        private val keys: Array<String>,
        private val styleMap: HashMap<String, HashMap<String, String>>,
        private val layerType: String,
        private val haveOutLine: Boolean = false
) : FeatureRendererImpl() {


    override fun getStyle(key: String): String? {
        return this.defaultStyle[key]
    }

    override fun createLayer(layerName: String, sourceId: String): Layer {
        return when (layerType) {
            "fillLayer" -> {
                FillLayer(layerName, sourceId)
                        .withProperties(
                                PropertyFactory.fillColor(Function.property(fillColor, IdentityStops<String>())),
                                PropertyFactory.fillOpacity(Function.property(fillOpacity, IdentityStops<Float>()))
                        )
            }
            "fillExtrusionLayer" -> {
                FillExtrusionLayer(layerName, sourceId)
                        .withProperties(
                                PropertyFactory.fillExtrusionColor(Function.property(fillExtrusionColor, IdentityStops<String>())),
                                PropertyFactory.fillOpacity(Function.property(fillExtrusionOpacity, IdentityStops<Float>())),
                                PropertyFactory.fillExtrusionHeight(Function.property(fillExtrusionHeight, IdentityStops<Float>()))
                                //PropertyFactory.fillExtrusionHeight(60.0f)
                        )
            }
            "symbolLayer" -> {
                SymbolLayer(layerName, sourceId)
                        .withProperties(
                                PropertyFactory.textField(Function.property(if(defaultStyle[textField] == null){
                                    "display"
                                }else{
                                    defaultStyle[textField].toString()
                                }, IdentityStops<String>())),
                                PropertyFactory.textColor(Function.property(textColor, IdentityStops<String>())),
                                PropertyFactory.textSize(Function.property(textSize, IdentityStops<Float>())),
                                PropertyFactory.iconSize(Function.property(iconSize, IdentityStops<Float>())),
                                PropertyFactory.textAnchor(Property.TEXT_JUSTIFY_LEFT),
                                //PropertyFactory.iconOffset(),
                                //PropertyFactory.iconOffset(arrayOf(-8f, 0f)),
                                PropertyFactory.iconImage(Function.property("logo", IdentityStops<String>()))
                        )
            }
            else -> {
                FillLayer(layerName, sourceId)
            }
        }
    }

    override fun isHaveOutLineLayer(): Boolean {
        return haveOutLine
    }

    override fun renderer(feature: Feature): Feature {
        defaultStyle.forEach { mapEntry ->
            val floatVal = mapEntry.value.toFloatOrNull()
            //Log.e("UniqueFeatureRenderer" , mapEntry.key + "=>floatVal ==>" + floatVal)
            if (floatVal == null) {
                feature.addProperty(mapEntry.key, JsonPrimitive(mapEntry.value))
            } else {
                feature.addProperty(mapEntry.key, JsonPrimitive(floatVal))
            }
        }
        keys.forEach keys@{ key->
            val value = feature.properties[key] ?: return@keys
            var styleKey :String? = null
            styleMap.keys.forEach{ styleMapKey->
                if(Pattern.matches(styleMapKey, value.toString())){
                    styleKey = styleMapKey
                    return@forEach
                }
            }
            if (styleKey == null){
                return feature
            }
            val otherStyle = styleMap[styleKey!!]
            otherStyle?.forEach { entry2 ->
                val floatVal2 = entry2.value.toFloatOrNull()
                if (floatVal2 == null) {
                    feature.addProperty(entry2.key, JsonPrimitive(entry2.value))
                } else {
                    feature.addProperty(entry2.key, JsonPrimitive(floatVal2))
                }
            }
            return feature
        }
        return feature
    }

}