package com.palmap.core.style

import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.services.commons.geojson.Feature

/**
 * Created by wtm on 2017/8/31.
 */
interface FeatureRenderer {

    fun renderer(feature: Feature): Feature

    fun createLayer(layerName: String, sourceId: String): Layer

    fun isHaveOutLineLayer(): Boolean

    fun getStyle(key: String): String?

}