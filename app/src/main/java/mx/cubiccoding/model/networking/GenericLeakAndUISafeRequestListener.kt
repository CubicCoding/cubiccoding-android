package mx.cubiccoding.model.networking

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import mx.cubiccoding.front.utils.isActivityAlive
import mx.cubiccoding.front.utils.isFragmentAlive
import java.lang.ref.WeakReference

/**
 * This class makes sure that the code success and failed
 * code gets executed if and only if the UI is still valid.
 * It also prevents memory leaks by holding only weak references
 * to the UI components...
 *
 */
class GenericLeakAndUISafeRequestListener<V, T, E>(
    view: V,
    private val success: (V, T) -> Unit,
    private val fail: (V, E) -> Unit
) : GenericRequestListener<T, E> {

    private val weakView = WeakReference(view)

    override fun onResult(result: T) {
        val view = weakView.get() ?: return
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (isActivityAlive(view as? Activity) || isFragmentAlive(view as? Fragment)) {
                success(view, result)
            }
        } else {
            Handler(Looper.getMainLooper()).post {
                if (isActivityAlive(view as? Activity) || isFragmentAlive(view as? Fragment)) {
                    success(view, result)
                }
            }
        }
    }

    override fun onFail(e: E) {
        val view = weakView.get() ?: return

        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (isActivityAlive(view as? Activity) || isFragmentAlive(view as? Fragment)) {
                fail(view, e)
            }
        } else {
            Handler(Looper.getMainLooper()).post {
                if (isActivityAlive(view as? Activity) || isFragmentAlive(view as? Fragment)) {
                    fail(view, e)
                }
            }
        }
    }

}
