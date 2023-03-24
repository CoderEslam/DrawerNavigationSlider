package com.doubleclick.doctorapp.android.views.slidingrootnav.transform

import android.view.View
import com.doubleclick.doctorapp.android.views.slidingrootnav.util.SideNavUtils

class ScaleTransformation(private val endScale: Float) : RootTransformation {
    override fun transform(dragProgress: Float, rootView: View?) {
        val scale: Float = SideNavUtils.evaluate(
            dragProgress, START_SCALE,
            endScale
        )
        rootView!!.scaleX = scale
        rootView!!.scaleY = scale
    }

    companion object {
        private const val START_SCALE = 1f
    }
}