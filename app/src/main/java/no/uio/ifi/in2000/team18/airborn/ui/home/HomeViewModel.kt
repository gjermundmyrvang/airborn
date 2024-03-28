package no.uio.ifi.in2000.team18.airborn.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.data.AirportDataSource
import no.uio.ifi.in2000.team18.airborn.data.FlightbriefRepository
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val airportDataSource: AirportDataSource,
    val flightbriefRepository: FlightbriefRepository,
) : ViewModel() {
    data class UiState(
        val airportInput: String = "", val airports: List<Airport> = listOf()
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val airports = airportDataSource.airports
            _state.update {
                it.copy(
                    airports = airports
                )
            }
        }
    }

    fun filterAirports(input: String) {
        _state.update {
            it.copy(
                airportInput = input,
                airports = airportDataSource.search(input),
            )
        }
    }

    fun selectAirport(airport: String) = _state.update { it.copy(airportInput = airport) }

    suspend fun generateFlightbrief(): String = flightbriefRepository.createFlightbrief(
        Icao(state.value.airportInput),
        if (state.value.airportInput.isEmpty()) null else Icao(state.value.airportInput),
        LocalDateTime.now()
    )
}