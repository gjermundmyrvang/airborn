package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.data.FlightBriefRepository
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.FlightBrief
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import javax.inject.Inject

@HiltViewModel
class FlightBriefViewModel @Inject constructor(
    val flightBriefRepository: FlightBriefRepository,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    data class UiState(
        val flightBrief: LoadingState<FlightBrief> = LoadingState.Loading,
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val brief =
                flightBriefRepository.getFlightBriefById(savedStateHandle.get<String>("flightBriefId")!!)
            if (brief != null) {
                _state.update { it.copy(flightBrief = LoadingState.Success(brief)) }
            } else {
                _state.update { it.copy(flightBrief = LoadingState.Error) }
            }
        }
    }
}