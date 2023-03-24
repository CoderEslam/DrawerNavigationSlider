package com.doubleclick.doctorapp.android.views.slidingrootnav.transform

import android.view.View

interface RootTransformation {
    fun transform(dragProgress: Float, rootView: View?)
}
