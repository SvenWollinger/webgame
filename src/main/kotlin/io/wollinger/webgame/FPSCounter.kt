package io.wollinger.webgame

import kotlin.js.Date

class FPSCounter {
    private var printFps = "Calculating fps..."
    private var fps = 0
    private var lastFPS = Date.now()

    fun frame() {
        val now = Date.now()
        if(lastFPS + 1000.0 < now) {
            printFps = "$fps FPS"
            lastFPS = now
            fps = 0
        }
        fps++
    }

    fun getString() = printFps
}