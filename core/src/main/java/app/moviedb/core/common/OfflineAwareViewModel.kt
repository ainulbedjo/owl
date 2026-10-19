package app.moviedb.core.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

abstract class OfflineAwareViewModel : ViewModel() {

    private val inFlight = mutableSetOf<Any>()

    private var retryingFromSheet = false

    abstract fun setOfflineSheetVisible(visible: Boolean)

    abstract fun reloadFailedParts()

    fun onTryAgain() {
        retryingFromSheet = true
        reloadFailedParts()
    }

    fun onDismissOfflineSheet() {
        retryingFromSheet = false
        setOfflineSheetVisible(false)
    }

    protected fun loadOnce(key: Any, block: suspend () -> Unit) {
        if (!inFlight.add(key)) return

        viewModelScope.launch {
            try {
                block()
            } finally {
                inFlight.remove(key)
            }
        }
    }

    protected fun handleOfflineSheet(result: AppResult<*>) {
        when {
            result is AppResult.Failure && result.error == AppError.Network -> setOfflineSheetVisible(true)
            result is AppResult.Success && retryingFromSheet -> onDismissOfflineSheet()
        }
    }
}
