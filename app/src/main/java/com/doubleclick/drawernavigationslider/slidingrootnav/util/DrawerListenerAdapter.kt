package com.doubleclick.doctorapp.android.views.slidingrootnav.util

import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import com.doubleclick.doctorapp.android.views.slidingrootnav.callback.DragListener
import com.doubleclick.doctorapp.android.views.slidingrootnav.callback.DragStateListener

class DrawerListenerAdapter(private val adaptee: DrawerListener, private val drawer: View) :
    DragListener, DragStateListener {
    override fun onDrag(progress: Float) {
        adaptee.onDrawerSlide(drawer, progress)
    }

    override fun onDragStart() {
        adaptee.onDrawerStateChanged(DrawerLayout.STATE_DRAGGING)
    }

    override fun onDragEnd(isMenuOpened: Boolean) {
        if (isMenuOpened) {
            adaptee.onDrawerOpened(drawer)
        } else {
            adaptee.onDrawerClosed(drawer)
        }
        adaptee.onDrawerStateChanged(DrawerLayout.STATE_IDLE)
    }
}
