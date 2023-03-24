package com.doubleclick.doctorapp.android.views.slidingrootnav.transform

import android.os.Build
import android.view.View
import com.doubleclick.doctorapp.android.views.slidingrootnav.util.SideNavUtils

class ElevationTransformation(private val endElevation: Float) : RootTransformation {


    companion object {
        private const val START_ELEVATION = 0f
    }

    override fun transform(dragProgress: Float, rootView: View?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val elevation: Float = SideNavUtils.evaluate(
                dragProgress, START_ELEVATION,
                endElevation
            )
            rootView!!.elevation = elevation
        }
    }
}
