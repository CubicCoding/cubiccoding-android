package mx.cubiccoding.front

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import mx.cubiccoding.BuildConfig
import timber.log.Timber

class CubicCodingApplication: MultiDexApplication() {
    companion object {
        lateinit var instance: CubicCodingApplication
            private set
        var IS_DEBUG = BuildConfig.DEBUG
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (IS_DEBUG) {//Set clean logs component...
            val timberTree = Timber.DebugTree()
            Timber.plant(timberTree)
        }
    }
}
