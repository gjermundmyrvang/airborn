package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

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
import no.uio.ifi.in2000.team18.airborn.model.Turbulence
import no.uio.ifi.in2000.team18.airborn.model.WeatherDay
import no.uio.ifi.in2000.team18.airborn.model.Webcam
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Sun
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState.Loading
import no.uio.ifi.in2000.team18.airborn.ui.common.toSuccess
import no.uio.ifi.in2000.team18.airborn.ui.connectivity.ConnectivityObserver
import java.net.SocketException
import java.nio.channels.UnresolvedAddressException
import javax.inject.Inject


sealed class AirportTabViewModel(
    val savedStateHandle: SavedStateHandle,
    val airportRepository: AirportRepository,
    val weatherRepository: WeatherRepository,
    private val connectivityObserver: ConnectivityObserver,
) : ViewModel() {
    private val _state = MutableStateFlow(AirportUiState())
    val state = _state.asStateFlow()
    abstract val icao: Icao

    data class AirportUiState(
        val airport: LoadingState<Airport> = Loading,
        val metarTaf: LoadingState<MetarTaf> = Loading,
        val isobaric: LoadingState<IsobaricData> = Loading,
        val turbulence: LoadingState<Map<String, List<Turbulence>>> = Loading,
        val webcams: LoadingState<List<Webcam>> = Loading,
        val weather: LoadingState<List<WeatherDay>> = Loading,
        val sun: LoadingState<Sun?> = Loading,
        val networkStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.Available,
        val hasTurbulence: Boolean = false,
        val nearbyAirportsWithMetar: LoadingState<List<Airport>> = Loading,
    )

    init {
        viewModelScope.launch {
            val airport = load { airportRepository.getByIcao(icao) }
            _state.update { it.copy(airport = airport.map { it!! }) }

            airportRepository.getByIcao(icao)?.let {
                val sun = load { airportRepository.fetchSunriseSunset(it) }
                _state.update { it.copy(sun = sun) }
            }
        }
        viewModelScope.launch {
            connectivityObserver.observe().collect { status ->
                _state.update { it.copy(networkStatus = status) }
            }
        }
        _state.update { it.copy(hasTurbulence = airportRepository.hasTurbulence(icao)) }
    }

    fun initMetarTaf() {
        viewModelScope.launch {
            val metarTaf = load { airportRepository.fetchTafMetar(icao) }
            _state.update { it.copy(metarTaf = metarTaf) }
        }
    }

    fun initNewMetar(icao: Icao) {
        viewModelScope.launch {
            val metarTaf = load { airportRepository.fetchTafMetar(icao) }
            _state.update { it.copy(metarTaf = metarTaf) }
        }
    }

    fun initNearby() {
        viewModelScope.launch {
            val airport = airportRepository.getByIcao(icao)
            airport?.let {
                val nearbyAirportsWithMetar =
                    load { airportRepository.getNearbyAirportsWithMetar(it) }
                _state.update { it.copy(nearbyAirportsWithMetar = nearbyAirportsWithMetar) }
            }
        }
    }

    fun initIsobaric() {
        viewModelScope.launch {
            val airport = airportRepository.getByIcao(icao)
            if (airport == null) {
                _state.update { it.copy(isobaric = LoadingState.Error("Failed to get airport")) }
                return@launch
            }
            val isobaric = load { weatherRepository.getIsobaricData(airport.position) }
            _state.update { it.copy(isobaric = isobaric) }
        }
    }


    fun initTurbulence() {
        viewModelScope.launch {
            val turbulence = load { airportRepository.fetchTurbulence(icao) }
            _state.update { it.copy(turbulence = turbulence) }
        }
    }

    fun initWebcam() {
        viewModelScope.launch {
            val airport = airportRepository.getByIcao(icao)
            if (airport == null) {
                _state.update { it.copy(isobaric = LoadingState.Error("Failed to get airport")) }
                return@launch
            }
            val webcams = load { airportRepository.fetchWebcamImages(airport) }
            _state.update { it.copy(webcams = webcams) }
        }
    }

    fun initWeather() {
        viewModelScope.launch {
            val airport = airportRepository.getByIcao(icao)
            if (airport == null) {
                _state.update { it.copy(isobaric = LoadingState.Error("Failed to get airport")) }
                return@launch
            }
            val weather = load { weatherRepository.getWeatherDays(airport) }
            _state.update { it.copy(weather = weather) }
        }
    }

    fun clearAllCache() {
        clearAirportCache()
        clearWeatherCache()
    }

    fun clearAirportCache() {
        airportRepository.clearCache()
    }

    fun clearWeatherCache() {
        weatherRepository.clearWeatherCache()
    }


    @HiltViewModel
    class DepartureViewModel @Inject constructor(
        savedStateHandle: SavedStateHandle,
        airportRepository: AirportRepository,
        weatherRepository: WeatherRepository,
        connectivityObserver: ConnectivityObserver,
    ) : AirportTabViewModel(
        savedStateHandle = savedStateHandle,
        airportRepository = airportRepository,
        weatherRepository = weatherRepository,
        connectivityObserver = connectivityObserver,
    ) {
        override val icao: Icao
            // For some reason this has to be a property. Otherwise something doesn't work.
            // Don't ask why.
            get() = Icao(savedStateHandle.get<String>("departureIcao")!!)

    }

    @HiltViewModel
    class ArrivalViewModel @Inject constructor(
        savedStateHandle: SavedStateHandle,
        airportRepository: AirportRepository,
        weatherRepository: WeatherRepository,
        connectivityObserver: ConnectivityObserver,
    ) : AirportTabViewModel(
        savedStateHandle = savedStateHandle,
        airportRepository = airportRepository,
        weatherRepository = weatherRepository,
        connectivityObserver = connectivityObserver,
    ) {
        override val icao: Icao
            // For some reason this has to be a property. Otherwise something doesn't work.
            // Don't ask why.
            get() = Icao(savedStateHandle.get<String>("arrivalIcao")!!)
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
        } catch (e: SocketException) {
            LoadingState.Error("Failed to connect to api")
        } catch (e: NoTransformationFoundException) {
            LoadingState.Error("Something went wrong with the api")
        } catch (e: JsonConvertException) {
            LoadingState.Error("Something went wrong with the api")
        } catch (e: Exception) {
            LoadingState.Error(message = "Unknown Error: $e")
        }
    }
}