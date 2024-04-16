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
import no.uio.ifi.in2000.team18.airborn.data.repository.WeatherRepository
import no.uio.ifi.in2000.team18.airborn.model.Area
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.model.WeatherDay
import no.uio.ifi.in2000.team18.airborn.model.Webcam
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.TurbulenceMapAndCross
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState.Loading
import no.uio.ifi.in2000.team18.airborn.ui.common.toSuccess
import java.nio.channels.UnresolvedAddressException
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FlightBriefViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val airportRepository: AirportRepository,
    private val weatherRepository: WeatherRepository,
) : ViewModel() {
    private val departureIcao = Icao(savedStateHandle.get<String>("departureIcao")!!)
    private val arrivalIcao =
        savedStateHandle.get<String>("arrivalIcao")?.let { if (it == "null") null else Icao(it) }

    data class AirportUiState(
        val airport: LoadingState<Airport> = Loading,
        val metarTaf: LoadingState<MetarTaf> = Loading,
        val isobaric: LoadingState<IsobaricData> = Loading,
        val turbulence: LoadingState<TurbulenceMapAndCross?> = Loading,
        val weather: LoadingState<List<WeatherDay>> = Loading,
        val webcams: LoadingState<List<Webcam>> = Loading,
    )

    data class UiState(
        val departure: AirportUiState,
        val arrival: AirportUiState?,
        val sigcharts: LoadingState<Map<Area, List<Sigchart>>> = Loading,
    )

    private val _state = MutableStateFlow(
        UiState(
            departure = AirportUiState(),
            arrival = if (arrivalIcao != null) AirportUiState() else null,
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val departure = airportRepository.getByIcao(departureIcao)!!
            val arrival = arrivalIcao?.let { airportRepository.getByIcao(it)!! }
            initAirport(departure) {
                _state.update { state -> state.copy(departure = it(state.departure)) }
            }
            if (arrival != null) initAirport(arrival) { fn ->
                _state.update { state -> state.copy(arrival = state.arrival?.let { fn(it) }) }
            }
        }

        viewModelScope.launch {
            val sigcharts = load { airportRepository.getSigcharts() }
            _state.update { it.copy(sigcharts = sigcharts) }
        }
    }

    private fun initAirport(
        airport: Airport, update: ((AirportUiState) -> AirportUiState) -> Unit
    ) {
        update { it.copy(airport = airport.toSuccess()) }

        viewModelScope.launch {
            val weather = load { weatherRepository.getWeatherDays(airport) }
            update { it.copy(weather = weather) }
        }

        viewModelScope.launch {
            val airportIsobaric = load {
                weatherRepository.getIsobaricData(position = airport.position, LocalDateTime.now())
            }
            update { it.copy(isobaric = airportIsobaric) }
        }

        viewModelScope.launch {
            val airportMetarTaf = load { airportRepository.fetchTafMetar(airport.icao) }
            update { it.copy(metarTaf = airportMetarTaf) }
        }

        viewModelScope.launch {
            val airportTurbulence = load { airportRepository.createTurbulence(airport.icao) }
            update { it.copy(turbulence = airportTurbulence) }
        }

        viewModelScope.launch {
            val webcams = load { airportRepository.fetchWebcamImages(airport) }
            update { it.copy(webcams = webcams) }
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