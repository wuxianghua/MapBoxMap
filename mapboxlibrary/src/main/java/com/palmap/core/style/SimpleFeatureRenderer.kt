package com.palmap.core.style

import com.google.gson.JsonPrimitive
import com.mapbox.mapboxsdk.style.functions.Function
import com.mapbox.mapboxsdk.style.functions.stops.IdentityStops
import com.mapbox.mapboxsdk.style.layers.*
import com.mapbox.services.commons.geojson.Feature

/**
 * Created by wtm on 2017/8/31.
 */
class SimpleFeatureRenderer(
        private val styleMap: HashMap<String, String>,
        private val layerType : String,
        private val haveOutLine: Boolean = false) : FeatureRendererImpl() {

    override fun getStyle(key: String): String? {
        return this.styleMap[key]
    }

    override fun createLayer(layerName: String, sourceId: String): Layer {
        return when (layerType) {
            "fillLayer" -> {
                FillLayer(layerName,sourceId)
                        .withProperties(
                                PropertyFactory.fillColor(getFillColor(styleMap)),
                                PropertyFactory.fillOpacity(getFillOpacity(styleMap))
                        )
            }
            "fillExtrusionLayer" -> {
                FillExtrusionLayer(layerName,sourceId)
                        .withProperties(
                                PropertyFactory.fillExtrusionColor(getFillExtrusionColor(styleMap)),
                                PropertyFactory.fillOpacity(getFillExtrusionOpacity(styleMap)),
                                PropertyFactory.fillExtrusionHeight(getFillExtrusionHeight(styleMap))
                        )
            }
            "symbolLayer" -> {
                SymbolLayer(layerName,sourceId)
                        .withProperties(
                                PropertyFactory.textField("{" + getTextField(styleMap)+ "}"),
                                PropertyFactory.textColor(getTextColor(styleMap)),
                                PropertyFactory.textSize(getTextSize(styleMap)),
                                PropertyFactory.iconSize(getIconSize(styleMap)),
                                PropertyFactory.textAnchor(Property.TEXT_JUSTIFY_LEFT),
                                //PropertyFactory.iconOffset(),
                                //PropertyFactory.iconOffset(arrayOf(-8f, 0f)),
                                PropertyFactory.iconImage(Function.property("logo", IdentityStops<String>()))
                        )
            }
            else -> {
                FillLayer(layerName,sourceId)
            }
        }
    }

    override fun isHaveOutLineLayer(): Boolean {
        return haveOutLine
    }

    override fun renderer(feature: Feature): Feature {
        styleMap.keys.forEach { key ->
            feature.addProperty(key, JsonPrimitive(styleMap[key]))
        }
        return feature
    }

}