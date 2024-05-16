package no.uio.ifi.in2000.team18.airborn.data.datasource

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.team18.airborn.model.GribFile
import no.uio.ifi.in2000.team18.airborn.model.GribFiles
import ucar.nc2.dt.grid.GridDataset
import java.io.File
import javax.inject.Inject
import kotlin.io.path.createParentDirectories

class GribDataSource @Inject constructor(
    val client: HttpClient, @ApplicationContext val context: Context
) {
    suspend fun <T : Any> useGribFile(
        gribFile: GribFile, func: (dataset: GridDataset) -> T
    ) = withContext(Dispatchers.IO) {
        GridDataset.open(downloadGribFile(gribFile = gribFile).path).use(func)
    }


    private suspend fun downloadGribFile(gribFile: GribFile): File {
        val file = resolveFile(gribFile)
        if (!file.exists()) {
            Log.d("grib", "downloading GRIP file ${gribFile.uri}")
            file.writeBytes(client.get(gribFile.uri).body<ByteArray>())
        }
        return file
    }

    suspend fun availableGribFiles(): GribFiles {
        val url = "weatherapi/isobaricgrib/1.0/available.json?type=grib2"
        return client.get(url).body()
    }

    private fun resolveFile(gribFile: GribFile): File {
        val path = context.cacheDir.toPath().resolve("grib").resolve(gribFileName(gribFile))
        path.createParentDirectories()
        return path.toFile()
    }

    private fun gribFileName(gribFile: GribFile) =
        "${gribFile.params.area}-${gribFile.params.time}-${gribFile.updated}.grib".replace(':', '_')
}



