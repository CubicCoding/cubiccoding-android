package mx.cubiccoding.front.mvp

import androidx.annotation.CallSuper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


abstract class BaseMVPModel: CoroutineScope {

    private lateinit var scopedJob: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + scopedJob

    @CallSuper
    open fun init() {
        scopedJob = Job()
    }

    @CallSuper
    open fun terminate() {
        scopedJob.cancel()
    }

}
