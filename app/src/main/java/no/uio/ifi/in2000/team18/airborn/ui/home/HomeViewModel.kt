package no.uio.ifi.in2000.team18.airborn.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.data.AirportDataSource
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val airportDataSource: AirportDataSource,
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
        _state.update { it.copy(departureAirportInput = input) }
        viewModelScope.launch {
            val result = airportDataSource.search(input)
            _state.update { it.copy(airports = result) }
        }
    }

    fun filterArrivalAirports(input: String) {
        _state.update { it.copy(arrivalAirportInput = input) }
        viewModelScope.launch {
            val result = airportDataSource.search(input)
            _state.update { it.copy(airports = result) }
        }
    }

    fun selectDepartureAirport(airport: String) =
        _state.update { it.copy(departureAirportInput = airport) }

    fun selectArrivalAirport(airport: String) =
        _state.update { it.copy(arrivalAirportInput = airport) }
}