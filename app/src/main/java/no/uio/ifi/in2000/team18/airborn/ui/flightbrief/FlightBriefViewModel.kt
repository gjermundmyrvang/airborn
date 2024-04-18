package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.data.repository.AirportRepository
import no.uio.ifi.in2000.team18.airborn.model.Area
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState.Loading
import no.uio.ifi.in2000.team18.airborn.ui.common.toSuccess
import java.nio.channels.UnresolvedAddressException
import javax.inject.Inject

@HiltViewModel
class FlightBriefViewModel @Inject constructor(
    private val airportRepository: AirportRepository, savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class UiState(
        val sigcharts: LoadingState<Map<Area, List<Sigchart>>> = Loading,
        val hasArrival: Boolean,
        val arrivalAirportInput: String = "",
        val airports: List<Airport> = listOf(),
        val departureIcao: Icao,
    )

    private val _state = MutableStateFlow(
        UiState(
            hasArrival = savedStateHandle.get<String>("arrivalIcao") != "null",
            departureIcao = Icao(savedStateHandle.get<String>("departureIcao")!!)
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    airports = airportRepository.all()
                )
            }
        }
    }

    fun initSigchart() {
        viewModelScope.launch {
            val sigcharts = load { airportRepository.getSigcharts() }
            _state.update { it.copy(sigcharts = sigcharts) }
        }
    }

    fun filterArrivalAirports(input: String) {
        _state.update { it.copy(arrivalAirportInput = input) }
        viewModelScope.launch {
            val result = airportRepository.search(input)
            _state.update { it.copy(airports = result) }
        }
    }

    private suspend fun <T> load(f: suspend () -> T): LoadingState<T> {
        return try {
            f().toSuccess()
        } catch (e: UnresolvedAddressException) {
            LoadingState.Error(message = "Unresolved Address")
        } catch (e: Exception) {
            LoadingState.Error(message = "Unknown Error: $e")
        }
    }
}