package mx.cubiccoding.model.networking

import java.lang.ref.WeakReference

/**
 * This class makes sure that the code success and failed
 * gets executed always as long as the process is live, but
 * it prevents memory leaks by holding only weak references
 * to the UI components...
 *
 */
class GenericLeakSafeRequestListener<V, T, E> (view: V, private val success: (V?, T) -> Unit, private val fail: (V?, E) -> Unit): GenericRequestListener<T, E> {

    private val weakView = WeakReference(view)

    override fun onResult(result: T) {
        val view = weakView.get()
        success(view, result)
    }

    override fun onFail(e: E) {
        val view = weakView.get() ?: return
        fail(view, e)
    }

}
