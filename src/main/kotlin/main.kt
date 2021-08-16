import org.jnativehook.GlobalScreen
import org.jnativehook.NativeHookException
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() {
    val logger: Logger = Logger.getLogger(GlobalScreen::class.java.getPackage().name)
    logger.level = Level.WARNING
    logger.useParentHandlers = false

    try {
        GlobalScreen.registerNativeHook()
    } catch(e: NativeHookException) {
        System.err.println("There was a problem registering the native hook.")
        System.err.println(e.message)

        exitProcess(1)
    }
    GlobalScreen.addNativeKeyListener(KeyListener())

    while (true) {
        println("How many successes do you want?")
        val line = readLine()
        if (line == null || line == "q") break
        BeeResearch.researchSuccessful(line.toInt())
    }

    exitProcess(0)

//    val oddActiveNodes = ImageIO.read(File("errors", "iterationnotresearch.png"))
//    println(oddActiveNodes.compareTo(BeeResearch.baseResearch))
//    println(BeeResearch.getActiveResearchSpots(oddActiveNodes).size)
}