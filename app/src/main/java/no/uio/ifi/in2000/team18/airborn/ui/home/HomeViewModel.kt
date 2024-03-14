package no.uio.ifi.in2000.team18.airborn.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.team18.airborn.data.AirportDataSource
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val airportDataSource: AirportDataSource
) : ViewModel() {
    data class UiState(
        val airportInput: String = "",
        val airports: List<Airport> = listOf(),
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun filterAirports(input: String) {
        _state.update {
            it.copy(
                airportInput = input,
                airports = airportDataSource.search(input),
            )
        }
    }

    fun selectAirport(airport: String) {
        _state.update {
            it.copy(airportInput = airport)
        }

    }
}