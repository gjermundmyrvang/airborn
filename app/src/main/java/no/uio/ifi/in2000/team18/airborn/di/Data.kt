package no.uio.ifi.in2000.team18.airborn.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.team18.airborn.data.AirportDataSource
import no.uio.ifi.in2000.team18.airborn.data.FlightbriefRepository
import no.uio.ifi.in2000.team18.airborn.data.IsobaricRepository
import no.uio.ifi.in2000.team18.airborn.data.SigchartDataSource
import no.uio.ifi.in2000.team18.airborn.data.TafmetarDataSource
import no.uio.ifi.in2000.team18.airborn.data.TurbulenceDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Data {
    @Provides
    @Singleton
    fun provideFlightbriefRepository(
        sigchartDataSource: SigchartDataSource,
        turbulenceDataSource: TurbulenceDataSource,
        tafmetarDataSource: TafmetarDataSource,
        airportDataSource: AirportDataSource,
        isobaricRepository: IsobaricRepository
    ): FlightbriefRepository =
        FlightbriefRepository(
            sigchartDataSource,
            turbulenceDataSource,
            tafmetarDataSource,
            airportDataSource,
            isobaricRepository
        )
}
