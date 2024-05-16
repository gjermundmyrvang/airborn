# App made by team 18 in IN2000 2024

## Team members

- Falk
- Terje
- Oscar
- Gjermund
- Ola
- Henrik

# Dokumentasjon

- [Modeller](MODELING.md)
- [Arkitekturskisser](ARCHITECTURE.md)

# Kjøre appen

Du kan laste ned appen fra [vår hjemmeside](https://airborn.jetlund.com/) eller bygge den selv, ved å kjøre

```sh
./gradlew assembleRelease
```

fra prosjektets rot-mappe. Dette forutsetter at du har installert android sdk og gradle.

# Biblioteker brukt

## AndroidX Room

- **Versjon:** 2.6.1
- [Android dokumentasjon](https://developer.android.com/training/data-storage/room)

  Androids persistensbibliotek, som gir et abstrakt lag over SQLite for å hjelpe til med
  databaseoperasjoner i Android.

## Accompanist Permissions

- **Versjon:** 0.24.13-rc
- [GitHub](https://github.com/google/accompanist/tree/main/permissions)

  Et bibliotek som tilbyr enkel tilgang til tilgangstillatelser på Android.

## Ktor

- **Versjon:** 2.3.9
- [Ktor Dokumentasjon](https://ktor.io/docs/client.html)

  Ktor er et asynkront nettverksbibliotek for å bygge klientside- og serversideapplikasjoner i
  Kotlin.

## Dagger Hilt

- **Versjon:** 2.49
- [Android dokumentasjon](https://developer.android.com/training/dependency-injection/hilt-android)

  Android Jetpack-bibliotek for enkel og konsistent injeksjon av avhengigheter i Android-apper.

## Coil Compose

- **Versjon:** 2.6.0
- [GitHub](https://coil-kt.github.io/coil/compose/)

  Et moderne, responsivt bildebehandlingsbibliotek for Android-apper.

## AndroidX Navigation Compose

- **Versjon:** 2.7.7
- [Android dokumentasjon](https://developer.android.com/develop/ui/compose/navigation)

  Android Jetpack-bibliotek for navigasjon i Compose-baserte Android-apper.

## Mapbox Compose Extension

- **Versjon:** 11.3.1
- [MapBox dokumentasjon](https://docs.mapbox.com/android/maps/guides/install/)

  Mapbox-biblioteket for integrasjon av Mapbox Maps i Compose-baserte Android-apper.

## Kotlinx Datetime

- **Versjon:** 0.6.0-RC.2
- [Dokumentasjon](https://github.com/Kotlin/kotlinx-datetime)

  Kotlin bibliotek for arbeid med dato og tid.

## Dokka Android Documentation Plugin

- **Versjon:** 1.9.20
- [GitHub](https://github.com/Kotlin/dokka)

  Dokka-plugin for å generere dokumentasjon for Android-prosjekter.

## Zoomable Imageview

- **Versjon:** 1.6.1
- [GitHub](https://github.com/usuiat/Zoomable)

  Et zoombart bildebibliotek for Android.

## NetCDF4

- **Versjon:** 4.5.5
- [NetCDF dokumentasjon](https://www.unidata.ucar.edu/software/netcdf/docs/index.html)

  NetCDF er et programvarebibliotek og et sett med dataformat som er utviklet for å bidra til å
  støtte nettklient-til-nettserver-samspill.
