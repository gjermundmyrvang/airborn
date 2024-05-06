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
