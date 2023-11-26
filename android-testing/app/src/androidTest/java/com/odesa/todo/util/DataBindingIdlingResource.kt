package com.odesa.todo.util

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.IdlingResource
import java.util.UUID

/**
 * There's a lot going on here, but the general idea is that ViewDataBindings are generated
 * whenever you're using data binding layouts. The ViewDataBinding's hasPendingBindings method
 * reports back whether the data binding library need to update the UI to reflect a change in
 * data.
 *
 * The idling resource is considered idle only if there are no pending bindings for any of the
 * ViewDataBindings.
 *
 * Finally, the extension functions DataBindingIdlingResource.monitorFragment and
 * DataBindingIdlingResource.monitorActivity take in FragmentScenario and ActivityScenario,
 * respectively. They then find the underlying activity and associate it with
 * DataBindingIdlingResource, so you can track the layout state. You must call one of these two
 * methods from your tests, otherwise the DataBindingIdlingResource won't know anything about
 * you layout.
 */

class DataBindingIdlingResource : IdlingResource {

    // List of registered callbacks
    private val idlingCallbacks = mutableListOf<IdlingResource.ResourceCallback>()

    // Give it a unique id to work around an Espresso bug where you cannot register/unregister
    // an idling resource with the same name.
    private val id = UUID.randomUUID().toString()

    // Holds whether isIdle was called and the result was false. We track this to avoid calling
    // onTransitionToIdle callbacks if Espresso never thought we wer idle in the first place
    private var wasNotIdle = false

    lateinit var activity: FragmentActivity
    override fun getName() = "DataBinding $id"

    override fun registerIdleTransitionCallback( callback: IdlingResource.ResourceCallback ) {
        idlingCallbacks.add( callback )
    }

    override fun isIdleNow(): Boolean {
        val idle = !getBindings().any { it.hasPendingBindings() }
        @Suppress( "LiftReturnOrAssignment" )
        if ( idle ) {
            if ( wasNotIdle ) {
                // Notify observers to avoid Espresso race detector.
                idlingCallbacks.forEach { it.onTransitionToIdle() }
            }
            wasNotIdle = false
        } else {
            wasNotIdle = true
            // Check next frame.
            activity.findViewById<View>( android.R.id.content ).postDelayed( { isIdleNow },
                16 )
        }
        return idle
    }

    /**
     * Find all binding classes in all currently available fragments.
     */
    private fun getBindings(): List<ViewDataBinding> {
        val fragments = ( activity as? FragmentActivity )
            ?.supportFragmentManager
            ?.fragments
        val bindings = fragments?.mapNotNull {
            it.view?.getBinding()
        } ?: emptyList()
        val childrenBindings = fragments?.flatMap { it.childFragmentManager.fragments }
            ?.mapNotNull { it.view?.getBinding() } ?: emptyList()
        return bindings + childrenBindings
    }
}

private fun View.getBinding(): ViewDataBinding? = DataBindingUtil.getBinding( this )

/**
 * Sets the activity from an [ActivityScenario] to be used from [DataBindingIdlingResource]
 */
fun DataBindingIdlingResource.monitorActivity(
    activityScenario: ActivityScenario<out FragmentActivity>
) {
    activityScenario.onActivity {
        this.activity = it
    }
}

/**
 * Sets the fragment from a [FragmentScenario] to be used from [DataBindingIdlingResource]
 */
fun <T: Fragment> DataBindingIdlingResource.monitorFragment(
    fragmentScenario: FragmentScenario<T> ) {
    fragmentScenario.onFragment {
        this.activity = it.requireActivity()
    }
}