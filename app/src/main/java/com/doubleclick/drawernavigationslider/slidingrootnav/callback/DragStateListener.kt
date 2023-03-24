package com.doubleclick.doctorapp.android.views.slidingrootnav.callback

interface DragStateListener {
    fun onDragStart()
    fun onDragEnd(isMenuOpened: Boolean)
}