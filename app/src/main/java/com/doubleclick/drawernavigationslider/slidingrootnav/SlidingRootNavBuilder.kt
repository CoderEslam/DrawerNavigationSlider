package com.doubleclick.doctorapp.android.views.slidingrootnav

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import com.doubleclick.doctorapp.android.views.slidingrootnav.callback.DragListener
import com.doubleclick.doctorapp.android.views.slidingrootnav.callback.DragStateListener
import com.doubleclick.doctorapp.android.views.slidingrootnav.transform.*
import com.doubleclick.doctorapp.android.views.slidingrootnav.util.ActionBarToggleAdapter
import com.doubleclick.doctorapp.android.views.slidingrootnav.util.DrawerListenerAdapter
import com.doubleclick.doctorapp.android.views.slidingrootnav.util.HiddenMenuClickConsumer
import com.doubleclick.drawernavigationslider.R
import java.util.*

open class SlidingRootNavBuilder(private val activity: Activity) {
    private var contentView: ViewGroup? = null
    private var menuView: View? = null
    private var menuLayoutRes = 0
    private val transformations: MutableList<RootTransformation>
    private val dragListeners: MutableList<DragListener>
    private val dragStateListeners: MutableList<DragStateListener>
    private var dragDistance: Int
    private var toolbar: Toolbar? = null
    private var gravity: SlideGravity
    private var isMenuOpened = false
    private var isMenuLocked = false
    private var isContentClickableWhenMenuOpened: Boolean
    private var savedState: Bundle? = null

    init {
        transformations = ArrayList<RootTransformation>()
        dragListeners = ArrayList<DragListener>()
        dragStateListeners = ArrayList<DragStateListener>()
        gravity = SlideGravity.LEFT
        dragDistance = dpToPx(DEFAULT_DRAG_DIST_DP)
        isContentClickableWhenMenuOpened = true
    }

    fun withMenuView(view: View?): SlidingRootNavBuilder {
        menuView = view
        return this
    }

    fun withMenuLayout(@LayoutRes layout: Int): SlidingRootNavBuilder {
        menuLayoutRes = layout
        return this
    }

    fun withToolbarMenuToggle(tb: Toolbar?): SlidingRootNavBuilder {
        toolbar = tb
        return this
    }

    fun withGravity(g: SlideGravity): SlidingRootNavBuilder {
        gravity = g
        return this
    }

    fun withContentView(cv: ViewGroup?): SlidingRootNavBuilder {
        contentView = cv
        return this
    }

    fun withMenuLocked(locked: Boolean): SlidingRootNavBuilder {
        isMenuLocked = locked
        return this
    }

    fun withSavedState(state: Bundle?): SlidingRootNavBuilder {
        savedState = state
        return this
    }

    fun withMenuOpened(opened: Boolean): SlidingRootNavBuilder {
        isMenuOpened = opened
        return this
    }

    fun withContentClickableWhenMenuOpened(clickable: Boolean): SlidingRootNavBuilder {
        isContentClickableWhenMenuOpened = clickable
        return this
    }

    fun withDragDistance(dp: Int): SlidingRootNavBuilder {
        return withDragDistancePx(dpToPx(dp))
    }

    fun withDragDistancePx(px: Int): SlidingRootNavBuilder {
        dragDistance = px
        return this
    }

    fun withRootViewScale(@FloatRange(from = 0.01) scale: Float): SlidingRootNavBuilder {
        transformations.add(ScaleTransformation(scale))
        return this
    }

    fun withRootViewElevation(@IntRange(from = 0) elevation: Int): SlidingRootNavBuilder {
        return withRootViewElevationPx(dpToPx(elevation))
    }

    fun withRootViewElevationPx(@IntRange(from = 0) elevation: Int): SlidingRootNavBuilder {
        transformations.add(ElevationTransformation(elevation.toFloat()))
        return this
    }

    fun withRootViewYTranslation(translation: Int): SlidingRootNavBuilder {
        return withRootViewYTranslationPx(dpToPx(translation))
    }

    fun withRootViewYTranslationPx(translation: Int): SlidingRootNavBuilder {
        transformations.add(YTranslationTransformation(translation.toFloat()))
        return this
    }

    fun addRootTransformation(transformation: RootTransformation): SlidingRootNavBuilder {
        transformations.add(transformation)
        return this
    }

    fun addDragListener(dragListener: DragListener): SlidingRootNavBuilder {
        dragListeners.add(dragListener)
        return this
    }

    fun addDragStateListener(dragStateListener: DragStateListener): SlidingRootNavBuilder {
        dragStateListeners.add(dragStateListener)
        return this
    }

    fun inject(): SlidingRootNav {
        val contentView = getContentView()
        val oldRoot = contentView!!.getChildAt(0)
        contentView.removeAllViews()
        val newRoot = createAndInitNewRoot(oldRoot)
        val menu = getMenuViewFor(newRoot)
        initToolbarMenuVisibilityToggle(newRoot, menu)
        val clickConsumer = HiddenMenuClickConsumer(activity)
        clickConsumer.setMenuHost(newRoot)
        newRoot.addView(menu)
        newRoot.addView(clickConsumer)
        newRoot.addView(oldRoot)
        contentView.addView(newRoot)
        if (savedState == null) {
            if (isMenuOpened) {
                newRoot.openMenu(false)
            }
        }
        newRoot.setMenuLocked(isMenuLocked)
        return newRoot
    }

    private fun createAndInitNewRoot(oldRoot: View): SlidingRootNavLayout {
        val newRoot = SlidingRootNavLayout(activity)
        newRoot.id = R.id.srn_root_layout
        newRoot.setRootTransformation(createCompositeTransformation())
        newRoot.setMaxDragDistance(dragDistance)
        newRoot.setGravity(gravity)
        newRoot.setRootView(oldRoot)
        newRoot.setContentClickableWhenMenuOpened(isContentClickableWhenMenuOpened)
        for (l in dragListeners) {
            newRoot.addDragListener(l)
        }
        for (l in dragStateListeners) {
            newRoot.addDragStateListener(l)
        }
        return newRoot
    }

    private fun getContentView(): ViewGroup? {
        if (contentView == null) {
            contentView = activity.findViewById<View>(android.R.id.content) as ViewGroup
        }
        check(contentView!!.childCount == 1) { activity.getString(R.string.srn_ex_bad_content_view) }
        return contentView
    }

    private fun getMenuViewFor(parent: SlidingRootNavLayout): View? {
        if (menuView == null) {
            check(menuLayoutRes != 0) { activity.getString(R.string.srn_ex_no_menu_view) }
            menuView = LayoutInflater.from(activity).inflate(menuLayoutRes, parent, false)
        }
        return menuView
    }

    private fun createCompositeTransformation(): RootTransformation {
        return if (transformations.isEmpty()) {
            CompositeTransformation(
                listOf(
                    ScaleTransformation(DEFAULT_END_SCALE),
                    ElevationTransformation(dpToPx(DEFAULT_END_ELEVATION_DP).toFloat())
                )
            )
        } else {
            CompositeTransformation(transformations)
        }
    }

    fun initToolbarMenuVisibilityToggle(sideNav: SlidingRootNavLayout, drawer: View?) {
        if (toolbar != null) {
            val dlAdapter = ActionBarToggleAdapter(activity)
            dlAdapter.setAdaptee(sideNav)
            val toggle = ActionBarDrawerToggle(
                activity, dlAdapter, toolbar,
                R.string.srn_drawer_open,
                R.string.srn_drawer_close
            )
            toggle.syncState()
            val listenerAdapter = DrawerListenerAdapter(toggle, drawer!!)
            sideNav.addDragListener(listenerAdapter)
            sideNav.addDragStateListener(listenerAdapter)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return Math.round(activity.resources.displayMetrics.density * dp)
    }

    companion object {
        private const val DEFAULT_END_SCALE = 0.65f
        private const val DEFAULT_END_ELEVATION_DP = 8
        private const val DEFAULT_DRAG_DIST_DP = 180
    }
}
