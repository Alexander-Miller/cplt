import java.io.BufferedWriter
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val OP_INDEX = 3
const val TIME_INDEX = 4

fun main() {

    // cy:log (K): [PERFORMANCE1] Load Main Page: 0.3984000000059605 seconds

    val today = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:MM:ss").format(LocalDateTime.now())
    val logFile =
        Path.of("/home/am/Documents/git/credit-plus-performance-test/frontend/logs/Performance-Log-2022-10-06T12:02:16.735Z.txt")
    val logDir = logFile.parent
    val opMap = HashMap<String, MutableList<String>>()

    for (line in Files.lines(logFile)) {
        if (!line.contains("PERFORMANCE")) continue

        val splitLine = line.trim().split(" ")
        val op = splitLine[OP_INDEX]
        val time = splitLine[TIME_INDEX].take(5)

        opMap.compute(op) { _, list ->
            list?.apply { add(time) } ?: mutableListOf(time)
        }
    }

    for ((op, times) in opMap) {
        val opFile = Path.of(logDir.toString(), "$today--$op.txt").toFile()
        BufferedWriter(FileWriter(opFile)).use { writer ->
            for (time in times) {
                writer.write(time)
                writer.write("\n")
            }
        }
        println("Wrote ${opFile.absolutePath}")
    }

    println("Done!")
}