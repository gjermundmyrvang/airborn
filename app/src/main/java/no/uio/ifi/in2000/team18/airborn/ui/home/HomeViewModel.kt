package no.uio.ifi.in2000.team18.airborn.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
        val departureAirportInput: String = "",
        val departureAirports: List<Airport> = listOf(),
        val arrivalAirportInput: String = "",
        val arrivalAirports: List<Airport> = listOf(),
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun filterDepartureAirports(input: String) {
        _state.update {
            it.copy(
                departureAirportInput = input,
                departureAirports = airportDataSource.search(input),
            )
        }
    }

    fun filterArrivalAirports(input: String) {
        _state.update {
            it.copy(
                arrivalAirportInput = input,
                arrivalAirports = airportDataSource.search(input),
            )
        }
    }

    fun selectDepartureAirport(airport: String) =
        _state.update { it.copy(departureAirportInput = airport) }

    fun selectArrivalAirport(airport: String) =
        _state.update { it.copy(arrivalAirportInput = airport) }


    suspend fun generateFlightbrief(): String =
        flightbriefRepository.createFlightbrief(
            Icao(state.value.departureAirportInput),
            if (state.value.arrivalAirportInput.isEmpty()) null else Icao(state.value.arrivalAirportInput),
            LocalDateTime.now()
        )
}