package com.doubleclick.doctorapp.android.views.slidingrootnav.transform

import android.view.View
import com.doubleclick.doctorapp.android.views.slidingrootnav.util.SideNavUtils

class YTranslationTransformation(private val endTranslation: Float) :
    RootTransformation {
    override fun transform(dragProgress: Float, rootView: View?) {
        val translation: Float = SideNavUtils.evaluate(
            dragProgress, START_TRANSLATION,
            endTranslation
        )
        rootView!!.translationY = translation
    }

    companion object {
        private const val START_TRANSLATION = 0f
    }
}