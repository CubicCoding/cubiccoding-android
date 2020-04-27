package mx.cubiccoding.front.utils.views

import android.os.Handler
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.transition.Visibility
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.activity_splash_frame_1.*
import mx.cubiccoding.R

class TransitionToScreenAnimations {

    @MainThread
    fun transitionScreen(root: ConstraintLayout, @LayoutRes layoutId: Int, delayInMS: Long, transitionDuration: Long, transition: TransitionSet = AutoTransition(),
                         interpolator: Interpolator = AccelerateDecelerateInterpolator()) {
        Handler().postDelayed({
            val constraintSet = ConstraintSet()
            transition.duration = transitionDuration
            transition.interpolator = interpolator
            TransitionManager.beginDelayedTransition(root, transition)
            constraintSet.load(root.context, layoutId)
            constraintSet.applyTo(root)
        }, delayInMS)
    }

    fun animateVisibilityOfViews(root: ConstraintLayout, visibility: Int, delayInMS: Long, transitionDuration: Long, transition: TransitionSet = AutoTransition(),
                                 interpolator: Interpolator = AccelerateDecelerateInterpolator(), vararg viewIds: Int) {
        Handler().postDelayed({
            val constraintSet = ConstraintSet()
            transition.duration = transitionDuration
            transition.interpolator = interpolator
            TransitionManager.beginDelayedTransition(root, transition)
            constraintSet.clone(root)
            for (viewId in viewIds) {//Apply the visibility on all the views...
                constraintSet.setVisibility(viewId, visibility)
            }
            constraintSet.applyTo(root)
        }, delayInMS)
    }

}