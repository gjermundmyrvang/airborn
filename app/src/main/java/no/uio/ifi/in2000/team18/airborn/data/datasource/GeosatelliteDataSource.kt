package no.uio.ifi.in2000.team18.airborn.data.datasource

import javax.inject.Inject

class GeosatelliteDataSource @Inject constructor() {
    fun fetchGeosatelliteImage(): String =
        "https://api.met.no/weatherapi/geosatellite/1.4/europe.png"
}
