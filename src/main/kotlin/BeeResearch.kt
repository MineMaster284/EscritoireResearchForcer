import java.awt.*
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.concurrent.fixedRateTimer

object BeeResearch {
    private val robot = Robot().apply {
        autoDelay = 175
    }

    internal var working: Boolean = true

    private val baseResearch = ImageIO.read(File("baseimage.png"))
    private val noBee = ImageIO.read(File("nobee.png"))
    internal val emptyCell = ImageIO.read(File("emptycell.png"))
    internal val unselectedCell = ImageIO.read(File("unselectedresearch.png"))
    internal val invalidMask = ImageIO.read(File("invalidspotmask.png"))
    internal val potentialMask = ImageIO.read(File("potentialspotmask.png"))

    private val emptyItem = ImageIO.read(File("emptyitem.png"))
    internal val invalidItem = ImageIO.read(File("invaliditem.png"))
    internal val researchNote = ImageIO.read(File("researchnote.png"))
    internal val combOutlineMask = ImageIO.read(File("comboutlinemask.png"))

    private val researchPositions = listOf(
        86 to 10,
        170 to 10,
        254 to 10,
        128 to 36,
        212 to 36,
        86 to 62,
        170 to 62,
        254 to 62,
        128 to 88,
        212 to 88,
        86 to 114,
        254 to 114,
        128 to 140,
        212 to 140,
        86 to 166,
        170 to 166,
        254 to 166,
        128 to 192,
        212 to 192,
        86 to 218,
        170 to 218,
        254 to 218
    )
    private val itemPositions = listOf(
        16 to 84,
        16 to 120,
        16 to 156,
        16 to 192,
        16 to 228,
        336 to 156,
        372 to 156,
        336 to 192,
        372 to 192,
        336 to 228,
        372 to 228
    )

    private fun getResearchImage(): BufferedImage {
        moveToPosition(0, 0)
        val screenRect = Rectangle(789, 331, 420, 272)
        return robot.createScreenCapture(screenRect)
    }

    internal fun isResearch(image: BufferedImage): Boolean =
        image.compareTo(baseResearch)
    internal fun hasBee(image: BufferedImage): Boolean =
        !image.getSubimage(170, 114, 44, 44).compareTo(noBee)
    internal fun getEmptyResearchSpots(image: BufferedImage): List<Int> =
        researchPositions.indices.filter { compareCell(it, image, emptyCell) }
    internal fun getActiveResearchSpots(image: BufferedImage): List<Int> =
        (researchPositions.indices - getEmptyResearchSpots(image))
            .also { activeNodes ->
                val active = activeNodes.size
                if (active % 2 != 0) {
                    ImageIO.write(image, "png", File("errors", "oddactivenodes.png"))
                    error("Odd number of active research nodes: $active")
                }
            }
    internal fun getUnselectedResearchSpots(image: BufferedImage): List<Int> =
        getActiveResearchSpots(image).filter { compareCell(it, image, unselectedCell) }
    internal fun getSelectedResearchSpots(image: BufferedImage): List<Int> =
        getActiveResearchSpots(image) - getUnselectedResearchSpots(image)
    internal fun getResultSlots(image: BufferedImage): List<Int> =
        (5 until 11).filterNot { compareItem(it, image, emptyItem) }
    internal fun isInvaldResearch(image: BufferedImage): Boolean = !isResearch(image) ||
            !hasBee(image) ||
            getActiveResearchSpots(image).isEmpty() ||
            getSelectedResearchSpots(image).any { compareCell(it, image, invalidMask) }
    internal fun isFinishedResearch(image: BufferedImage): Boolean = !isInvaldResearch(image) &&
            getUnselectedResearchSpots(image).isEmpty()

    private fun attemptSingleResearch(): Pair<Boolean, BufferedImage> {
        var image = getResearchImage()
        while (working && !isInvaldResearch(image) && !isFinishedResearch(image)) {
            val unselectedCells = getUnselectedResearchSpots(image)
            selectCell(unselectedCells[0])
            selectCell(unselectedCells[1])
            image = getResearchImage()
        }
        return isFinishedResearch(image) to image
    }

    private fun researchUntilSuccessful(maxIterations: Int = 10): Boolean {
        var i = 0
        while (working && i++ < maxIterations) {
            val (successful, image) = attemptSingleResearch()
            if (successful) {
                println("Try $i succeeded.")
                gatherResults(image)
                useResearch()
                resetBee()
                return true
            }
            if (!isResearch(image)) {
                println("Iteration $i not research.")
                ImageIO.write(image, "png", File("errors", "iterationnotresearch.png"))
                return false
            }
            println("Try $i failed.")
            resetBee()
        }
        return false
    }

    fun researchSuccessful(number: Int) {
        working = true
        for (i in 0 until number) {
            if (working) {
                val successful = researchUntilSuccessful(Int.MAX_VALUE)
                if (!successful) println("Failed research ${i + 1}")
            } else {
                println("Broken after ${i - 1} / $number")
                break
            }
        }
    }

    private fun selectCell(index: Int) {
        moveToCell(index)
        robot.clickMouse(InputEvent.BUTTON1_DOWN_MASK)
    }
    private fun moveToCell(index: Int) {
        val (x, y) = researchPositions[index]
        moveToPosition(x + 22, y + 22)
    }
    private fun selectItem(index: Int) {
        moveToItem(index)
        robot.clickMouse(InputEvent.BUTTON1_DOWN_MASK)
    }
    private fun moveToItem(index: Int) {
        val (x, y) = itemPositions[index]
        moveToPosition(x + 16, y + 16)
    }
    private fun moveToBee() {
        moveToPosition(170 + 22, 114 + 22)
    }
    private fun moveToPosition(x: Int, y: Int) {
        robot.mouseMove(789 + x, 331 + y)
    }

    private fun resetBee() {
        moveToBee()
        robot.clickMouse(InputEvent.BUTTON1_DOWN_MASK)
        robot.clickMouse(InputEvent.BUTTON1_DOWN_MASK)
        robot.delay(175 * 2)
    }

    private fun gatherResults(image: BufferedImage) {
        robot.keyPress(KeyEvent.VK_SHIFT)
        getResultSlots(image).forEach { resultSlot ->
            selectItem(resultSlot)
        }
        robot.keyRelease(KeyEvent.VK_SHIFT)
    }

    private fun useResearch() {
        // Exit escritoire
        robot.clickKey(KeyEvent.VK_E)
        // Look up
        robot.mouseMove(999, 451)
        // Right-click
        robot.clickMouse(KeyEvent.BUTTON3_DOWN_MASK)
        // Look down
        robot.mouseMove(999, 651)
        // Open escritoire
        robot.clickMouse(KeyEvent.BUTTON3_DOWN_MASK)
    }

    internal fun compareCell(index: Int, image: BufferedImage, other: BufferedImage): Boolean {
        val (x, y) = researchPositions[index]
        return image.getSubimage(x, y, 44, 44).compareTo(other)
    }

    internal fun compareItem(index: Int, image: BufferedImage, other: BufferedImage): Boolean {
        val (x, y) = itemPositions[index]
        return image.getSubimage(x, y, 32, 32).compareTo(other)
    }
}
