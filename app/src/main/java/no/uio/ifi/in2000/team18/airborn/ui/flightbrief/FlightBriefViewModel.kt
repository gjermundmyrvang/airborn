package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.data.FlightbriefRepository
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Flightbrief
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import javax.inject.Inject

@HiltViewModel
class FlightBriefViewModel @Inject constructor(
    val flightbriefRepository: FlightbriefRepository,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    data class UiState(
        val flightbrief: LoadingState<Flightbrief> = LoadingState.Loading,
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val brief =
                flightbriefRepository.getFlightbriefById(savedStateHandle.get<String>("flightbriefId")!!)

        }
    }
}