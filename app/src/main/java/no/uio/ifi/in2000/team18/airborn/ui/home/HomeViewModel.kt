package no.uio.ifi.in2000.team18.airborn.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.team18.airborn.data.SigchartDataSource
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    data class UiState(
        val dummy: Unit = Unit
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()
}