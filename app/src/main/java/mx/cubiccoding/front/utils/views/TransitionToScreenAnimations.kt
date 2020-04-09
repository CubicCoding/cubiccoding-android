package mx.cubiccoding.front.utils.views

import android.os.Handler
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.transition.TransitionSet
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
                         interpolator: Interpolator = AccelerateDecelerateInterpolator(),
                         constraintSet: ConstraintSet = ConstraintSet()) {
        Handler().postDelayed({
            transition.duration = transitionDuration
            transition.interpolator = interpolator
            TransitionManager.beginDelayedTransition(root, transition)
            constraintSet.load(root.context, layoutId)
            constraintSet.applyTo(root)
        }, delayInMS)
    }

}