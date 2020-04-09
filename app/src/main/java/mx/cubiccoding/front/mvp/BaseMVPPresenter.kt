package mx.cubiccoding.front.mvp

import androidx.annotation.CallSuper

abstract class BaseMVPPresenter<E: Any, T : BaseMVPModel> {

    protected lateinit var model: T
    protected lateinit var viewContract: E

    @CallSuper
    open fun init(viewContract: E, model: T) {
        this.viewContract = viewContract
        this.model = model
        model.init()
    }

    @CallSuper
    open fun terminate() {
        model.terminate()
    }
}
