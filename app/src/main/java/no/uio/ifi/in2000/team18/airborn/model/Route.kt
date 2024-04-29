package no.uio.ifi.in2000.team18.airborn.model

import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricPosition
import java.time.ZonedDateTime

class Route(
    val departure: Airport,
    val arrival: Airport,
    var positions: Map<Double, IsobaricPosition>? = null, // fraction of Route as Double
    var timeSeries: Map<ZonedDateTime, List<GribFile>>? = null,
    var availableGribFiles: List<GribFile>? = null,
    var position: IsobaricPosition? = null, // current Isobaric position in view
    var isobaric: IsobaricData? = null, // current Isobaric Data
) {
    fun initializePositions(posList: List<Position>) {
        val fractions = listOf(0.0, 0.25, 0.5, 0.75, 1.0)
        require(posList.size == fractions.size)
        positions = fractions.zip(posList.map { IsobaricPosition(it) }).toMap()
    }

    //TODO: this will arrange timeSeries by using availableGribFiles
    // and connect to ZonedDateTimes
    fun initializeTimeSeries() {}

}

/**
 * Follow a great-circle line between two positions.
 * https://www.movable-type.co.uk/scripts/latlong.html
 *
 * Warning: Exponential time complexity (!)
 *
 * Returns 1 + 2.pow(depth + 1) points along the route.
 * Example: depth 3 gives 17 points.
 *
 * @param depth Recursion depth as integer.
 * Depth zero returns list of start, midpoint and end positions.
 *
 * @return List of points evenly distributed along the route, including start and end points.
 */
fun findAlongRoute(start: Position, end: Position, depth: Int = 2): List<Position> {
    fun allButLastPosition(d: Int): List<Position> {
        val midpoint = start.halfwayTo(end)
        if (d <= 0) return listOf(start, midpoint)
        return allButLastPosition(d - 1) +
                allButLastPosition(d - 1)
    }
    return allButLastPosition(depth) + end
}
