package no.uio.ifi.in2000.team18.airborn

import no.uio.ifi.in2000.team18.airborn.data.repository.sigmet.ParseResult
import no.uio.ifi.in2000.team18.airborn.data.repository.sigmet.metsParser
import no.uio.ifi.in2000.team18.airborn.data.repository.sigmet.parseSigmet
import no.uio.ifi.in2000.team18.airborn.data.repository.sigmet.parseSigmets
import org.junit.Test

class SigmetParserTest {
    @Test
    fun testSigmetParseOk() {
        var source = """
            ZCZC
            WSNO31 ENMI 111536
            ENOR SIGMET M02 VALID 111600/112000 ENMI-
            ENOR POLARIS FIR SEV MTW FCST WI N5905 E00715 - N6205 E00800 - N6215 E00955 - N6115 E01125 - N5945 E01000 - N5905 E00850 - N5905 E00715 SFC/FL320 STNR NC=
        """.trimIndent()

        assert(parseSigmet(source) is ParseResult.Ok)
    }

    @Test
    fun testAirmetParseOk() {
        val source = """
            ZCZC
            WANO31 ENMI 111110
            ENOR AIRMET I05 VALID 111200/111600 ENMI-
            ENOR POLARIS FIR MOD ICE FCST WI N6815 E01115 - N7015 E01730 - N7005 E02245 - N6925 E02230 - N6745 E01600 - N6815 E01115 2500FT/FL100 STNR WKN=
        """.trimIndent()

        assert(parseSigmet(source) is ParseResult.Ok)
    }

    @Test
    fun `test sigmet with bodø FIR`() {
        val source = """
            ZCZC
            WSNO36 ENMI 211336
            ENOB SIGMET M01 VALID 211400/211800 ENMI-
            ENOB BODOE OCEANIC FIR SEV MTW FCST WI N7950 E01030 - N8000 E01730 - N7900 E02000 - N7900 E01100 - N7950 E01030 SFC/FL250 STNR INTSF=
        """.trimIndent()

        assert(parseSigmet(source) is ParseResult.Ok)
    }

    @Test
    fun `test multiple airmets and sigmets`() {
        val source = """
            ZCZC
            WSNO31 ENMI 111536
            ENOR SIGMET M02 VALID 111600/112000 ENMI-
            ENOR POLARIS FIR SEV MTW FCST WI N5905 E00715 - N6205 E00800 - N6215 E00955 - N6115 E01125 - N5945 E01000 - N5905 E00850 - N5905 E00715 SFC/FL320 STNR NC=
            
            ZCZC
            WANO31 ENMI 111110
            ENOR AIRMET I05 VALID 111200/111600 ENMI-
            ENOR POLARIS FIR MOD ICE FCST WI N6815 E01115 - N7015 E01730 - N7005 E02245 - N6925 E02230 - N6745 E01600 - N6815 E01115 2500FT/FL100 STNR WKN=
        """.trimIndent()

        val result = parseSigmets(source)
        println(metsParser.parse(source))
        assert(result.size == 2)
    }
}