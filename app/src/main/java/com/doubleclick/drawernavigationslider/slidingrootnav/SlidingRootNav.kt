package com.doubleclick.doctorapp.android.views.slidingrootnav

interface SlidingRootNav {
    fun isMenuClosed(): Boolean

    fun isMenuOpened(): Boolean

    fun isMenuLocked(): Boolean

    fun closeMenu()
    fun closeMenu(animated: Boolean)
    fun openMenu()
    fun openMenu(animated: Boolean)
    val layout: SlidingRootNavLayout?
}
