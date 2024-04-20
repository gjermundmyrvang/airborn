package no.uio.ifi.in2000.team18.airborn

import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.ParseResult
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.parseMetar
import org.junit.Test

class MetarParserTest {
    @Test
    fun parseMetarWithRVR() {
        val source =
            "ENSS 152150Z 07014KT 1100 R33/P2000 -SN SCT005/// BKN015/// OVC038/// M01/M02 Q1009 RMK WIND 0500FT VRB04KT="
        val res = parseMetar(source)
        print(res)
        assert(res is ParseResult.Ok)
    }
}