package com.doubleclick.doctorapp.android.views.slidingrootnav.util

object SideNavUtils {
    fun evaluate(fraction: Float, startValue: Float, endValue: Float): Float {
        return startValue + fraction * (endValue - startValue)
    }
}
