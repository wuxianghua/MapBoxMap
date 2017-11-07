package com.palmap.core.style

/**
 * Created by wtm on 2017/9/5.
 */
abstract class FeatureRendererImpl : FeatureRenderer {

    companion object {
        val fillColor = "fillColor"
        val fillOpacity = "fillOpacity"
        val lineColor = "lineColor"
        val lineWidth = "lineWidth"
        val lineOpacity = "lineOpacity"

        val fillExtrusionColor = "fillExtrusionColor"
        val fillExtrusionOpacity = "fillExtrusionOpacity"
        val fillExtrusionHeight = "fillExtrusionHeight"

        val iconUrl = "iconUrl"
        val iconField = "iconField"
        val iconColor = "iconColor"
        val iconAllowOverlap = "iconAllowOverlap"
        val iconHaloBlur = "iconHaloBlur"
        val iconHaloColor = "iconHaloColor"
        val iconOpacity = "iconOpacity"
        val iconSize = "iconSize"
        val iconOffset = "iconOffset"

        val textField = "textField"
        val textSize = "textSize"
        val textColor = "textColor"
        val textAnchor = "textAnchor"
        val textOpacity = "textOpacity"
        val textOffset = "textOffset"
    }

    protected fun getFillColor(styleMap: HashMap<String, String>, defaultValue: String = "#000"): String {
        return getValueFromMap(styleMap, fillColor, defaultValue)
    }

    protected fun getFillOpacity(styleMap: HashMap<String, String>, defaultValue: Float = 1.0f): Float {
        return getValueFromMap(styleMap, fillOpacity, defaultValue)
    }

    protected fun getLineColor(styleMap: HashMap<String, String>, defaultValue: String = "#000"): String {
        return if (styleMap[lineColor] != null) styleMap[lineColor]!! else defaultValue
    }

    protected fun getLineWidth(styleMap: HashMap<String, String>, defaultValue: Float = 1.0f): Float {
        return getValueFromMap(styleMap, lineWidth, defaultValue)
    }

    protected fun getLineOpacity(styleMap: HashMap<String, String>, defaultValue: Float = 1.0f): Float {
        return getValueFromMap(styleMap, lineOpacity, defaultValue)
    }

    protected fun getFillExtrusionColor(styleMap: HashMap<String, String>, defaultValue: String = "#000"): String {
        return getValueFromMap(styleMap, fillExtrusionColor, defaultValue)
    }

    protected fun getFillExtrusionOpacity(styleMap: HashMap<String, String>, defaultValue: Float = 1.0f): Float {
        return getValueFromMap(styleMap, fillExtrusionOpacity, defaultValue)
    }

    protected fun getFillExtrusionHeight(styleMap: HashMap<String, String>, defaultValue: Float = 1.0f): Float {
        return getValueFromMap(styleMap, fillExtrusionHeight, defaultValue)
    }

    protected fun getTextField(styleMap: HashMap<String, String>, defaultValue: String = "display"): String {
        return getValueFromMap(styleMap, textField, defaultValue)
    }

    protected fun getTextColor(styleMap: HashMap<String, String>, defaultValue: String = "#000"): String {
        return getValueFromMap(styleMap, textColor, defaultValue)
    }

    protected fun getTextSize(styleMap: HashMap<String, String>, defaultValue: Float = 12.0f): Float {
        return getValueFromMap(styleMap, textSize, defaultValue)
    }

    protected fun getIconSize(styleMap: HashMap<String, String>, defaultValue: Float = 1.0f): Float {
        return getValueFromMap(styleMap, iconSize, defaultValue)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private fun getValueFromMap(map: HashMap<String, String>, key: String, defaultValue: String): String {
        return if (map[key] != null) map[key]!! else defaultValue
    }

    private fun getValueFromMap(map: HashMap<String, String>, key: String, defaultValue: Float): Float {
        return if (map[key] != null) map[key]!!.toFloat() else defaultValue
    }
}