package no.uio.ifi.in2000.team18.airborn.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    data class UiState(
        val airportInput: String = "",
        val airports: List<String> = listOf("ENGM", "ENSB", "ENBR", "ENVA", "ENGB", "ENGC", "ENGD", "ENGE", "ENGF"),
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun filterAirports(input: String) {
        _state.update {
            it.copy(
                airportInput = input,
                airports = _state.value.airports.filter { airport ->
                    airport.startsWith(input, ignoreCase = true)
                },
            )
        }
    }

    fun selectAirport(airport: String) {
        _state.update {
            it.copy(airportInput = airport)
        }

    }
}