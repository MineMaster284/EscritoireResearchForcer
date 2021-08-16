import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener

class KeyListener: NativeKeyListener {
    override fun nativeKeyTyped(event: NativeKeyEvent) = Unit

    override fun nativeKeyPressed(event: NativeKeyEvent) = Unit

    override fun nativeKeyReleased(event: NativeKeyEvent)
    {
        if (event.keyCode == NativeKeyEvent.VC_M) {
            BeeResearch.working = false
        }
    }
}
