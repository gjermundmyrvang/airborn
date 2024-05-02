package no.uio.ifi.in2000.team18.airborn.model

import android.util.Log
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.RouteProgress
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.getRouteProgress
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricPosition
import java.time.ZonedDateTime

class Route(
    val departure: Airport,
    val arrival: Airport,
    var positions: Map<RouteProgress, Position>? = null,
    var timeSeries: Map<ZonedDateTime, GribFile>? = null,
    var position: IsobaricPosition? = null, // current Isobaric position in view
    var isobaric: IsobaricData? = null, // current Isobaric Data
) {
    fun initializePositions(posList: List<Position>) {
        val fractions = getRouteProgress()
        require(posList.size == fractions.size)
        positions = fractions.zip(posList.map { it }).toMap()
        Log.d("Route", "Positions made: $positions")
    }
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
