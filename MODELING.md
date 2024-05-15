# Use Case Diagram: Fastest Route

*Plan a flight aiming to find the fastest route according to winds aloft forecast at a certain
time.*

![Use case diagram](docs/use-case-winds-aloft.png)

## Textual description

### Actors

- Pilot of a small plane (the user)
- Norwegian Meteorological Institute (the API-provider)

### Main Flow

Pre-conditions:

- Pilot has chosen departure and arrival airports
- Pilot has generated brief and chosen the overall tab

Post-conditions:

- User has been presented to relevant winds aloft forecasts in order to plan the fastest flight
  route

Main Flow:

1. User chooses «Winds Aloft»
2. App displays flight direction (bearing) as a number and an icon pointing in the same direction
3. App also displays winds aloft forecast, provided by the Norwegian Meteorological Institute, for
   different heights halfway along the route. Direction of wind shows both as degrees and arrows,
   making it easily comparable to flight direction (see above).
4. User chooses another time stamp, according to time of planned flight, and pushes the Update
   button
5. App displays winds aloft forecast for new time, at same position as above
6. User chooses another point along the route by using the slider and pushing Update
7. App displays winds aloft forecast for new position, at same time as above
8. The last two steps will likely be repeated a couple of times for other points along the route
9. The user (the pilot) now has been presented forecast data from muliple points along the route,
   and will be able to consider approximately which height might be suitable for the fastest route
   at the planned flight time

Alternative Flow:

3. App displays error message if fetching forecast failed
4. User closes the Winds Aloft section
5. User opens the Winds Aloft section again (a bit later)
6. If this time fetching forecast succeeded, flow continues at main flow step 2

# Sekvensdiagram: Valg av departure/arrival

```mermaid
sequenceDiagram
    actor Bruker
    Bruker ->>+ HomeScreen: Starts app
    activate HomeScreen
    HomeScreen -->> Bruker: Show homescreen
    Bruker ->> HomeScreen: Writes "ENG" in departure input field
    HomeScreen ->> HomeViewModel: filterDepartureAirports("ENG")
    activate HomeViewModel
    HomeViewModel ->> AirportRepository: search("ENG)
    activate AirportRepository
    AirportRepository ->> AirportDataSource: search("ENG")
    activate AirportDataSource
    AirportDataSource -->> AirportRepository: Filtered airports
    AirportRepository -->> HomeViewModel: Filtered airports
    HomeViewModel -->> HomeScreen: Filtered airports
    HomeScreen -->> Bruker: Show filtered airports to user
    Bruker ->> HomeScreen: Choose departure "Gardemoen, ENGM"
    Bruker ->> HomeScreen: Clicks "ENAL" on map
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

    Bruker ->> HomeScreen: Clicks add arrival button in "ENAL" Infobox
    deactivate HomeViewModel
    deactivate AirportRepository
    deactivate AirportDataSource
    deactivate HomeScreen
    deactivate SunriseSunsetDataSource
```

**Tekstlig beskrivelse**\
**Navn:** Valg av departure og arrival airport\
**Aktør:** Småflypilot\
**Prebetingelser:** Ingen prebetingelser\
**Postbetingelser:** Departure og arrival flyplass er valgt. Appen er klar til å opprette\
flightbrief

## Hovedflyt:

1. User starts app
2. Appen viser HomeScreen
3. Piloten skrive inn "ENG" i departure input feltet
4. filterDepartureAirports funksjon blir kalt med "ENG" argument
5. search("ENG") i airportRepository blir kalt
6. search funksjonen i AirportDataSource blir kalt og returnerer flyplasser filtrert på "ENG"
7. de filtrerte flyplassene returneres fra AirportRepository til HomeViewModel
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

## Alternativ flyt:

15.1. fetchSunriseSunet klarer ikke å hente data fra api, returnerer error melding til
airportRepository\
15.2. Feilmelding blir returnert til HomeViewModel. state sin sun blir satt til Error\
15.3. Feilmelding vises til bruker i InfoBox\

# Klassediagram som reflekterer usecaset ovenfor

# Aktivitetsdiagram metar/taf

<svg aria-roledescription="classDiagram" role="graphics-document document" viewBox="0 -50 1382.265625 1253" style="max-width: 100%;" xmlns="http://www.w3.org/2000/svg" width="100%" id="graph-div" height="100%" xmlns:xlink="http://www.w3.org/1999/xlink"><style>
@import
url("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css");'</style><style>
#graph-div{font-family:"trebuchet ms",verdana,arial,sans-serif;font-size:16px;fill:#333;}#graph-div
.error-icon{fill:#552222;}#graph-div .error-text{fill:#552222;stroke:#552222;}#graph-div
.edge-thickness-normal{stroke-width:2px;}#graph-div .edge-thickness-thick{stroke-width:
3.5px;}#graph-div .edge-pattern-solid{stroke-dasharray:0;}#graph-div
.edge-pattern-dashed{stroke-dasharray:3;}#graph-div .edge-pattern-dotted{stroke-dasharray:
2;}#graph-div .marker{fill:#333333;stroke:#333333;}#graph-div .marker.cross{stroke:
#333333;}#graph-div svg{font-family:"trebuchet ms",verdana,arial,sans-serif;font-size:
16px;}#graph-div g.classGroup text{fill:#9370DB;stroke:none;font-family:"trebuchet ms"
,verdana,arial,sans-serif;font-size:10px;}#graph-div g.classGroup text .title{font-weight:
bolder;}#graph-div .nodeLabel,#graph-div .edgeLabel{color:#131300;}#graph-div .edgeLabel .label
rect{fill:#ECECFF;}#graph-div .label text{fill:#131300;}#graph-div .edgeLabel .label
span{background:#ECECFF;}#graph-div .classTitle{font-weight:bolder;}#graph-div .node rect,#graph-div
.node circle,#graph-div .node ellipse,#graph-div .node polygon,#graph-div .node path{fill:
#ECECFF;stroke:#9370DB;stroke-width:1px;}#graph-div .divider{stroke:#9370DB;stroke-width:
1;}#graph-div g.clickable{cursor:pointer;}#graph-div g.classGroup rect{fill:#ECECFF;stroke:
#9370DB;}#graph-div g.classGroup line{stroke:#9370DB;stroke-width:1;}#graph-div .classLabel
.box{stroke:none;stroke-width:0;fill:#ECECFF;opacity:0.5;}#graph-div .classLabel .label{fill:
#9370DB;font-size:10px;}#graph-div .relation{stroke:#333333;stroke-width:1;fill:none;}#graph-div
.dashed-line{stroke-dasharray:3;}#graph-div .dotted-line{stroke-dasharray:1 2;}#graph-div
#compositionStart,#graph-div .composition{fill:#333333!important;stroke:#333333!
important;stroke-width:1;}#graph-div #compositionEnd,#graph-div .composition{fill:#333333!
important;stroke:#333333!important;stroke-width:1;}#graph-div #dependencyStart,#graph-div
.dependency{fill:#333333!important;stroke:#333333!important;stroke-width:1;}#graph-div
#dependencyStart,#graph-div .dependency{fill:#333333!important;stroke:#333333!
important;stroke-width:1;}#graph-div #extensionStart,#graph-div .extension{fill:transparent!
important;stroke:#333333!important;stroke-width:1;}#graph-div #extensionEnd,#graph-div
.extension{fill:transparent!important;stroke:#333333!important;stroke-width:1;}#graph-div
#aggregationStart,#graph-div .aggregation{fill:transparent!important;stroke:#333333!
important;stroke-width:1;}#graph-div #aggregationEnd,#graph-div .aggregation{fill:transparent!
important;stroke:#333333!important;stroke-width:1;}#graph-div #lollipopStart,#graph-div
.lollipop{fill:#ECECFF!important;stroke:#333333!important;stroke-width:1;}#graph-div
#lollipopEnd,#graph-div .lollipop{fill:#ECECFF!important;stroke:#333333!important;stroke-width:
1;}#graph-div .edgeTerminals{font-size:11px;line-height:initial;}#graph-div
.classTitleText{text-anchor:middle;font-size:18px;fill:#333;}#graph-div :
root{--mermaid-font-family:"trebuchet ms"
,verdana,arial,sans-serif;}</style><g><defs><marker orient="auto" markerHeight="240" markerWidth="190" refY="7" refX="18" class="marker aggregation classDiagram" id="graph-div_classDiagram-aggregationStart"><path d="M 18,7 L9,13 L1,7 L9,1 Z"></path></marker></defs><defs><marker orient="auto" markerHeight="28" markerWidth="20" refY="7" refX="1" class="marker aggregation classDiagram" id="graph-div_classDiagram-aggregationEnd"><path d="M 18,7 L9,13 L1,7 L9,1 Z"></path></marker></defs><defs><marker orient="auto" markerHeight="240" markerWidth="190" refY="7" refX="18" class="marker extension classDiagram" id="graph-div_classDiagram-extensionStart"><path d="M 1,7 L18,13 V 1 Z"></path></marker></defs><defs><marker orient="auto" markerHeight="28" markerWidth="20" refY="7" refX="1" class="marker extension classDiagram" id="graph-div_classDiagram-extensionEnd"><path d="M 1,1 V 13 L18,7 Z"></path></marker></defs><defs><marker orient="auto" markerHeight="240" markerWidth="190" refY="7" refX="18" class="marker composition classDiagram" id="graph-div_classDiagram-compositionStart"><path d="M 18,7 L9,13 L1,7 L9,1 Z"></path></marker></defs><defs><marker orient="auto" markerHeight="28" markerWidth="20" refY="7" refX="1" class="marker composition classDiagram" id="graph-div_classDiagram-compositionEnd"><path d="M 18,7 L9,13 L1,7 L9,1 Z"></path></marker></defs><defs><marker orient="auto" markerHeight="240" markerWidth="190" refY="7" refX="6" class="marker dependency classDiagram" id="graph-div_classDiagram-dependencyStart"><path d="M 5,7 L9,13 L1,7 L9,1 Z"></path></marker></defs><defs><marker orient="auto" markerHeight="28" markerWidth="20" refY="7" refX="13" class="marker dependency classDiagram" id="graph-div_classDiagram-dependencyEnd"><path d="M 18,7 L9,13 L14,7 L9,1 Z"></path></marker></defs><defs><marker orient="auto" markerHeight="240" markerWidth="190" refY="7" refX="13" class="marker lollipop classDiagram" id="graph-div_classDiagram-lollipopStart"><circle r="6" cy="7" cx="7" fill="transparent" stroke="black"></circle></marker></defs><defs><marker orient="auto" markerHeight="240" markerWidth="190" refY="7" refX="1" class="marker lollipop classDiagram" id="graph-div_classDiagram-lollipopEnd"><circle r="6" cy="7" cx="7" fill="transparent" stroke="black"></circle></marker></defs><g class="root"><g class="clusters"></g><g class="edgePaths"><path marker-end="url(#graph-div_classDiagram-aggregationEnd)" style="fill:none" class="edge-pattern-solid relation" id="id_FlightBriefViewModel_WeatherRepository_1" d="M491.117,78.794L585.251,90.995C679.385,103.196,867.652,127.598,961.786,140.966C1055.92,154.333,1055.92,156.667,1055.92,157.833L1055.92,159"></path><path marker-end="url(#graph-div_classDiagram-aggregationEnd)" style="fill:none" class="edge-pattern-solid relation" id="id_FlightBriefViewModel_AirportRepository_2" d="M316.844,104.709L298.386,112.591C279.928,120.473,243.013,136.236,224.555,160.535C206.098,184.833,206.098,217.667,206.098,250.5C206.098,283.333,206.098,316.167,206.098,345.417C206.098,374.667,206.098,400.333,206.098,413.167L206.098,426"></path><path marker-end="url(#graph-div_classDiagram-aggregationEnd)" style="fill:none" class="edge-pattern-solid relation" id="id_RouteIsobaric_Airport_3" d="M426.777,527.307L388.331,544.422C349.884,561.538,272.991,595.769,234.628,614.059C196.265,632.348,196.432,634.697,196.516,635.871L196.599,637.045"></path><path marker-end="url(#graph-div_classDiagram-aggregationEnd)" style="fill:none" class="edge-pattern-solid relation" id="id_Airport_Position_4" d="M206.098,886L206.098,890.167C206.098,894.333,206.098,902.667,210.715,911.258C215.333,919.849,224.568,928.698,229.185,933.122L233.803,937.547"></path><path marker-end="url(#graph-div_classDiagram-compositionEnd)" style="fill:none" class="edge-pattern-solid relation" id="id_RouteIsobaric_IsobaricData_5" d="M596.629,559.584L610.85,571.32C625.072,583.056,653.514,606.528,667.736,624.097C681.957,641.667,681.957,653.333,681.957,659.167L681.957,665"></path><path marker-end="url(#graph-div_classDiagram-compositionEnd)" style="fill:none" class="edge-pattern-solid relation" id="id_IsobaricData_IsobaricLayer_6" d="M681.957,858L681.957,866.833C681.957,875.667,681.957,893.333,681.957,903.333C681.957,913.333,681.957,915.667,681.957,916.833L681.957,918"></path><path marker-end="url(#graph-div_classDiagram-compositionEnd)" style="fill:none" class="edge-pattern-solid relation" id="id_FlightBriefViewModel_FlightBriefViewModel-UIState_7" d="M396.939,127L396.446,131.167C395.953,135.333,394.967,143.667,394.474,153.667C393.98,163.667,393.98,175.333,393.98,181.167L393.98,187"></path><path marker-end="url(#graph-div_classDiagram-aggregationEnd)" style="fill:none" class="edge-pattern-solid relation" id="id_FlightBriefViewModel-UIState_RouteIsobaric_8" d="M393.98,296L393.98,304.833C393.98,313.667,393.98,331.333,397.52,344.391C401.059,357.448,408.138,365.897,411.678,370.121L415.217,374.345"></path><path style="fill:none" class="edge-pattern-solid relation" id="id_WeatherRepository_RouteIsobaric_9" d="M908.662,309.642L892.329,316.202C875.996,322.761,843.33,335.881,791.325,359.205C739.319,382.529,667.974,416.059,632.301,432.823L596.629,449.588"></path><path marker-end="url(#graph-div_classDiagram-aggregationEnd)" style="fill:none" class="edge-pattern-solid relation" id="id_WeatherRepository_GribDataSource_10" d="M983.876,324L979.792,328.167C975.708,332.333,967.539,340.667,963.455,353C959.371,365.333,959.371,381.667,959.371,389.833L959.371,398"></path><path marker-end="url(#graph-div_classDiagram-aggregationEnd)" style="fill:none" class="edge-pattern-solid relation" id="id_WeatherRepository_LocationForecastDataSource_11" d="M1201.72,324L1209.986,328.167C1218.251,332.333,1234.782,340.667,1243.047,357.667C1251.313,374.667,1251.313,400.333,1251.313,413.167L1251.313,426"></path><path marker-end="url(#graph-div_classDiagram-compositionEnd)" style="fill:none" class="edge-pattern-solid relation" id="id_GribDataSource_GribFile_12" d="M959.371,563L959.371,574.167C959.371,585.333,959.371,607.667,959.371,624.667C959.371,641.667,959.371,653.333,959.371,659.167L959.371,665"></path><path marker-end="url(#graph-div_classDiagram-compositionEnd)" style="fill:none" class="edge-pattern-solid relation" id="id_GribFile_GribFileParams_13" d="M959.371,858L959.371,866.833C959.371,875.667,959.371,893.333,959.371,915C959.371,936.667,959.371,962.333,959.371,975.167L959.371,988"></path><path style="fill:none" class="edge-pattern-solid relation" id="id_AirportRepository_Airport_14" d="M209.336,535L210.463,550.833C211.59,566.667,213.844,598.333,214.674,618.333C215.505,638.333,214.911,646.667,214.615,650.833L214.318,655"></path><path style="fill:none" class="edge-pattern-solid relation" id="id_RouteIsobaric_Position_15" d="M456.476,605L454.483,609.167C452.491,613.333,448.506,621.667,446.514,649.25C444.521,676.833,444.521,723.667,444.521,770.5C444.521,817.333,444.521,864.167,441.274,894.083C438.027,924,431.533,937,428.286,943.5L425.039,950"></path></g><g class="edgeLabels"><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g><g transform="translate(506.5439465316396, 95.91906071288298)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g></g><g transform="translate(1059.9957740348784, 153.5623716846294)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"></g><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g><g transform="translate(294.8589822793942, 97.7867379955473)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g></g><g transform="translate(216.09765812499992, 421.50000160714285)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"></g><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g><g transform="translate(404.6895398027158, 520.720662334808)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g></g><g transform="translate(211.07388857929837, 639.1354141024262)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"></g><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
2</span></div></foreignObject></g><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g><g transform="translate(195.07753047670568, 905.2688370739796)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g></g><g transform="translate(239.54159304829992, 922.0620053898898)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"></g><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g><g transform="translate(600.5789815384144, 582.2918458980844)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g></g><g transform="translate(691.957030625, 660.4999994642857)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"></g><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g><g transform="translate(666.957030625, 875.4999994642857)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g></g><g transform="translate(691.957030625, 913.4999994642857)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"></g><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
*</span></div></foreignObject></g><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g><g transform="translate(380.5135972907486, 143.14302958556044)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g></g><g transform="translate(403.980469375, 182.5000005357143)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"></g><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g><g transform="translate(378.980469375, 313.5000005357143)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g></g><g transform="translate(422.0356862569163, 360.09510225187836)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"></g><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g><g transform="translate(886.8325519506722, 302.244565368992)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g></g><g transform="translate(613.847042330107, 450.720433758573)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"></g><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g><g transform="translate(960.9137364136128, 325.9975577236451)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g></g><g transform="translate(969.3710918749999, 393.49999839285715)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"></g><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g><g transform="translate(1210.5949129504043, 345.2719170289519)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g></g><g transform="translate(1261.3125, 421.5)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"></g><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g><g transform="translate(944.3710918750002, 580.4999983928572)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g></g><g transform="translate(969.3710918749999, 660.4999983928572)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"></g><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">*</span></div></foreignObject></g><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g><g transform="translate(944.3710918750002, 875.4999983928572)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g></g><g transform="translate(969.3710918749999, 983.4999983928572)" class="edgeTerminals"><g transform="translate(0, 0)" class="inner"></g><foreignObject style="width: 9px; height: 12px;"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel">
1</span></div></foreignObject></g><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g><g class="edgeLabel"><g transform="translate(0, 0)" class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="edgeLabel"></span></div></foreignObject></g></g></g><g class="nodes"><g transform="translate(403.98046875, 67.5)" data-id="FlightBriefViewModel" data-node="true" id="classId-FlightBriefViewModel-39" class="node default"><rect height="119" width="174.2734375" y="-59.5" x="-87.13671875" class="outer title-state" style=""></rect><line y2="-23.5" y1="-23.5" x2="87.13671875" x1="-87.13671875" class="divider"></line><line y2="-7.5" y1="-7.5" x2="87.13671875" x1="-87.13671875" class="divider"></line><g class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel"></span></div></foreignObject><foreignObject transform="translate( -79.63671875, -52)" height="24" width="159.2734375" class="classTitle"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
FlightBriefViewModel</span></div></foreignObject><foreignObject transform="translate( -79.63671875, 0)" height="24" width="132.5625"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
initRouteIsobaric()</span></div></foreignObject><foreignObject transform="translate( -79.63671875, 28)" height="24" width="158.921875"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
changeRouteIsobaric()</span></div></foreignObject></g></g><g transform="translate(1055.919921875, 250.5)" data-id="WeatherRepository" data-node="true" id="classId-WeatherRepository-40" class="node default"><rect height="147" width="294.515625" y="-73.5" x="-147.2578125" class="outer title-state" style=""></rect><line y2="-37.5" y1="-37.5" x2="147.2578125" x1="-147.2578125" class="divider"></line><line y2="-21.5" y1="-21.5" x2="147.2578125" x1="-147.2578125" class="divider"></line><g class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel"></span></div></foreignObject><foreignObject transform="translate( -71.60546875, -66)" height="24" width="143.2109375" class="classTitle"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
WeatherRepository</span></div></foreignObject><foreignObject transform="translate( -139.7578125, -14)" height="24" width="254.265625"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
getRouteIsobaric() : :
RouteIsobaric</span></div></foreignObject><foreignObject transform="translate( -139.7578125, 14)" height="24" width="211.734375"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
calculateHeight() : :
Distance</span></div></foreignObject><foreignObject transform="translate( -139.7578125, 42)" height="24" width="279.515625"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
getAirPressureAtSeaLevel() : :
Pressure</span></div></foreignObject></g></g><g transform="translate(206.09765625, 489.5)" data-id="AirportRepository" data-node="true" id="classId-AirportRepository-41" class="node default"><rect height="91" width="177.6484375" y="-45.5" x="-88.82421875" class="outer title-state" style=""></rect><line y2="-9.5" y1="-9.5" x2="88.82421875" x1="-88.82421875" class="divider"></line><line y2="6.5" y1="6.5" x2="88.82421875" x1="-88.82421875" class="divider"></line><g class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel"></span></div></foreignObject><foreignObject transform="translate( -66.5390625, -38)" height="24" width="133.078125" class="classTitle"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
AirportRepository</span></div></foreignObject><foreignObject transform="translate( -81.32421875, 14)" height="24" width="162.6484375"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
getByIcao() : :
Airport?</span></div></foreignObject></g></g><g transform="translate(511.703125, 489.5)" data-id="RouteIsobaric" data-node="true" id="classId-RouteIsobaric-42" class="node default"><rect height="231" width="169.8515625" y="-115.5" x="-84.92578125" class="outer title-state" style=""></rect><line y2="-79.5" y1="-79.5" x2="84.92578125" x1="-84.92578125" class="divider"></line><line y2="104.5" y1="104.5" x2="84.92578125" x1="-84.92578125" class="divider"></line><g class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel"></span></div></foreignObject><foreignObject transform="translate( -50.921875, -108)" height="24" width="101.84375" class="classTitle"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
RouteIsobaric</span></div></foreignObject><foreignObject transform="translate( -77.42578125, -68)" height="24" width="131.3046875"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
departure:
Airport</span></div></foreignObject><foreignObject transform="translate( -77.42578125, -40)" height="24" width="80.9375"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
arr:
Airport</span></div></foreignObject><foreignObject transform="translate( -77.42578125, -12)" height="24" width="154.8515625"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
isobaric:
IsobaricData</span></div></foreignObject><foreignObject transform="translate( -77.42578125, 16)" height="24" width="131.78125"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
distance:
Distance</span></div></foreignObject><foreignObject transform="translate( -77.42578125, 44)" height="24" width="129.7734375"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
bearing:
Direction</span></div></foreignObject><foreignObject transform="translate( -77.42578125, 72)" height="24" width="142.875"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
currentPos:
Position</span></div></foreignObject></g></g><g transform="translate(206.09765625, 770.5)" data-id="Airport" data-node="true" id="classId-Airport-43" class="node default"><rect height="231" width="396.1953125" y="-115.5" x="-198.09765625" class="outer title-state" style=""></rect><line y2="-79.5" y1="-79.5" x2="198.09765625" x1="-198.09765625" class="divider"></line><line y2="48.5" y1="48.5" x2="198.09765625" x1="-198.09765625" class="divider"></line><g class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel"></span></div></foreignObject><foreignObject transform="translate( -26.6484375, -108)" height="24" width="53.296875" class="classTitle"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
Airport</span></div></foreignObject><foreignObject transform="translate( -190.59765625, -68)" height="24" width="69.5390625"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
icao:
Icao</span></div></foreignObject><foreignObject transform="translate( -190.59765625, -40)" height="24" width="91.4453125"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
name:
String</span></div></foreignObject><foreignObject transform="translate( -190.59765625, -12)" height="24" width="123.5"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
position:
Position</span></div></foreignObject><foreignObject transform="translate( -190.59765625, 16)" height="24" width="146.375"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
isFavourite:
Boolean</span></div></foreignObject><foreignObject transform="translate( -190.59765625, 56)" height="24" width="381.1953125"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
fromBuiltinAirport(airport: BuiltinAirport) : ::
Airport</span></div></foreignObject><foreignObject transform="translate( -190.59765625, 84)" height="24" width="136.078125"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
toString() : :
String</span></div></foreignObject></g></g><g transform="translate(367.33984375, 1065.5)" data-id="Position" data-node="true" id="classId-Position-44" class="node default"><rect height="231" width="245.2734375" y="-115.5" x="-122.63671875" class="outer title-state" style=""></rect><line y2="-79.5" y1="-79.5" x2="122.63671875" x1="-122.63671875" class="divider"></line><line y2="-7.5" y1="-7.5" x2="122.63671875" x1="-122.63671875" class="divider"></line><g class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel"></span></div></foreignObject><foreignObject transform="translate( -29.48828125, -108)" height="24" width="58.9765625" class="classTitle"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
Position</span></div></foreignObject><foreignObject transform="translate( -115.13671875, -68)" height="24" width="103.90625"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
latitude:
Float</span></div></foreignObject><foreignObject transform="translate( -115.13671875, -40)" height="24" width="114.515625"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
longitude:
Float</span></div></foreignObject><foreignObject transform="translate( -115.13671875, 0)" height="24" width="165.859375"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
halfwayTo() : :
Position</span></div></foreignObject><foreignObject transform="translate( -115.13671875, 28)" height="24" width="174.9296875"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
distanceTo() : :
Distance</span></div></foreignObject><foreignObject transform="translate( -115.13671875, 56)" height="24" width="172.921875"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
bearingTo() : :
Direction</span></div></foreignObject><foreignObject transform="translate( -115.13671875, 84)" height="24" width="230.2734375"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
getPointAtDistance() : :
Position</span></div></foreignObject></g></g><g transform="translate(681.95703125, 770.5)" data-id="IsobaricData" data-node="true" id="classId-IsobaricData-45" class="node default"><rect height="175" width="250.5078125" y="-87.5" x="-125.25390625" class="outer title-state" style=""></rect><line y2="-51.5" y1="-51.5" x2="125.25390625" x1="-125.25390625" class="divider"></line><line y2="20.5" y1="20.5" x2="125.25390625" x1="-125.25390625" class="divider"></line><g class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel"></span></div></foreignObject><foreignObject transform="translate( -45.84765625, -80)" height="24" width="91.6953125" class="classTitle"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
IsobaricData</span></div></foreignObject><foreignObject transform="translate( -117.75390625, -40)" height="24" width="129.375"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
position:
Position,</span></div></foreignObject><foreignObject transform="translate( -117.75390625, -12)" height="24" width="161.7421875"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
time:
ZonedDateTime,</span></div></foreignObject><foreignObject transform="translate( -117.75390625, 28)" height="24" width="196.2734375"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
data: List(
IsobaricLayer) : ,</span></div></foreignObject><foreignObject transform="translate( -117.75390625, 56)" height="24" width="235.5078125"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
timeSeries: List(
ZonedDateTime)</span></div></foreignObject></g></g><g transform="translate(681.95703125, 1065.5)" data-id="IsobaricLayer" data-node="true" id="classId-IsobaricLayer-46" class="node default"><rect height="259" width="283.9609375" y="-129.5" x="-141.98046875" class="outer title-state" style=""></rect><line y2="-93.5" y1="-93.5" x2="141.98046875" x1="-141.98046875" class="divider"></line><line y2="118.5" y1="118.5" x2="141.98046875" x1="-141.98046875" class="divider"></line><g class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel"></span></div></foreignObject><foreignObject transform="translate( -49.98046875, -122)" height="24" width="99.9609375" class="classTitle"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
IsobaricLayer</span></div></foreignObject><foreignObject transform="translate( -134.48046875, -82)" height="24" width="136.828125"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
pressure:
Pressure,</span></div></foreignObject><foreignObject transform="translate( -134.48046875, -54)" height="24" width="198.53125"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
temperature:
Temperature,</span></div></foreignObject><foreignObject transform="translate( -134.48046875, -26)" height="24" width="110.4453125"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
uWind:
Double,</span></div></foreignObject><foreignObject transform="translate( -134.48046875, 2)" height="24" width="109.5390625"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
vWind:
Double,</span></div></foreignObject><foreignObject transform="translate( -134.48046875, 30)" height="24" width="268.9609375"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
windFromDirection: Direction? =
null,</span></div></foreignObject><foreignObject transform="translate( -134.48046875, 58)" height="24" width="187.4765625"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
windSpeed: Speed? =
null,</span></div></foreignObject><foreignObject transform="translate( -134.48046875, 86)" height="24" width="167.6640625"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
height: Distance? =
null</span></div></foreignObject></g></g><g transform="translate(393.98046875, 250.5)" data-id="FlightBriefViewModel-UIState" data-node="true" id="classId-FlightBriefViewModel-UIState-47" class="node default"><rect height="91" width="234.03125" y="-45.5" x="-117.015625" class="outer title-state" style=""></rect><line y2="-9.5" y1="-9.5" x2="117.015625" x1="-117.015625" class="divider"></line><line y2="34.5" y1="34.5" x2="117.015625" x1="-117.015625" class="divider"></line><g class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel"></span></div></foreignObject><foreignObject transform="translate( -109.515625, -38)" height="24" width="219.03125" class="classTitle"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
FlightBriefViewModel-UIState</span></div></foreignObject><foreignObject transform="translate( -109.515625, 2)" height="24" width="202.53125"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
routeIsobaric :
LoadingState</span></div></foreignObject></g></g><g transform="translate(959.37109375, 489.5)" data-id="GribDataSource" data-node="true" id="classId-GribDataSource-48" class="node default"><rect height="147" width="237.9765625" y="-73.5" x="-118.98828125" class="outer title-state" style=""></rect><line y2="-37.5" y1="-37.5" x2="118.98828125" x1="-118.98828125" class="divider"></line><line y2="-21.5" y1="-21.5" x2="118.98828125" x1="-118.98828125" class="divider"></line><g class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel"></span></div></foreignObject><foreignObject transform="translate( -58.1171875, -66)" height="24" width="116.234375" class="classTitle"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
GribDataSource</span></div></foreignObject><foreignObject transform="translate( -111.48828125, -14)" height="24" width="157.9921875"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
useGribFiles() : :
Type</span></div></foreignObject><foreignObject transform="translate( -111.48828125, 14)" height="24" width="222.9765625"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
availableGribFiles() : :
GribFile</span></div></foreignObject><foreignObject transform="translate( -111.48828125, 42)" height="24" width="190.0625"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
downloadGribFile() : :
File</span></div></foreignObject></g></g><g transform="translate(1251.3125, 489.5)" data-id="LocationForecastDataSource" data-node="true" id="classId-LocationForecastDataSource-49" class="node default"><rect height="91" width="245.90625" y="-45.5" x="-122.953125" class="outer title-state" style=""></rect><line y2="-9.5" y1="-9.5" x2="122.953125" x1="-122.953125" class="divider"></line><line y2="6.5" y1="6.5" x2="122.953125" x1="-122.953125" class="divider"></line><g class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel"></span></div></foreignObject><foreignObject transform="translate( -106.2109375, -38)" height="24" width="212.421875" class="classTitle"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
LocationForecastDataSource</span></div></foreignObject><foreignObject transform="translate( -115.453125, 14)" height="24" width="230.90625"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
fetchForecast() : :
LocationData</span></div></foreignObject></g></g><g transform="translate(959.37109375, 770.5)" data-id="GribFile" data-node="true" id="classId-GribFile-50" class="node default"><rect height="175" width="191.15625" y="-87.5" x="-95.578125" class="outer title-state" style=""></rect><line y2="-51.5" y1="-51.5" x2="95.578125" x1="-95.578125" class="divider"></line><line y2="76.5" y1="76.5" x2="95.578125" x1="-95.578125" class="divider"></line><g class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel"></span></div></foreignObject><foreignObject transform="translate( -29.83984375, -80)" height="24" width="59.6796875" class="classTitle"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
GribFile</span></div></foreignObject><foreignObject transform="translate( -88.078125, -40)" height="24" width="121.6953125"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
endpoint:
String,</span></div></foreignObject><foreignObject transform="translate( -88.078125, -12)" height="24" width="176.15625"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
params:
GribFileParams,</span></div></foreignObject><foreignObject transform="translate( -88.078125, 16)" height="24" width="117.125"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
updated:
String,</span></div></foreignObject><foreignObject transform="translate( -88.078125, 44)" height="24" width="71.8125"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
uri:
String</span></div></foreignObject></g></g><g transform="translate(959.37109375, 1065.5)" data-id="GribFileParams" data-node="true" id="classId-GribFileParams-51" class="node default"><rect height="119" width="170.8671875" y="-59.5" x="-85.43359375" class="outer title-state" style=""></rect><line y2="-23.5" y1="-23.5" x2="85.43359375" x1="-85.43359375" class="divider"></line><line y2="48.5" y1="48.5" x2="85.43359375" x1="-85.43359375" class="divider"></line><g class="label"><foreignObject height="0" width="0"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel"></span></div></foreignObject><foreignObject transform="translate( -56.171875, -52)" height="24" width="112.34375" class="classTitle"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
GribFileParams</span></div></foreignObject><foreignObject transform="translate( -77.93359375, -12)" height="24" width="89.921875"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
area:
String,</span></div></foreignObject><foreignObject transform="translate( -77.93359375, 16)" height="24" width="155.8671875"><div style="display: inline-block; white-space: nowrap;" xmlns="http://www.w3.org/1999/xhtml"><span class="nodeLabel">
time:
ZonedDateTime</span></div></foreignObject></g></g></g></g></g><text class="classTitleText" y="-25" x="691.1328125">
Class Diagram Reflecting Use Case Fastest Route</text></svg>

# Aktivitetsdiagram Metar/Taf

```mermaid
flowchart TD
;
    style start fill: #000, stroke: #fff, stroke-width: 2px;
    style B fill: #228B22;
    style C fill: #1a8a99;
    style D fill: #1a8a99;
    style E fill: #228B22;
    style F fill: #1a8a99;
    style G fill: #ff7f0e;
    style H fill: #228B22;
    style I fill: #228B22;
    style J fill: #1a8a99;
    style K fill: #228B22, stroke: #228B22, stroke-width: 2px;
    style stop fill: #000, stroke: #fff, stroke-width: 2px;
    start((Start))
    start --> B(Homescreen)
    B --> C[/User chooses Departure/]
    C --> D[/User presses Go to brief/]
    D --> E(Flightbriefscreen #departuretab)
    E --> F[/Open metar/taf collapsible/]
    F --> G{airport has metar/taf?}
    G -->|no| H(Show nearby airports with metar/taf)
    H --> J[/User selects new airport/]
    J --> K(Metar/taf for selected airport)
    G -->|yes| I(Show Metar/Taf)
    K --> stop((End))
    I --> stop 
 ```

