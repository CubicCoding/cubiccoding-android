package mx.cubiccoding.front.home.scoreboard.actions.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StudentViewModelFactory(private val email: String, private val torunamentId: Int): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(String::class.java, Int::class.java).newInstance(email, torunamentId)
    }
}