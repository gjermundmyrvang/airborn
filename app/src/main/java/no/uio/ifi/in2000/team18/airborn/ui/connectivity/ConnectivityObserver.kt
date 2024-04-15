package no.uio.ifi.in2000.team18.airborn.ui.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status { Available, Unavailable, Losing, Lost }
}