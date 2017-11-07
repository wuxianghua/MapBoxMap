package com.palmap.core

/**
 * Created by wtm on 2017/10/9.
 */
interface OverLayer {

    fun getCoordinate(): DoubleArray

    fun getFloorId() : Long

    fun getResource() : Int

}