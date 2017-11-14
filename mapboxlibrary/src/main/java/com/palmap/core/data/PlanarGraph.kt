package com.palmap.core.data

import android.util.Log
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.services.commons.geojson.BaseFeatureCollection
import com.mapbox.services.commons.geojson.FeatureCollection
import com.mapbox.services.commons.geojson.Polygon
import com.palmap.core.style.FeatureRenderer
import com.palmap.core.style.UniqueFeatureRenderer
import org.json.JSONObject
import java.io.Serializable
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by wtm on 2017/9/7.
 * 地图渲染数据
 */
class PlanarGraph(private val mapData: String,val zoomLevel : Double = 16.0){

    val dataMap: LinkedHashMap<String, FeatureCollection> = LinkedHashMap()

    var dataCorrect: Boolean = false
        private set

    var floorId: Long = 0
        private set
    var mapCenter: LatLng = LatLng(31.23912895960803,121.4965994993567)
    val AUSTRALIA_BOUNDS = LatLngBounds.Builder()
            .include(LatLng(31.23912895960803 - 0.0015, 121.4965994993567 + 0.002))
            .include(LatLng(31.23912895960803 + 0.0025, 121.4965994993567 - 0.002))
            .build()

    private var isResolve:AtomicBoolean = AtomicBoolean(false)

    private var isLoadStyle:AtomicBoolean = AtomicBoolean(false)

    /**
     * 数据是否解析过
     */
    fun isResolve(): Boolean = isResolve.get()

    /**
     * 解析数据 建议放在子现场 避免耗时
     */
    fun resolveData(){
        val oldTime = System.currentTimeMillis();
        if(isResolve.get()){
            return
        }
        isResolve.compareAndSet(false,true)
        try {
            val mapDataJson = JSONObject(mapData)
            //if (mapData)
            mapDataJson.keys().forEach { key ->
                try {
                    val featureCollection = BaseFeatureCollection.fromJson(mapDataJson.getString(key))
                    dataMap[key] = featureCollection
                    if ("Frame" == key) {
                        val feature = featureCollection.features[0]
                        floorId = feature.properties["planar_graph"].asLong
                        val position = (feature.geometry as Polygon).coordinates[0][0]
                        mapCenter = LatLng(31.23912895960803,121.4965994993567)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            dataCorrect = true

            Log.e("PlanarGraph","time: " + (System.currentTimeMillis()-oldTime))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 根据renderer加载style  建议放在子现场 避免耗时
     */
    fun loadStyle(renderers: HashMap<String, FeatureRenderer>) {
        if (!dataCorrect) return
        if(isLoadStyle.get()){
            return
        }
        isLoadStyle.compareAndSet(false,true)
        dataMap.forEach { entry ->
            val layerName = entry.key
            val featureCollection = entry.value

            val render = renderers[layerName] ?: return@forEach
            var textRender: UniqueFeatureRenderer? = null
            if (renderers[layerName + "_Text"] != null) {
                val r = renderers[layerName + "_Text"]!!
                if (r is UniqueFeatureRenderer) {
                    textRender = r
                }
            }
            if (render is UniqueFeatureRenderer) {
                featureCollection.features.forEach { f ->
                    render.renderer(f)
                    textRender?.renderer(f)
                }
            }else{
                if (textRender!=null){
                    featureCollection.features.forEach { f ->
                        textRender?.renderer(f)
                    }
                }
            }
        }
    }

}