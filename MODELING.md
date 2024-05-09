**Sekvensdiagram: Valg av departure/arrival**

```mermaid
sequenceDiagram
    actor Bruker
    
    Bruker->>+ HomeScreen: Starts app
    activate HomeScreen


    HomeScreen-->> Bruker: Show homescreen
    Bruker->> HomeScreen: Writes "ENG" in departure input field


    HomeScreen->> HomeViewModel: filterDepartureAirports("ENG")
    activate HomeViewModel
    HomeViewModel->> AirportRepository: search("ENG)
    activate AirportRepository
    AirportRepository->> AirportDataSource: search("ENG")
    activate AirportDataSource


    AirportDataSource -->> AirportRepository: Filtered airports
    AirportRepository -->> HomeViewModel: Filtered airports
    HomeViewModel -->> HomeScreen: Filtered airports
    HomeScreen -->> Bruker: Show filtered airports to user


    Bruker->> HomeScreen: Choose departure "Gardemoen, ENGM"
    

    Bruker->> HomeScreen: Clicks "ENAL" on map
    HomeScreen -->> Bruker: Show InfoBox for ENAL with Sun loading bar


    HomeScreen ->> HomeViewModel: updateSunriseSunset(ENAL)
    HomeViewModel ->> AirportRepository: fetchSunriseSunset (ENAL)
    AirportRepository ->> SunriseSunsetDataSource: fetchSunriseSunset(ENAL)
    activate SunriseSunsetDataSource
    SunriseSunsetDataSource ->> SunriseSunsetDataSource: fetch from sunrise api

    alt Error
        SunriseSunsetDataSource -->> AirportRepository: Failed to load Sun
        AirportRepository -->> HomeViewModel: Failed to load Sun
        HomeViewModel -->> HomeScreen: Failed to load Sun
        HomeScreen -->> Bruker: Show Error message (failed to laod sun) in InfoBox

    else Succsess
        SunriseSunsetDataSource -->> AirportRepository: SunriseSunset
        AirportRepository -->> HomeViewModel: SunriseSunset
        HomeViewModel -->> HomeScreen: SunriseSunset

        HomeScreen -->> Bruker: Show Sundata in InfoBox 
    end

    Bruker->> HomeScreen: Clicks add arrival button in "ENAL" Infobox


    deactivate HomeViewModel
    deactivate AirportRepository
    deactivate AirportDataSource
    deactivate HomeScreen
    deactivate SunriseSunsetDataSource
```

Tekstlig beskrivelse:
**Navn:** Valg av departure og arrival airport
**Aktør:** Småflypilot
**Prebetingelser:** Ingen prebetingelser
**Postbetingelser:** Departure og arrival flyplass er valgt. Appen er klar til å opprette
flightbrief
**Hovedflyt:**

1.User starts app
2.Appen viser HomeScreen
3.Piloten skrive inn "ENG" i departure input feltet
4.filterDepartureAirports funksjon blir kalt med "ENG" argument
5.search("ENG") i airportRepository blir kalt
6.search funksjonen i AirportDataSource blir kalt og returnerer flyplasser filtrert på "ENG"
7.de filtrerte flyplassene returneres fra AirportRepository til HomeViewModel

8. searchResults i homeViewModel UiState oppdateres til de filtrerte flyplassene
9. De filtrerte flyplassene vises til bruker
10. Bruker velger "Oslo lufthavn, Gardemoen" som departure flyplass
11. Bruker trykker på "ENAL" i kartet
12. HomeScreen viser InfoBox Composable for Ålesund flyplass, ENAL. Med loading bar for soldata
13. HomeScreen kaller på updateSunriseSunset(ENAL) i AirportRepository
14. updateSunriseSunset kaller videre på fetchSunriseSunset i SunriseSunsetDataSource
15. fetchSunriseSunset henter data fra Sunrise api`et til MET og returnerer en SunriseSunset instans
16. SunriseSunset returneres til AirportRepository
17. AirportRepository returnerer SunriseSunset for ENAL til HomeViewModel
18. HomeViewModel oppdaterer sun i UiState
19. HomeScreen viser soldata for Ålesund, ENAL
20. Bruker trykker på add arrival knappen i InfoBoxen

**Alternativ flyt:**
15.1: fetchSunriseSunet klarer ikke å hente data fra api, returnerer error melding til
airportRepository
15.2: Feilmelding blir returnert til HomeViewModel. state sin sun blir satt til Error
15.3: Feilmelding vises til bruker i InfoBox

**Aktivitetsdiagram metar/taf**

```mermaid
flowchart TD;
    style A fill:#000,stroke:#fff,stroke-width:2px;
    style B fill:#228B22;
    style C fill:#ff7f0e;
    style D fill:#ff7f0e;
    style E fill:#228B22;
    style F fill:#228B22;
    style G fill:#ff7f0e;
    style H fill:#228B22;
    style I fill:#228B22;
    style J fill:#ff7f0e;
    style K fill:#228B22,stroke:#228B22,stroke-width:2px;
    style L fill:#000,stroke:#fff,stroke-width:2px;

    A((Start))
    A --> B(Homescreen)
    B --> C{Departure chosen?}
    C --> |no| B
    C --> |yes| D{Go to brief?}
    D --> |yes| E(Flightbriefscreen #departuretab)
    D --> |no| B
    E --> F(Show metar/taf)
    F --> G{airport has metar/taf?}
    G --> |no| H(Show nearby airports with metar/taf)
    H --> J{new airport selected?}
    J --> |no| H
    J --> |yes| K(Metar/taf with new airport data)
    K --> L((End))
    G --> |yes| I(Metar/Taf)
    I --> L
 ```