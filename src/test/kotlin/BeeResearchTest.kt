import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File
import javax.imageio.ImageIO

internal class BeeResearchTest {
    private val invalid = ImageIO.read(File("testinvalidresearch.png"))!!
    private val potential = ImageIO.read(File("testpotentialresearch.png"))!!
    private val finished = ImageIO.read(File("testfinished.png"))!!

    @Test
    fun isResearch() {
        assertTrue(BeeResearch.isResearch(invalid))
        assertTrue(BeeResearch.isResearch(potential))
        assertTrue(BeeResearch.isResearch(finished))
    }

    @Test
    fun hasBee() {
        assertTrue(BeeResearch.hasBee(invalid))
        assertTrue(BeeResearch.hasBee(potential))
        assertTrue(BeeResearch.hasBee(finished))
    }

    @Test
    fun getEmptyResearchSpots() {
        assertIterableEquals(
            listOf(0, 1, 2, 3, 4, 5, 7, 10, 11, 14, 16, 17, 18, 19, 20, 21),
            BeeResearch.getEmptyResearchSpots(invalid)
        )
        assertIterableEquals(
            listOf(0, 1, 2, 3, 4, 5, 7, 10, 11, 14, 16, 17, 18, 19, 20, 21),
            BeeResearch.getEmptyResearchSpots(potential)
        )
        assertIterableEquals(
            listOf(0, 1, 2, 3, 4, 5, 7, 10, 11, 14, 16, 17, 18, 19, 20, 21),
            BeeResearch.getEmptyResearchSpots(finished)
        )
    }

    @Test
    fun getActiveResearchSpots() {
        assertIterableEquals(
            listOf(6, 8, 9, 12, 13, 15),
            BeeResearch.getActiveResearchSpots(invalid)
        )
        assertIterableEquals(
            listOf(6, 8, 9, 12, 13, 15),
            BeeResearch.getActiveResearchSpots(potential)
        )
        assertIterableEquals(
            listOf(6, 8, 9, 12, 13, 15),
            BeeResearch.getActiveResearchSpots(finished)
        )
    }

    @Test
    fun getUnselectedResearchSpots() {
        assertIterableEquals(
            listOf(6, 8, 12, 15),
            BeeResearch.getUnselectedResearchSpots(invalid)
        )
        assertIterableEquals(
            listOf(8, 9, 12, 13, 15),
            BeeResearch.getUnselectedResearchSpots(potential)
        )
        assertTrue(BeeResearch.getUnselectedResearchSpots(finished).isEmpty())
    }

    @Test
    fun getSelectedResearchSpots() {
        assertIterableEquals(
            listOf(9, 13),
            BeeResearch.getSelectedResearchSpots(invalid)
        )
        assertIterableEquals(
            listOf(6),
            BeeResearch.getSelectedResearchSpots(potential)
        )
        assertIterableEquals(
            listOf(6, 8, 9, 12, 13, 15),
            BeeResearch.getSelectedResearchSpots(finished)
        )
    }

    @Test
    fun isInvaldResearch() {
        assertTrue(BeeResearch.isInvaldResearch(invalid))
        assertFalse(BeeResearch.isInvaldResearch(potential))
        assertFalse(BeeResearch.isInvaldResearch(finished))
    }

    @Test
    fun isFinishedResearch() {
        assertFalse(BeeResearch.isFinishedResearch(invalid))
        assertFalse(BeeResearch.isFinishedResearch(potential))
        assertTrue(BeeResearch.isFinishedResearch(finished))
    }

    @Test
    fun compareCell() {
        assertTrue(BeeResearch.compareCell(0, invalid, BeeResearch.emptyCell))
        assertTrue(BeeResearch.compareCell(6, invalid, BeeResearch.unselectedCell))
        assertTrue(BeeResearch.compareCell(9, invalid, BeeResearch.invalidMask))

        assertTrue(BeeResearch.compareCell(0, potential, BeeResearch.emptyCell))
        assertFalse(BeeResearch.compareCell(6, potential, BeeResearch.unselectedCell))
        assertTrue(BeeResearch.compareCell(6, potential, BeeResearch.potentialMask))
    }
}