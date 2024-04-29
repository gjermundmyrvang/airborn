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
        val departureAirport: Airport? = null,
        val arrivalAirport: Airport? = null,
        val searchResults: List<Airport> = listOf(),
        val airports: List<Airport> = listOf(),
        val sigmets: List<Sigmet> = listOf(),
        val sun: LoadingState<Sun?> = LoadingState.Loading,
        val showNoSigmetMessage: Boolean = false,
    ) {
        val airportPair
            get() = departureAirport?.let { d ->
                arrivalAirport?.let { Pair(d, it) }
            }
    }

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

    fun switchDepartureArrival() {
        val departureInput = _state.value.departureAirportInput
        val departureAirport = _state.value.departureAirport
        _state.update {
            it.copy(
                departureAirportInput = _state.value.arrivalAirportInput,
                departureAirport = _state.value.arrivalAirport,
                arrivalAirportInput = departureInput,
                arrivalAirport = departureAirport
            )
        }
    }

    fun switchToDeparture() {
        _state.update {
            it.copy(
                departureAirportInput = _state.value.arrivalAirportInput,
                departureAirport = _state.value.arrivalAirport,
                arrivalAirportInput = "",
                arrivalAirport = null
            )
        }
    }

    fun switchToArrival() {
        _state.update {
            it.copy(
                arrivalAirportInput = _state.value.departureAirportInput,
                arrivalAirport = _state.value.departureAirport,
                departureAirportInput = "",
                departureAirport = null
            )
        }
    }

    fun clearDepartureInput() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    departureAirportInput = "", departureAirport = null
                )
            }
            val airports = airportRepository.all()
            _state.update {
                it.copy(searchResults = airports)
            }
        }
    }

    fun clearArrivalInput() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    arrivalAirportInput = "", arrivalAirport = null
                )
            }
            val airports = airportRepository.all()
            _state.update {
                it.copy(searchResults = airports)
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
                val isEmpty = sigmets.isEmpty()
                it.copy(sigmets = sigmets, showNoSigmetMessage = isEmpty)
            }
        }
    }

    fun dismissNoSigmetMessage() {
        _state.update {
            it.copy(showNoSigmetMessage = false)
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

    fun selectDepartureAirport(airport: Airport) = _state.update {
        it.copy(departureAirportInput = airport.icao.code, departureAirport = airport)
    }

    fun selectArrivalAirport(airport: Airport) = _state.update {
        it.copy(
            arrivalAirportInput = airport.icao.code, arrivalAirport = airport
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