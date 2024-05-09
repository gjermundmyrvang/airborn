```mermaid
flowchart TD;
    style start fill:#000,stroke:#fff,stroke-width:2px;
    style B fill:#228B22;
    style C fill:#177578;
    style D fill:#177578;
    style E fill:#228B22;
    style F fill:#177578;
    style G fill:#ff7f0e;
    style H fill:#228B22;
    style I fill:#228B22;
    style J fill:#177578;
    style K fill:#228B22,stroke:#228B22,stroke-width:2px;
    style stop fill:#000,stroke:#fff,stroke-width:2px;

    start((Start))
    start --> B(Homescreen)
    B --> C[/User chooses Departure/]
    C --> D[/User presses Go to brief/]
    D --> E(Flightbriefscreen #departuretab)
    E --> F[/Open metar/taf collapsible/]
    F --> G{airport has metar/taf?}
    G --> |no| H(Show nearby airports with metar/taf)
    H --> J[/User selects new airport/]
    J --> K(Metar/taf for selected airport)
    G --> |yes| I(Show Metar/Taf)

    K --> stop((End))
    I --> stop 
 ```
