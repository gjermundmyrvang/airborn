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
    private val airportDataSource: AirportDataSource,
    private val flightbriefRepository: FlightbriefRepository,
) : ViewModel() {
    data class UiState(
        val departureAirportInput: String = "",
        val arrivalAirportInput: String = "",
        val airports: List<Airport> = listOf(),
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    airports = airportDataSource.airports
                )
            }
        }
    }

    fun filterDepartureAirports(input: String) {
        _state.update {
            it.copy(
                departureAirportInput = input,
                airports = airportDataSource.search(input),
            )
        }
    }

    fun filterArrivalAirports(input: String) {
        _state.update {
            it.copy(
                arrivalAirportInput = input,
                airports = airportDataSource.search(input),
            )
        }
    }

    fun selectDepartureAirport(airport: String) =
        _state.update { it.copy(departureAirportInput = airport) }

    fun selectArrivalAirport(airport: String) =
        _state.update { it.copy(arrivalAirportInput = airport) }


    suspend fun generateFlightbrief(): String = flightbriefRepository.createFlightbrief(
        Icao(state.value.departureAirportInput),
        if (state.value.arrivalAirportInput.isEmpty()) null else Icao(state.value.arrivalAirportInput),
        LocalDateTime.now()
    )
}