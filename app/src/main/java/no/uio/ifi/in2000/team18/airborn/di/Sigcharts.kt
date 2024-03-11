package no.uio.ifi.in2000.team18.airborn.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.team18.airborn.data.SigchartDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Sigcharts {
    @Provides
    @Singleton
    fun provideSigchartDataSource(): SigchartDataSource =
        SigchartDataSource()
}