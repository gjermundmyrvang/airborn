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
import no.uio.ifi.in2000.team18.airborn.model.Area
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.Distance
import no.uio.ifi.in2000.team18.airborn.model.OffshoreMap
import no.uio.ifi.in2000.team18.airborn.model.Position
import no.uio.ifi.in2000.team18.airborn.model.Pressure
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.model.Speed
import no.uio.ifi.in2000.team18.airborn.model.Temperature
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricLayer
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState.Loading
import no.uio.ifi.in2000.team18.airborn.ui.common.toSuccess
import java.nio.channels.UnresolvedAddressException
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FlightBriefViewModel @Inject constructor(
    private val airportRepository: AirportRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    data class UiState(
        val sigcharts: LoadingState<Map<Area, List<Sigchart>>> = Loading,
        val hasArrival: Boolean,
        val arrivalAirportInput: String = "",
        val airports: List<Airport> = listOf(),
        val departureIcao: Icao,
        val offshoreMaps: LoadingState<Map<String, List<OffshoreMap>>> = Loading,
        val geoSatelliteImage: LoadingState<String> = Loading,
        val route: LoadingState<IsobaricData> = Loading,
    )

    private val _state = MutableStateFlow(
        UiState(
            hasArrival = savedStateHandle.get<String>("arrivalIcao") != "null",
            departureIcao = Icao(savedStateHandle.get<String>("departureIcao")!!)
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

    fun initRouteIsobaric() {
        viewModelScope.launch {
            val dummyData = IsobaricData(
                Position(
                    latitude = 62.56, longitude = 6.11,
                ), LocalDateTime.parse("2024-04-22T09:58:16.568610"), listOf<IsobaricLayer>(
                    IsobaricLayer(
                        Pressure(600.0),
                        Temperature(-10.0),
                        uWind = 3.645589828491211,
                        vWind = -5.9597625732421875,
                        Direction(
                            009.0
                        ),
                        Speed(6.986350630122777),
                        Distance(4267.0)
                    ),

                    IsobaricLayer(
                        Pressure(700.0),
                        Temperature(-5.0),
                        uWind = 3.645589828491211,
                        vWind = -5.9597625732421875,
                        Direction(199.0),
                        Speed(6.986350630122777),
                        Distance(3200.0)
                    ),

                    IsobaricLayer(
                        Pressure(750.0),
                        Temperature(-2.0),
                        uWind = 3.645589828491211,
                        vWind = -5.9597625732421875,
                        Direction(329.0),
                        Speed(6.986350630122777),
                        Distance(2134.0)
                    ),

                    IsobaricLayer(
                        Pressure(750.0),
                        Temperature(3.0),
                        uWind = 3.645589828491211,
                        vWind = -5.9597625732421875,
                        Direction(329.0),
                        Speed(6.986350630122777),
                        Distance(1067.0)
                    )
                )
            )
            val route = load { dummyData }
            _state.update { it.copy(route = route) }
        }
    }


    fun filterArrivalAirports(input: String) {
        _state.update { it.copy(arrivalAirportInput = input) }
        viewModelScope.launch {
            val result = airportRepository.search(input)
            _state.update { it.copy(airports = result) }
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