package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.data.repository.AirportRepository
import no.uio.ifi.in2000.team18.airborn.data.repository.WeatherRepository
import no.uio.ifi.in2000.team18.airborn.model.Area
import no.uio.ifi.in2000.team18.airborn.model.OffshoreMap
import no.uio.ifi.in2000.team18.airborn.model.Radar
import no.uio.ifi.in2000.team18.airborn.model.RouteForecast
import no.uio.ifi.in2000.team18.airborn.model.RouteIsobaric
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState.Loading
import no.uio.ifi.in2000.team18.airborn.ui.common.toSuccess
import no.uio.ifi.in2000.team18.airborn.ui.connectivity.ConnectivityObserver
import java.nio.channels.UnresolvedAddressException
import javax.inject.Inject

@HiltViewModel
class FlightBriefViewModel @Inject constructor(
    private val airportRepository: AirportRepository,
    private val weatherRepository: WeatherRepository,
    savedStateHandle: SavedStateHandle,
    private val connectivityObserver: ConnectivityObserver,
) : ViewModel() {

    data class UiState(
        val sigcharts: LoadingState<Map<Area, List<Sigchart>>> = Loading,
        val arrivalAirportInput: String = "",
        val arrivalIcao: Icao?,
        val airports: List<Airport> = listOf(),
        val departureIcao: Icao,
        val offshoreMaps: LoadingState<Map<String, List<OffshoreMap>>> = Loading,
        val geoSatelliteImage: LoadingState<String> = Loading,
        val route: LoadingState<RouteIsobaric> = Loading,
        val radarAnimations: LoadingState<List<Radar>> = Loading,
        val networkStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.Available,
        val routeForecast: LoadingState<List<RouteForecast>> = Loading,
        val isIgaRoute: Boolean = false,
    ) {
        val hasArrival: Boolean get() = arrivalIcao != null
    }

    private val _state = MutableStateFlow(
        UiState(
            departureIcao = Icao(savedStateHandle.get<String>("departureIcao")!!),
            arrivalIcao = savedStateHandle.get<String>("arrivalIcao")
                ?.let { if (it == "null") null else Icao(it) },
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
        viewModelScope.launch {
            connectivityObserver.observe().collect { status ->
                _state.update { it.copy(networkStatus = status) }
            }
        }
        viewModelScope.launch {
            try {
                val departureIcao = _state.value.departureIcao
                val arrivalIcao = _state.value.arrivalIcao
                if (arrivalIcao != null) {
                    _state.update {
                        it.copy(
                            isIgaRoute = airportRepository.isRoute(
                                departureIcao,
                                arrivalIcao
                            )
                        )
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun initSigchart() {
        viewModelScope.launch {
            val sigcharts = load { airportRepository.getSigcharts() }
            _state.update { it.copy(sigcharts = sigcharts) }
        }
    }

    fun initOffshoreMpas() {
        viewModelScope.launch {
            val offshoreMaps = load { airportRepository.getOffshoreMaps() }
            _state.update { it.copy(offshoreMaps = offshoreMaps) }
        }
    }

    fun initGeosatelliteImage() {
        viewModelScope.launch {
            val satelliteImage = load { airportRepository.getGeosatelliteImage() }
            _state.update { it.copy(geoSatelliteImage = satelliteImage) }
        }
    }

    fun initRadarAnimations() {
        viewModelScope.launch {
            val radarAnimations = load { airportRepository.fetchRadarAnimations() }
            _state.update { it.copy(radarAnimations = radarAnimations) }
        }
    }

    fun initRouteIsobaric() {
        viewModelScope.launch {
            if (!_state.value.hasArrival) {
                _state.update { it.copy(route = LoadingState.Error("Choose arrival airport")) }
            } else {
                val departure = airportRepository.getByIcao(state.value.departureIcao)!!
                val arrival = airportRepository.getByIcao(_state.value.arrivalIcao!!)!!

                val data = load { weatherRepository.getRouteIsobaric(departure, arrival) }
                _state.update { it.copy(route = data) }
            }
        }
    }

    fun initRoute() {
        viewModelScope.launch {
            val departure = _state.value.departureIcao
            val arrival =
                _state.value.arrivalIcao!! // This function is only called when user has created brief with an arrival
            val routeForecast = load { airportRepository.fetchRoute(departure, arrival) }
            _state.update {
                it.copy(routeForecast = routeForecast)
            }
        }
    }

    fun filterArrivalAirports(input: String) {
        Log.i("ARRIVAL from filter", input)
        _state.update { it.copy(arrivalAirportInput = input) }
        viewModelScope.launch {
            val result = airportRepository.search(input)
            _state.update { it.copy(airports = result) }
        }
    }

    fun selectArrivalAirport(icao: String) = _state.update {
        it.copy(
            arrivalAirportInput = icao
        )
    }

    fun clearArrivalInput() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    arrivalAirportInput = ""
                )
            }
        }
    }

    private suspend fun <T> load(f: suspend () -> T): LoadingState<T> {
        if (state.value.networkStatus != ConnectivityObserver.Status.Available) {
            return LoadingState.Error(message = "Network Unavailable")
        }
        return try {
            f().toSuccess()
        } catch (e: UnresolvedAddressException) {
            LoadingState.Error(message = "Unresolved Address")
        } catch (e: ConnectTimeoutException) {
            LoadingState.Error("Connection Timed out")
        } catch (e: NoTransformationFoundException) {
            LoadingState.Error("Something went wrong with the api")
        } catch (e: JsonConvertException) {
            LoadingState.Error("Something went wrong with the api")
        } catch (e: Exception) {
            LoadingState.Error(message = "Unknown Error: $e")
        }
    }
}