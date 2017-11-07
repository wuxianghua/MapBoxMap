package com.palmap.core.style.renderable

import com.palmap.core.style.FeatureRenderer

/**
 * Created by wtm on 2017/9/12.
 */
class Renderable {

    var backgroundColor : String = "#fff"
    var backgroundOpacity : Float =  1.0f
    lateinit var renderer :  HashMap<String, FeatureRenderer>

    private constructor()

    private constructor(backgroundColor : String = "#fff",
                        backgroundOpacity : Float = 1.0f,
                        renderer: HashMap<String, FeatureRenderer>
    ) : this(){
        this.backgroundColor = backgroundColor
        this.backgroundOpacity = backgroundOpacity
        this.renderer = renderer
    }

    companion object {

        class Builder{
            private var backgroundColor : String = "#fff"
            private var backgroundOpacity : Float =  1.0f
            private lateinit var renderer :  HashMap<String, FeatureRenderer>

            fun backgroundColor(backgroundColor : String) : Builder{
                this.backgroundColor = backgroundColor
                return this
            }

            fun backgroundOpacity(backgroundOpacity : Float) : Builder{
                this.backgroundOpacity = backgroundOpacity
                return this
            }

            fun renderer(renderer :  HashMap<String, FeatureRenderer>) : Builder{
                this.renderer = renderer
                return this
            }

            fun build() : Renderable {
                return Renderable(this.backgroundColor,this.backgroundOpacity,this.renderer)
            }

        }

    }


}