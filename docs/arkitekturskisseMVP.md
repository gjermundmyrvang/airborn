```mermaid
graph TD
    viewHome[HomeScreen]
    viewFlight[FlightBriefScreen]
    viewModelHome[HomeViewModel]
    viewModelFlight[FlightBriefViewModel]
    flightRepo[FlightBriefRepository]
    dataAirport[AirportDataSource]
    dataSigchart[SigchartDataSource]
    dataTurbulence[TurbulenceDataSource]
    dataMetarTaf[TafMetarDataSource]
    dataIsobaricGrib[GribDataSource]
    isobaricRepo[IsobaricRepository]
    dataForecast[LocationForecastDataSource]
    forecastRepo[LocationForecastRepository]

    subgraph view[View]
        viewHome
        viewFlight
    end

    subgraph viewmodel[ViewModel]
        viewModelHome
        viewModelFlight
    end

    subgraph data[Data]
        flightRepo
        isobaricRepo
        dataAirport
        dataSigchart
        dataTurbulence
        dataMetarTaf
        dataIsobaricGrib
        dataForecast
        forecastRepo
    end

    viewHome --> viewModelHome
    viewFlight --> viewModelFlight
    viewModelHome -->|Search flights| dataAirport
    viewModelHome -->|createFlightBrief| flightRepo
    viewModelFlight --> flightRepo
    flightRepo --> dataAirport
    flightRepo --> dataSigchart
    flightRepo --> dataTurbulence
    flightRepo --> dataMetarTaf
    flightRepo --> isobaricRepo
    flightRepo --> forecastRepo
    isobaricRepo --> dataIsobaricGrib
    forecastRepo --> dataForecast
    classDef viewColor fill: #3CDA84
    classDef viewModelColor fill: #4383F2
    classDef dataColor fill: #082E42
    classDef title font-size: 22px, color: #FAFAFA
    class view title
    class viewmodel title
    class data title
    class data paddingData
    class view viewColor
    class viewmodel viewModelColor
    class data dataColor
```
