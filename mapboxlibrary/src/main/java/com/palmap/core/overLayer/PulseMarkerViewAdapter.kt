package com.palmap.core.overLayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.palmap.mapboxlibrary.R

/**
 * Created by wtm on 2017/10/9.
 */

class PulseMarkerViewAdapter internal constructor(context: Context) : MapboxMap.MarkerViewAdapter<PulseMarkerView>(context) {
    private val inflater: LayoutInflater

    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun getView(marker: PulseMarkerView, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.view_test_marker, parent, false)
            viewHolder = ViewHolder()
            viewHolder.icon = convertView!!.findViewById<View>(R.id.icon) as ImageView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        viewHolder.icon!!.setImageBitmap(marker.icon.bitmap)
        return convertView
    }

    private class ViewHolder {

        var icon: ImageView? = null
    }
}
