import java.awt.Robot
import java.awt.image.BufferedImage

fun BufferedImage.compareTo(other: BufferedImage) = width == other.width &&
        height == other.height &&
        (0 until width).any { x -> (0 until height).any { y -> (other.alphaRaster?.getSample(x, y, 0) ?: 255) != 0 } } &&
        (0 until width).all { x ->
            val mapped = (0 until height).map { y ->
                other.alphaRaster?.getSample(x, y, 0) == 0 ||
                        getRGB(x, y) == other.getRGB(x, y)
            }
            false !in mapped
        }

fun Robot.clickMouse(buttons: Int) {
    mousePress(buttons)
    mouseRelease(buttons)
}

fun Robot.clickKey(keycode: Int) {
    keyPress(keycode)
    keyRelease(keycode)
}
