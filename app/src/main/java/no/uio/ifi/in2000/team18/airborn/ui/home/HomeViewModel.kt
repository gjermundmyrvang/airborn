package no.uio.ifi.in2000.team18.airborn.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.data.repository.AirportRepository
import no.uio.ifi.in2000.team18.airborn.data.repository.SigmetRepository
import no.uio.ifi.in2000.team18.airborn.model.Sigmet
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Sun
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.toSuccess
import java.nio.channels.UnresolvedAddressException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val airportRepository: AirportRepository,
    private val sigmetRepository: SigmetRepository,
) : ViewModel() {
    data class UiState(
        val departureAirportInput: String = "",
        val arrivalAirportInput: String = "",
        val departureAirportIcao: Icao? = null,
        val arrivalAirportIcao: Icao? = null,
        val searchResults: List<Airport> = listOf(),
        val airports: List<Airport> = listOf(),
        val sigmets: List<Sigmet> = listOf(),
        val sun: LoadingState<Sun?> = LoadingState.Loading
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            initSigmets()
        }

        viewModelScope.launch {
            val airports = airportRepository.all()
            _state.update {
                it.copy(searchResults = airports, airports = airports)
            }
        }
    }

    fun clearDepartureInput() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    departureAirportInput = "",
                    departureAirportIcao = null
                )
            }
        }
    }

    fun clearArrivalInput() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    arrivalAirportInput = "",
                    arrivalAirportIcao = null
                )
            }
        }
    }

    private fun initSigmets() {
        viewModelScope.launch {
            val sigmets = try {
                sigmetRepository.fetchSigmets()
            } catch (e: UnresolvedAddressException) {
                listOf()
            } catch (e: Exception) {
                listOf()
            }
            _state.update {
                it.copy(sigmets = sigmets)
            }
        }
    }

    fun filterDepartureAirports(input: String) {
        _state.update { it.copy(departureAirportInput = input) }
        viewModelScope.launch {
            val result = airportRepository.search(input)
            _state.update { it.copy(searchResults = result) }
        }
    }

    fun filterArrivalAirports(input: String) {
        _state.update { it.copy(arrivalAirportInput = input) }
        viewModelScope.launch {
            val result = airportRepository.search(input)
            _state.update { it.copy(searchResults = result) }
        }
    }

    fun selectDepartureAirport(airport: Icao) = _state.update {
        it.copy(departureAirportInput = airport.code, departureAirportIcao = airport)
    }

    fun selectArrivalAirport(airport: Icao) = _state.update {
        it.copy(
            arrivalAirportInput = airport.code, arrivalAirportIcao
            = airport
        )
    }

    fun updateSunriseAirport(airport: Airport) {
        viewModelScope.launch {
            val sun = load { airportRepository.fetchSunriseSunset(airport) }
            _state.update { it.copy(sun = sun) }
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