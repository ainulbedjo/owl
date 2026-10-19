package app.moviedb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.moviedb.core.data.repository.ThemeRepository
import app.moviedb.core.model.ThemeSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val themes: ThemeRepository,
) : ViewModel() {

    val theme: StateFlow<ThemeSettings?> = themes.settings
        .stateIn<ThemeSettings?>(viewModelScope, SharingStarted.Eagerly, null)

    fun onThemeChange(settings: ThemeSettings) {
        viewModelScope.launch { themes.update(settings) }
    }
}
